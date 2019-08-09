/*
 * Copyright (C) 2019 Paul
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

/**
 *
 * @author Paul
 */
public class DotProduct {

    public static float floatXfloatInnerProduct(int leftStride, int leftLastIndex, int leftIndex, int rightStride, int rightIndex, float[] leftData, float[] rightData) {
        double result = 0;
        while (leftIndex < leftLastIndex) {
            result += leftData[leftIndex] * rightData[rightIndex];
            leftIndex += leftStride;
            rightIndex += rightStride;
        }
        return (float) result;
    }

    public static float intXfloatInnerProduct(int leftStride, int leftLastIndex, int leftIndex, int rightStride, int rightIndex, int[] leftData, float[] rightData) {
        double result = 0;
        while (leftIndex < leftLastIndex) {
            result += leftData[leftIndex] * rightData[rightIndex];
            leftIndex += leftStride;
            rightIndex += rightStride;
        }
        return (float) result;
    }

    public static int intXintInnerProduct(int leftStride, int leftLastIndex, int leftIndex, int rightStride, int rightIndex, int[] leftData, int[] rightData) {
        int result = 0;
        while (leftIndex < leftLastIndex) {
            result += leftData[leftIndex] * rightData[rightIndex];
            leftIndex += leftStride;
            rightIndex += rightStride;
        }
        return result;
    }

    public static Object iXiMMUL(int nRows, int nCols, int innerCount, int aOffset, int aColStride, int bOffset, int bRowStride, int[] aData, int[] bData, int bColStride, int[] result, int aRowStride) {
        Object resultData;
        int aRowIndex = 0;
        for (int i = 0; i < nRows; i++) {
            int bColIndex = 0;
            for (int j = 0; j < nCols; j++) {
                int sum = 0;
                int bRowIndex = 0;
                int aColIndex = 0;
                for (int k = 0; k < innerCount; k++) {
                    //c[i][j] += a[i][k] * b[k][j]
                    int aikIndex = aOffset + aRowIndex + aColIndex;
                    int bkjIndex = bOffset + bRowIndex + bColIndex;
                    sum += aData[aikIndex] * bData[bkjIndex];
                    bRowIndex += bRowStride;
                    aColIndex += aColStride;
                }
                bColIndex += bColStride;
                int cijIndex = i * nCols + j;
                result[cijIndex] = sum;
            }
            aRowIndex += aRowStride;
        }
        resultData = result;
        return resultData;
    }

    public static Object fXfMMUL(int nRows, int nCols, int innerCount, int aOffset, int aColStride, int bOffset, int bRowStride, float[] aData, float[] bData, int bColStride, float[] result, int aRowStride) {
        Object resultData;
        int aRowIndex = 0;
        for (int i = 0; i < nRows; i++) {
            int bColIndex = 0;
            for (int j = 0; j < nCols; j++) {
                float sum = 0.0F;
                int aColIndex = 0;
                int bRowIndex = 0;
                for (int k = 0; k < innerCount; k++) {
                    //c[i][j] += a[i][k] * b[k][j]
                    int aikIndex = aOffset + aRowIndex + aColIndex;
                    int bkjIndex = bOffset + bRowIndex + bColIndex;
                    sum += aData[aikIndex] * bData[bkjIndex];
                    bRowIndex += bRowStride;
                    aColIndex += aColStride;
                }
                bColIndex += bColStride;
                int cijIndex = i * nCols + j;
                result[cijIndex] = sum;
            }
            aRowIndex += aRowStride;
        }
        resultData = result;
        return resultData;
    }
    
}
