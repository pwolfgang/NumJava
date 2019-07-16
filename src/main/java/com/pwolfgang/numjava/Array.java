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

/**
 * This class is similar to the NumPY ndarray.
 * @author Paul
 */
public class Array {
    
    private int[] shape;
    private Class<?> dataType;
    Object data;
    
    public Array(int[] shape, Class<?> dataType, Object data) {
        this.shape = shape;
        this.dataType = dataType;
        this.data = data;
    }
    
    /**
     * Construct an Array from a Java Array of primitive types.
     * @param data An Object reference to the source array.
     */
    public Array(Object data) {
        Class<?> dataClass = data.getClass();
        if (!dataClass.isArray()) {
            throw new IllegalArgumentException(dataClass + " is not an array");
        }
        Class<?> parentClass = dataClass;
        Object currentLevel = data;
        List<Integer> sizes = new ArrayList<>();
        Class<?> componentType;
        while(true) {
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
        shape = new int[sizes.size()];
        for (int i = 0; i < sizes.size(); i++) {
            shape[i] = sizes.get(i);
        }
        dataType = componentType;
        int totalSize = 1;
        for (int d : shape) {
            totalSize *= d;
        }
        this.data = java.lang.reflect.Array.newInstance(dataType, totalSize);
        copyData(data, 0);
        
    }
    
    private int copyData(Object data, int index) {
        if (data.getClass().getComponentType().isPrimitive()) {
            return copyRow(this.data, data, index);
        } else {
            int length = java.lang.reflect.Array.getLength(data);
            for (int i = 0; i < length; i++) {
                Object level = java.lang.reflect.Array.get(data, i);
                int levelLength = java.lang.reflect.Array.getLength(level);
                index = copyData(level, index);
            }
            return index;
        }
    }
    
    private int copyRow(Object data, Object row, int index) {
        int rowSize = java.lang.reflect.Array.getLength(row);
        for (int i = 0; i < rowSize; i++) {
            java.lang.reflect.Array.set(data, index, java.lang.reflect.Array.get(row, i));
            index++;
        }
        return index;
    }
    
    public int[] getShape() {
        return shape;
    }
    
    public Class<?> getDataType() {
        return dataType;
    }
    
    public int getInt(int... idx) {
        if (idx.length != shape.length) {
            throw new IllegalArgumentException("Indices must match shape to get signle element");
        }
        int index = computeIndex(idx);
        return ((int[])data)[index];
    }
    
    public float getFloat(int... idx) {
        if (idx.length != shape.length) {
            throw new IllegalArgumentException("Indices must match shape to get signle element");
        }
        int index = computeIndex(idx);
        return ((float[])data)[index];
    }

    private int computeIndex(int[] idx) throws IllegalArgumentException {
        if (idx.length > shape.length) {
            throw new IllegalArgumentException("Too many indices: " 
                    + Arrays.asList(idx).toString()
                    + " shape: " + Arrays.asList(shape).toString());
        }
        int index = 0;
        for (int i = 0; i < idx.length - 1; i++) {
            index += idx[i];
            index *= shape[i+1];
        }
        index += idx[idx.length-1];
        return index;
    }
    
    public Array getSubArray(int ...idx) {
        if (idx.length > shape.length-1) {
            throw new IllegalArgumentException("Too many indices");
        }
        int[] idxPrime = new int[shape.length];
        System.arraycopy(idx, 0, idxPrime, 0, idx.length);
        int index = computeIndex(idxPrime);
        int deltaIndices = shape.length - idx.length;
        int[] newShape = new int[deltaIndices];
        for (int i = 0; i < deltaIndices; i++) {
            newShape[i] = shape[idx.length + i];
        }
        int totalSize = 1;
        for (int d : newShape) {
            totalSize *= d;
        }
        Object newData = java.lang.reflect.Array.newInstance(dataType, totalSize);
        System.arraycopy(data, index, newData, 0, totalSize);
        return new Array(newShape, dataType, newData);
    }
    
}
