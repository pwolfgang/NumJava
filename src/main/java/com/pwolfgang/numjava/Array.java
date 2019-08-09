/*
 * Copyright (C) 2019 Paul Wolfgang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.pwolfgang.numjava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.StringJoiner;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntBinaryOperator;

/**
 * This class is similar to the NumPY ndarray.
 *
 * @author Paul
 */
public class Array {

    int[] shape;
    int[] stride;
    int numDim;
    private Class<?> dataType;
    final int offset;
    Object data;

    /**
     * Create an Array object. 
     *
     * @param shape The shape (a java array of ints.)
     * @param stride The stride (a java array of ints.)
     * @param dataType The data type (primitive java class object)
     * @param offset Start index in the data arrayl
     * @param data A single dimension array of data values.
     */
    public Array(int[] shape, int[] stride, Class<?> dataType, int offset, Object data) {
        this.shape = shape;
        this.numDim = shape.length;
        this.stride = stride;
        this.dataType = dataType;
        this.offset = offset;
        this.data = data;
    }

    /**
     * Construct an Array from a Java Array of primitive types.
     *
     * @param data An Object reference to the source array.
     */
    public Array(Object data) {
        Class<?> dataClass = data.getClass();
        if (!dataClass.isArray()) {
            if (dataClass == Integer.class || dataClass == Float.class) {
                this.data = data;
                if (dataClass == Integer.class) {
                    this.dataType = int.class;
                } else {
                    this.dataType = float.class;
                }
                shape = new int[0];
                stride = new int[0];
                offset = 0;
                numDim = 0;
            } else {
                throw new IllegalArgumentException("Unrecognized data type");
            }
        } else {
            Class<?> parentClass = dataClass;
            Object currentLevel = data;
            List<Integer> sizes = new ArrayList<>();
            Class<?> componentType;
            while (true) {
                sizes.add(java.lang.reflect.Array.getLength(currentLevel));
                componentType = parentClass.getComponentType();
                if (!componentType.isArray()) {
                    break;
                }
                parentClass = componentType;
                currentLevel = java.lang.reflect.Array.get(currentLevel, 0);
            }
            if (!componentType.isPrimitive()) {
                throw new IllegalArgumentException(componentType + " is not a primitive type");
            }
            numDim = sizes.size();
            shape = new int[numDim];
            for (int i = 0; i < sizes.size(); i++) {
                shape[i] = sizes.get(i);
            }
            stride = new int[numDim];
            stride[numDim - 1] = 1;
            for (int i = numDim - 2; i >= 0; i--) {
                stride[i] = stride[i + 1] * shape[i + 1];
            }
            dataType = componentType;
            int totalSize = 1;
            for (int d : shape) {
                totalSize *= d;
            }
            this.data = java.lang.reflect.Array.newInstance(dataType, totalSize);
            offset = 0;
            copyData(data, 0);
        }
    }
    
    public static Array generate(DoubleSupplier supplier, int... shape) {
        Object data = java.lang.reflect.Array.newInstance(float.class, shape);
        IndexIterator itr = new IndexIterator(shape);
        while (itr.hasNext()) {
            int[] idx = itr.next();
            setData(data, supplier, idx);
        }
        return new Array(data);
    }
    
    private static void setData(Object data, DoubleSupplier supplier, int[] idx) {
        if (idx.length == 1) {
            java.lang.reflect.Array.setFloat(data, idx[0], (float)supplier.getAsDouble());
        } else {
            int firstIndex = idx[0];
            int[] remainingIndicies = new int[idx.length - 1];
            System.arraycopy(idx, 1, remainingIndicies, 0, idx.length-1);
            int numRows = java.lang.reflect.Array.getLength(data);
            for (int i = 0; i < numRows; i++) {
                Object row = java.lang.reflect.Array.get(data, i);
                setData(row, supplier, remainingIndicies);
            }
        }
    }
    
    public int size() {
        int s = 1;
        for (int d : shape) {
            s *= d;
        }
        return s;
    }
    
