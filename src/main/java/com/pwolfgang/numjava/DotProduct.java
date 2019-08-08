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

import cz.adamh.utils.NativeUtils;

/**
 *
 * @author Paul
 */
public class DotProduct {
    static {
        String libName;
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            libName = "/dotproduct.dll";
        } else {
            libName = "/libdotproduct.so";
        }
        try {
            NativeUtils.loadLibraryFromJar(libName);
        } catch (Exception ex) {
            throw new RuntimeException("Load of library failed", ex);
        }
    }

    public native static int intXintInnerProduct(int leftStride, int leftLastIndex, int leftIndex, 
            int rightStride, int rightIndex, int[] leftData, int[] rightData); 
//    {
//        int result = 0;
//        while (leftIndex < leftLastIndex) {
//            result += leftData[leftIndex] * rightData[rightIndex];
//            leftIndex += leftStride;
//            rightIndex += rightStride;
//        }
//        return result;
//    }

    public native static float intXfloatInnerProduct(int leftStride, int leftLastIndex, 
            int leftIndex, int rightStride, int rightIndex, int[] leftData, float[] rightData); 
//    {
//        double result = 0;
//        while (leftIndex < leftLastIndex) {
//            result += leftData[leftIndex] * rightData[rightIndex];
//            leftIndex += leftStride;
//            rightIndex += rightStride;
//        }
//        return (float) result;
//    }

    public native static float floatXfloatInnerProduct(int leftStride, int leftLastIndex, 
            int leftIndex, int rightStride, int rightIndex, float[] leftData, float[] rightData); 
//    {
//        double result = 0;
//        while (leftIndex < leftLastIndex) {
//            result += leftData[leftIndex] * rightData[rightIndex];
//            leftIndex += leftStride;
//            rightIndex += rightStride;
//        }
//        return (float) result;
//    }
    
}