    /**
     * Make a copy of an array. If the source array is a subArray or a transpose 
     * the result will only contain the selected in row column order.
     */
    public static Array copyOf(Array source) {
        if (source.numDim == 0) {
            return new Array(source.data);
        }
        int sourceSize = 1;
        for (int d : source.shape) {
            sourceSize *= d;
        }
        if (sourceSize == java.lang.reflect.Array.getLength(source.data) 
                && source.shape[source.numDim-1] == 1) {
            Object copy = java.lang.reflect.Array.newInstance(source.dataType, sourceSize);
            System.arraycopy(source.data, 0, copy, 0, sourceSize);
            return new Array(source.shape, source.stride, source.dataType, source.offset, copy);
        }
        Object resultData = java.lang.reflect.Array.newInstance(source.dataType, sourceSize);
        IndexIterator itr = new IndexIterator(source.shape);
        for (int i = 0; i < sourceSize; i++) {
            int[] idx = itr.next();
            int index = source.computeIndex(idx);
            java.lang.reflect.Array.set(resultData, i, java.lang.reflect.Array.get(source.data, index));
        }
        int[] newStride = new int[source.numDim];
        newStride[source.numDim - 1] = 1;
        for (int i = source.numDim - 2; i >= 0; i--) {
            newStride[i] = newStride[i + 1] * source.shape[i + 1];
        }
        return new Array(source.shape, newStride, source.dataType, 0, resultData);
    }

    /**
     * Method to a row (may be multidimensional) from a Java array to the data
     * array.
     *
     * @param data The destination data array.
     * @param index The start index in this data array
     * @return Updated value of index.
     */
    private int copyData(Object data, int index) {
        if (data.getClass().getComponentType().isPrimitive()) {
            return copyRow(this.data, data, index);
        } else {
            int length = java.lang.reflect.Array.getLength(data);
            for (int i = 0; i < length; i++) {
                Object level = java.lang.reflect.Array.get(data, i);
                index = copyData(level, index);
            }
            return index;
        }
    }

    /**
     * Method to copy the actual single-dimension row segment.
     *
     * @param data The source array.
     * @param row The destination array.
     * @param index The start index in the source
     * @return updated value of source.
     */
    private int copyRow(Object data, Object row, int index) {
        int rowSize = java.lang.reflect.Array.getLength(row);
        System.arraycopy(row, 0, data, index, rowSize);
        return index + rowSize;
    }

    /**
     * Return the shape.
     *
     * @return the shape.
     */
    public int[] getShape() {
        return shape;
    }

    /**
     * Return the dataType
     *
     * @return the dataType
     */
    public Class<?> getDataType() {
        return dataType;
    }

    /**
     * Get an int value from the array.
     *
     * @param idx The index.
     * @return The value at this index
     * @throws ClassCastException if the dataType is not int.
     */
    public int getInt(int... idx) {
        if (numDim == 0) {
            return (Integer)data;
        }
        if (idx.length != numDim) {
            throw new IllegalArgumentException("Indices must match shape to get signle element");
        }
        int index = computeIndex(idx);
        return ((int[]) data)[index];
    }

    /**
     * Get a float value from the array.
     *
     * @param idx The index.
     * @return The value at this index
     * @throws ClassCastException if the dataType is not float.
     */
    public float getFloat(int... idx) {
        if (numDim == 0) {
            return (Float)data;
        }
        if (idx.length != numDim) {
            throw new IllegalArgumentException("Indices must match shape to get signle element");
        }
        int index = computeIndex(idx);
        return ((float[]) data)[index];
    }
    
    /**
     * Return the index of the maximum value within the flattened array.
     * @return The index of the maximum value.
     */
    public int argMax() {
        int start = offset;
        int idx[] = Arrays.copyOf(shape, shape.length);
        for (int i = 0; i < idx.length-1; i++) {
            idx[i]--;
        }
        int end = computeIndex(idx);
        if (dataType == int.class) {
            int[] intData = (int[])data;
            int maxIndex = 0;
            int maxV = intData[0];
            for (int i = start; i < end; i++) {
                if (intData[i] > maxV) {
                    maxV = intData[i];
                    maxIndex = i;
                }
            }
            return maxIndex - start;
        } else {
            float[] floatData = (float[])data;
            int maxIndex = 0;
            float maxV = floatData[0];
            for (int i = start; i < end; i++) {
                if (floatData[i] > maxV) {
                    maxV = floatData[i];
                    maxIndex = i;
                }
            }
            return maxIndex - start;  
        }
    }
    
    /**
     * Store a value in the array.
     * 
     * @param x The value to be stored
     * @param idx The index.
     * @return The modified array with the value set.
     */
    public Array set(Number x, int... idx) {
        if (numDim == 0) {
            data = x;
            return this;
        }
        if (idx.length != numDim) {
            throw new IllegalArgumentException("Indices must match shape to get signle element");
        }
        int index = computeIndex(idx);
        if (dataType == int.class) {
            ((int[])data)[index] = x.intValue();
        } else {
            ((float[])data)[index] = x.floatValue();
        }
        return this;
    }

   /**
     * Compute the linear offset into the internal data array.
     *
     * @param idx The index array
     * @return The index to the data array
     * @throws IllegalArgumentException if there are more indices than shapes.
     */
    private int computeIndex(int[] idx) 
            throws IllegalArgumentException {
        if (idx.length > numDim) {
            throw new IllegalArgumentException("Too many indices: "
                    + Arrays.asList(idx).toString()
                    + " shape: " + Arrays.asList(shape).toString());
        }
        int index = 0;
        for (int i = 0; i < idx.length; i++) {
            index += stride[i] * idx[i];
        }
        return index + offset;
    }
    
    private int computeLastIndex() {
        int index = 0;
        for (int i = 0; i < shape.length; i++) {
            index += stride[i] * (shape[i] - 1);
        }
        return index + offset;
    }

    /**
     * Get a sub-array (slice)
     *
     * @param idx the index array
     * @return A new Array object containing the sub-array
     * @throws IllegalArgumentException if the number of indices does not
     * address a slice.
     */
    public Array getSubArray(int... idx) {
        if (idx.length > numDim) {
            throw new IllegalArgumentException("Too many indices");
        }
        if (idx.length == numDim) {
            int index = computeIndex(idx);
            if (dataType == int.class) {
                return new Array(((int[])data)[index]);
            } else {
                return new Array(((float[])data)[index]);
            }
        }
        int[] idxPrime = new int[numDim];
        System.arraycopy(idx, 0, idxPrime, 0, idx.length);
        int newOffset = computeIndex(idxPrime);
        int deltaIndices = numDim - idx.length;
        int[] newShape = new int[deltaIndices];
        int[] newStride = new int[deltaIndices];
        for (int i = 0; i < deltaIndices; i++) {
            newShape[i] = shape[idx.length + i];
            newStride[i] = stride[idx.length + i];
        }
        return new Array(newShape, newStride, dataType, newOffset, data);
    }
    
    /**
     * Get subArray as a single row.
     * @param idx The index of the row
     * @return The selected subArray as a single row.
     */
    public Array getRow(int... idx) {
        Array subArray = getSubArray(idx);
        int newNumDim = subArray.numDim+1;
        int[] newShape = new int[newNumDim];
        int[] newStride = new int[newNumDim];
        System.arraycopy(subArray.shape, 0, newShape, 1, subArray.numDim);
        System.arraycopy(subArray.stride, 0, newStride, 1, subArray.numDim);
        newShape[0] = 1;
        int newStride0 = 1;
        for (int i = 1; i < newNumDim; i++) {
            newStride0 *= newShape[i];
        }
        newStride[0] = newStride0;
        subArray.stride = newStride;
        subArray.shape = newShape;
        subArray.numDim = newNumDim;
        return subArray;
    }
    
    /**
     * Get a range of rows. Creates an Array containing the selected rows
     * as defined by the range of the first index.
     * @param low The start index
     * @param high One past the last index
     * @return An Array view of the selected rows
     */
    public Array getRange(int low, int high) {
        int[] idx = new int[numDim];
        idx[0] = low;
        int numRows = high - low;
        int[] newShape = Arrays.copyOf(shape, numDim);
        newShape[0] = numRows;
        int newOffset = computeIndex(idx);
        return new Array(newShape, stride, dataType, newOffset, data);
    }

    /**
     * Return a transpose of this array.
     *
     * @return A transpose of this array.
     */
    public Array transpose() {
        if (numDim < 2) {
            return this;
        }
        int[] newShape = new int[numDim];
        int[] newStride = new int[numDim];
        for (int i = 0; i < numDim; i++) {
            newShape[i] = shape[numDim - i - 1];
            newStride[i] = stride[numDim - i - 1];
        }
        return new Array(newShape, newStride, dataType, offset, data);
    }

    /**
     * Return a re-shaped view of this array.
     *
     * @param newShape The new shape.
     * @return a reshaped view.
     * @throws IllegalArgumentException if this is a transposed Array.
     */
    public Array reShape(int... newShape) {
        if (stride[numDim - 1]!= 1) {
            throw new IllegalArgumentException("Cannot reshape a transposed array");            
        }
        int newNumDim = newShape.length;
        int[] newStride = new int[newNumDim];
        newStride[newNumDim - 1] = 1;
        for (int i = newNumDim - 2; i >= 0; i--) {
            newStride[i] = newStride[i + 1] * newShape[i + 1];
        }
        return new Array(newShape, newStride, dataType, offset, data);
    }
    
    /**
     * Multiply each member by a scalar value.
     * @param s The scalar value
     * @return A new Array with each element multiplied by the scalar value.
     */
    public Array mul(Number s) {
        return mul(new Array(s));
    }
    
    /**
     * Compute the dot product of this and other.
     * If this and other are one-dim, result is the inner-product.
     * If this and other are two-dim, result is matrix multiply.
     * If this or other are singletons, then result multiplying the singleton
     * by each member of the non-singleton.
     * If this is an n-dim array and other is a one-dim array, it is the
     * sum product over the last axis of this and other.
     * @param other
     * @return 
     */
    public Array dot(Array other) {
        if (numDim == 0) {
            return other.mul((Number) data);
        }
        if (other.numDim == 0) {
            return mul((Number)other.data);
        }
        if (shape.length == 1 && other.shape.length == 1) {
            return innerProduct(this, other);
        }
        if (numDim == 2 && other.numDim == 2) {
            if (shape[1] != other.shape[0]) {
                throw new IllegalArgumentException(
                        String.format("Cannot multiply %s array by %s array", 
                                Arrays.toString(shape), Arrays.toString(other.shape)));
            }
            return mmul(this, other);
        }
        if (other.numDim == 1) {
            return nDtimes1D(this, other);
        }
        return nDtimesMd(this, other);
    }
    
    static Array nDtimes1D(Array a, Array b) {
        int aNumDim = a.numDim;
        if (a.shape[aNumDim-1] != b.shape[0]) {
            throw new IllegalArgumentException(
            String.format("shapes %s and %s not alligned",
                    Arrays.toString(a.shape), Arrays.toString(b.shape)));
        }
        int[] resultShape = Arrays.copyOf(a.shape, aNumDim-1);
        Class<?> resultDataType;
        if (a.dataType == int.class && b.dataType == int.class) {
            resultDataType = int.class;
        } else {
            resultDataType = float.class;
        }
        Object result = java.lang.reflect.Array.newInstance(resultDataType, resultShape);
        Array resultArray = new Array(result);
        IndexIterator itr = new IndexIterator(resultShape);
        while (itr.hasNext()) {
            int[] idx = itr.next();
            Array x = a.getSubArray(idx).dot(b);
            resultArray.set((Number)x.data, idx);
        }
        return resultArray;
    }
    
    static Array nDtimesMd(Array a, Array b) {
        int aNumDim = a.numDim;
        int bNumDim = b.numDim;
        int aVlength = a.shape[aNumDim-1];
        int bVlength = b.shape[bNumDim-2];
        if (aVlength != bVlength) {
            throw new IllegalArgumentException(
            String.format("shapes %s and %s not alligned",
                    Arrays.toString(a.shape), Arrays.toString(b.shape)));
        }
        int[] aSubShape = Arrays.copyOf(a.shape, aNumDim-1);
        int[] bSubShape = new int[bNumDim-1];
        System.arraycopy(b.shape, 0, bSubShape, 0, bNumDim-1);
        bSubShape[bNumDim-2] = b.shape[bNumDim-1];
        int[] resultShape = new int[aNumDim + bNumDim - 2];
        System.arraycopy(aSubShape, 0, resultShape, 0, aNumDim-1);
        System.arraycopy(bSubShape, 0, resultShape, aNumDim-1, bNumDim-1);
        Class<?> resultDataType;
        if (a.dataType == int.class && b.dataType == int.class) {
            resultDataType = int.class;
        } else {
            resultDataType = float.class;
        }
        Object result = java.lang.reflect.Array.newInstance(resultDataType, resultShape);
        Array resultArray = new Array(result);
        IndexIterator itrA = new IndexIterator(aSubShape);
        while (itrA.hasNext()) {
            int[] idxA = itrA.next();
            Array row = a.getSubArray(idxA);
            IndexIterator itrB = new IndexIterator(bSubShape);
            while (itrB.hasNext()) {
                int[] idxB = itrB.next();
                Array col = getSecondToLast(b, idxB);
                int[] idxC = new int[resultArray.numDim];
                System.arraycopy(idxA, 0, idxC, 0, idxA.length);
                System.arraycopy(idxB, 0, idxC, idxA.length, idxB.length);
                Array x = row.dot(col);
                resultArray.set((Number)x.data, idxC);
            }
        }
        return resultArray;      
    }
    
    static Array getSecondToLast(Array b, int[] idx) {
        int[] subIndex = Arrays.copyOf(idx, idx.length-1);
        int i = idx[idx.length-1];
        return b.getSubArray(subIndex).transpose().getSubArray(i);
    }
    
    
    static Array mmul(Array a, Array b) {
        int nRows = a.shape[0];
        int nCols = b.shape[1];
        int[] resultShape = new int[]{nRows, nCols};
        int[] resultStride = new int[]{nCols, 1};
        int innerCount = a.shape[1];
        int aRowStride = a.stride[0];
        int aColStride = a.stride[1];
        int bRowStride = b.stride[0];
        int bColStride = b.stride[1];
        Object resultData = null;
        Class<?> resultDataType = null;
        if (a.dataType == int.class && b.dataType == int.class) {
            resultDataType = int.class;
            int[] aData = (int[])a.data;
            int[] bData = (int[])b.data;
            resultData = DotProduct.iXiMMUL(nRows, nCols, innerCount, a.offset, aColStride, b.offset, bRowStride, aData, bData, bColStride, aRowStride);
        } else if (a.dataType == float.class && b.dataType == float.class) {
            resultDataType = float.class;
            float[] aData = (float[])a.data;
            float[] bData = (float[])b.data;
            resultData = DotProduct.fXfMMUL(nRows, nCols, innerCount, a.offset, aColStride, b.offset, bRowStride, aData, bData, bColStride, aRowStride);
        } else {
            throw new RuntimeException("mixed mmul not allowed");
        }
        return new Array(resultShape, resultStride, resultDataType, 0, resultData);
    }

    
    private static Array innerProduct(Array left, Array right) {
        if (left.shape[0] != right.shape[0]) {
            throw new IllegalArgumentException("Arrays must be the same size");
        }
        int leftStride = left.stride[0];
        int leftLastIndex = leftStride * left.shape[0] + left.offset;
        int leftIndex = left.offset;
        int rightStride = right.stride[0];
        int rightLastIndex = rightStride * right.shape[0] + right.offset;
        int rightIndex = right.offset;
        if (left.dataType == int.class) {
            if (right.dataType == int.class) {
                return new Array(DotProduct.intXintInnerProduct(leftStride, leftLastIndex, leftIndex, 
                    rightStride, rightIndex, (int[])left.data, (int[])right.data));
            } else {
                return new Array(DotProduct.intXfloatInnerProduct(leftStride, leftLastIndex, leftIndex, 
                    rightStride, rightIndex, (int[])left.data, (float[])right.data));
            }
        } else if (left.dataType == float.class) {
            if (right.dataType == float.class) {
                return new Array(DotProduct.floatXfloatInnerProduct(leftStride, leftLastIndex, leftIndex, 
                    rightStride, rightIndex, (float[])left.data, (float[])right.data));                
            } else {
                return new Array(DotProduct.intXfloatInnerProduct(rightStride, rightLastIndex, rightIndex, 
                    leftStride, leftIndex, (int[])right.data, (float[])left.data));                
            }
        }
        throw new RuntimeException("Cannot Get Here");
    }
    
    
    
    public String toStringDebug() {
        String dataString;
        if (numDim == 0) {
            dataString = ((Number)data).toString();
        } else if (dataType == int.class) {
            dataString = Arrays.toString((int[])data);
        } else {
            dataString = Arrays.toString((float[])data);
        }
        return String.format("Shape: %s%nStride: %s%nOffset: %s%nDataType: %s%nData: %s%n",
                Arrays.toString(shape), Arrays.toString(stride), offset, 
                dataType.toString(), dataString);
    }

    @Override
    public String toString() {
        if (numDim == 0) {
            return ((Number)data).toString();
        }
        return toString(this);
    }
    
    private String toString(Array a) {
        if (a.numDim == 1) {
            StringJoiner sj = new StringJoiner(", ", "{", "}");
            int deltaIndex = a.stride[0];
            for (int i = a.offset; i < a.offset + a.shape[0]*deltaIndex; i += deltaIndex) {
                sj.add(java.lang.reflect.Array.get(a.data, i).toString());
            }
            return sj.toString();
        } else {
            int numRows = shape[0];
            StringJoiner sj = new StringJoiner(", ", "{", "}");
            for (int r = 0; r < numRows; r++) {
                Array row = a.getSubArray(r);
                sj.add(toString(row));
            }
            return sj.toString();
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() == o.getClass()) {
            Array other = (Array)o;
            if (this.dataType != other.dataType) return false;
            if (!Arrays.equals(this.shape, other.shape)) return false;
            if (!Arrays.equals(this.stride, other.stride)) return false;
            if (numDim == 0) {
                return this.data.equals(other.data);
            }
            int lastIndexLeft = this.computeLastIndex();
            int lastIndexRight = other.computeLastIndex();
            int firstIndexLeft = this.offset;
            int firstIndexRight = other.offset;
            if (dataType == int.class) {
                return equalInts((int[])this.data, firstIndexLeft, lastIndexLeft,
                        (int[])other.data, firstIndexRight, lastIndexRight);
            } else if (dataType == float.class) {
                return equalFloats((float[])this.data, firstIndexLeft, lastIndexLeft,
                        (float[])other.data, firstIndexRight, lastIndexRight);
            } else {
                throw new RuntimeException("Unrecognized datatype");
            }
        } else {
            return false;
        }
    }
    
    private boolean equalInts(int[] left, int indexLeft, int lastIndexLeft,
            int[] right, int indexRight, int lastIndexRight) {
            while(indexLeft < lastIndexLeft && indexRight < lastIndexRight) {
                if (left[indexLeft++] != right[indexRight++]) {
                    return false;
                }
            }
            return true;
    }

    private boolean equalFloats(float[] left, int indexLeft, int lastIndexLeft,
            float[] right, int indexRight, int lastIndexRight) {
            while(indexLeft < lastIndexLeft && indexRight < lastIndexRight) {
                if (left[indexLeft++] != right[indexRight++]) {
                    return false;
                }
            }
            return true;
    }
    
    private Array performIntOperation(Array other, IntBinaryOperator op) {
        Array result = copyOf(this);
        if (result.numDim == 0 && other.numDim == 0) {
            result.data = op.applyAsInt((Integer)result.data, (Integer)other.data);
        }
        if (other.numDim == 0) {
            int[] left = (int[])result.data;
            int right = other.getInt(0);
            for (int i = 0; i < left.length; i++) {
                left[i] = op.applyAsInt(left[i], right);
        }
            return result;
        }
        if (result.numDim == other.numDim) {
            if (!Arrays.equals(result.shape, other.shape)) {
                throw new IllegalArgumentException(
                String.format("this shape: %s is not equal to other shape: %s", 
                        Arrays.toString(this.shape), Arrays.toString(other.shape)));
            }
            performIntOperation(result, other, op);
            return result;
        }
        if (result.numDim < other.numDim) {
                throw new IllegalArgumentException(
                String.format("this shape: %s is not compatible with other shape: %s", 
                        Arrays.toString(this.shape), Arrays.toString(other.shape)));            
        }
        int deltaDim = result.numDim - other.numDim;
        int[] extraDims = new int[deltaDim];
        System.arraycopy(result.shape, 0, extraDims, 0, deltaDim);
        int[] matchingDims = new int[other.numDim];
        System.arraycopy(result.shape, deltaDim, matchingDims, 0, other.numDim);
        if (!Arrays.equals(matchingDims, other.shape)) {
                throw new IllegalArgumentException(
                String.format("this shape: %s is not compatible with other shape: %s", 
                        Arrays.toString(this.shape), Arrays.toString(other.shape)));                        
        }
        IndexIterator itr = new IndexIterator(extraDims);
        while (itr.hasNext()) {
            int[] idx = itr.next();
            Array subArray = result.getSubArray(idx);
            performIntOperation(subArray, other, op);
        }
        return result;
    }
    
    /**
     * Returns a modified copy of this Array after applying the supplied operator
     * to each value.
     * @param op The operatior to be applied
     * @return A modified Array
     */
    public Array apply(DoubleUnaryOperator op) {
        Array result = copyOf(this);
        if (result.dataType != float.class) {
            result.convertToFloat();
        }
        IndexIterator itr = new IndexIterator(result.shape);
        float[] floatData = (float[])result.data;
        while (itr.hasNext()) {
            int[] idx = itr.next();
            int index = result.computeIndex(idx);
            floatData[index] = (float)op.applyAsDouble(floatData[index]);
        }
        return result;
    }
       
    private void performIntOperation(Array left, Array right, IntBinaryOperator op) {
        IndexIterator itr1 = new IndexIterator(left.shape);
        IndexIterator itr2 = new IndexIterator(right.shape);
        int[] leftData = (int[])left.data;
        int[] rightData = (int[])right.data;
        while (itr1.hasNext()) {
            int[] idx1 = itr1.next();
            int[] idx2 = itr2.next();
            int index1 = left.computeIndex(idx1);
            int index2 = right.computeIndex(idx2);
            leftData[index1] = op.applyAsInt(leftData[index1], rightData[index2]);
        }
    }
    
    private Array performFloatOperation(Array other, DoubleBinaryOperator op) {
        Array result = copyOf(this);
        if (result.dataType != float.class) {
            result.convertToFloat();
        }
        if (other.dataType != float.class) {
            other = copyOf(other);
            other.convertToFloat();
        }
        if (result.numDim == 0 && other.numDim == 0) {
            result.data = (float)op.applyAsDouble((Float)result.data, (Float)other.data);
            return result;
        }
        if (other.numDim == 0) {
            float[] left = (float[])result.data;
            float right = other.getFloat(0);
            for (int i = 0; i < left.length; i++) {
                left[i] = (float)op.applyAsDouble(left[i], right);
        }
            return result;
        }
        if (result.numDim == other.numDim) {
            if (!Arrays.equals(result.shape, other.shape)) {
                throw new IllegalArgumentException(
                String.format("this shape: %s is not equal to other shape: %s", 
                        Arrays.toString(this.shape), Arrays.toString(other.shape)));
            }
            performFloatOperation(result, other, op);
            return result;
        }
        if (result.numDim < other.numDim) {
                throw new IllegalArgumentException(
                String.format("this shape: %s is not compatible with other shape: %s", 
                        Arrays.toString(this.shape), Arrays.toString(other.shape)));            
        }
        int deltaDim = result.numDim - other.numDim;
        int[] extraDims = new int[deltaDim];
        System.arraycopy(result.shape, 0, extraDims, 0, deltaDim);
        int[] matchingDims = new int[other.numDim];
        System.arraycopy(result.shape, deltaDim, matchingDims, 0, other.numDim);
        if (!Arrays.equals(matchingDims, other.shape)) {
                throw new IllegalArgumentException(
                String.format("this shape: %s is not compatible with other shape: %s", 
                        Arrays.toString(this.shape), Arrays.toString(other.shape)));                        
        }
        IndexIterator itr = new IndexIterator(extraDims);
        while (itr.hasNext()) {
            int[] idx = itr.next();
            Array subArray = result.getSubArray(idx);
            performFloatOperation(subArray, other, op);
        }
        return result;
    }
    
    private void performFloatOperation(Array left, Array right, DoubleBinaryOperator op) {
        IndexIterator itr1 = new IndexIterator(left.shape);
        IndexIterator itr2 = new IndexIterator(right.shape);
        float[] leftData = (float[])left.data;
        float[] rightData = (float[])right.data;
        while (itr1.hasNext()) {
            int[] idx1 = itr1.next();
            int[] idx2 = itr2.next();
            int index1 = left.computeIndex(idx1);
            int index2 = right.computeIndex(idx2);
            leftData[index1] = (float)op.applyAsDouble(leftData[index1], rightData[index2]);
        }
    }
    
    private void convertToFloat() {
        dataType =float.class;
        if (numDim == 0) {
            data = ((Number)data).floatValue();
        } else {
            int[] dataAsInt = (int[])data;
            float[] newData = new float[dataAsInt.length];
            for (int i = 0; i < dataAsInt.length; i++) {
                newData[i] = dataAsInt[i];
            }
            data = newData;
        }   
    }

    public Array sub(Array other) {
        if (this.dataType == int.class && other.dataType == int.class) {
            return performIntOperation(other, (int x, int y) -> x - y);
        } else {
            return performFloatOperation(other, (double x, double y) -> x - y);
        }
    }
    public Array add(Array other) {
        if (this.dataType == int.class && other.dataType == int.class) {
            return performIntOperation(other, (int x, int y) -> x + y);
        } else {
            return performFloatOperation(other, (double x, double y) -> x + y);
        }
    }
    public Array mul(Array other) {
        if (this.dataType == int.class && other.dataType == int.class) {
            return performIntOperation(other, (int x, int y) -> x * y);
        } else {
            return performFloatOperation(other, (double x, double y) -> x * y);
        }
    }
    public Array div(Array other) {
        if (this.dataType == int.class && other.dataType == int.class) {
            return performIntOperation(other, (int x, int y) -> x / y);
        } else {
            return performFloatOperation(other, (double x, double y) -> x / y);
        }
    }    
    
    /**
     * Return a PrimitiveIterator.OfDouble. If this is a
     * singleton, then the returned iterator::next method will return the value
     * cast as a double. If this is a single-dimension array, the iterator will 
     * iterate through the elements, casting them as a double. 
     * @return PrimitiveIterator.OfDouble
     * @throws IllegalArgumentException if there are more than 1 dimension
     */
    public PrimitiveIterator.OfDouble iterator() {
        if (numDim == 0) {
            return new SingletonIterator(this);
        }
        if (numDim != 1) {
            throw new IllegalArgumentException("Can only iterate over singelton or single-dim array");
        }
        if (dataType == int.class) {
            return new IteratorOverInt(this);
        }
        if (dataType == float.class) {
            return new IteratorOverFloat(this);
        }
        throw new IllegalArgumentException("Only arrays of int or float are supported");
    }
    
    private static class SingletonIterator implements PrimitiveIterator.OfDouble {
        
        private boolean nextCalled;
        private final Number data;
        
        public SingletonIterator(Array array) {
            nextCalled = false;
            data = (Number) array.data;
        }
        
        @Override
        public boolean hasNext() {
            return !nextCalled;
        }
        
        @Override
        public double nextDouble() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            nextCalled = true;
            return data.doubleValue();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class IteratorOverInt implements PrimitiveIterator.OfDouble {
        
        private final int stride;
        private final int lastIndex;
        private int index;
        private final int[] data;
    
        public IteratorOverInt(Array array) {
            this.stride = array.stride[0];
            this.index = array.offset;
            this.lastIndex = index + stride * array.shape[0];
            this.data = (int[])array.data;
        }
        
        @Override
        public boolean hasNext() {
            return index < lastIndex;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public double nextDouble() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            double value = (double)data[index];
            index += stride;
            return value;
        }
    }

    private static class IteratorOverFloat implements PrimitiveIterator.OfDouble {
        
        private final int stride;
        private final int lastIndex;
        private int index;
        private final float[] data;
    
        public IteratorOverFloat(Array array) {
            this.stride = array.stride[0];
            this.index = array.offset;
            this.lastIndex =  index + stride * array.shape[0];
            this.data = (float[])array.data;
        }
        
        @Override
        public boolean hasNext() {
            return index < lastIndex;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public double nextDouble() {
            double value = (double)data[index];
            index += stride;
            return value;
        }
    }
    
}
