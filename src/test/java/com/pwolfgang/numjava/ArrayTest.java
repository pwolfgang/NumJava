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

import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.function.DoubleSupplier;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Paul
 */
public class ArrayTest {
    
    public ArrayTest() {
    }

    @Test
    public void constructSingleArrayOfInts() {
        System.out.println("Single Array of Ints");
        Array anArray = new Array(new int[]{1, 2, 3, 4});
        System.out.println(anArray);
        assertArrayEquals(new int[]{4}, anArray.getShape());
        assertEquals(int.class, anArray.getDataType());
        assertArrayEquals(new int[]{1, 2, 3, 4}, (int[])anArray.data);
        assertEquals(1, anArray.getInt(0));
    }
    
    @Test
    public void constructTwoDimArrayOfFloat() {
        System.out.println("Two Dim Array Of Float");
        Array anArray = createTwoDimArray();
        System.out.println(anArray);
        assertEquals(float.class, anArray.getDataType());
        assertArrayEquals(new int[]{3, 4}, anArray.getShape());
        assert(Arrays.equals(new float[]{1.0f, 2.0f, 3.0f, 4.0f, 
            5.0f, 6.0f, 7.0f, 8.0f, 
            10.0f, 11.0f, 12.0f, 13.0f},
                (float[])anArray.data));
    }

    public Array createTwoDimArray() {
        float[][] twoDFloat = new float[][]
            {{ 1.0f,  2.0f,  3.0f,  4.0f},
             { 5.0f,  6.0f,  7.0f,  8.0f},
             {10.0f, 11.0f, 12.0f, 13.0f}};
        Array anArray = new Array(twoDFloat);
        return anArray;
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void constructArrayOfString() {
        Array anArray = new Array("The quick brown fox");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void constructArrayOfStrings() {
        Array anArray = new Array(new String[]{"A", "B", "C"});
    }
    
    @Test
    public void contructFourDimArrayOfInt() {
        System.out.println("Four Dim Array of Int");
        int [][][][] data = new int[][][][]
        {{{{1111, 1112, 1113, 1114},
           {1121, 1122, 1123, 1124}},
          {{1211, 1212, 1213, 1214},
           {1221, 1222, 1223, 1223}},
          {{1311, 1312, 1313, 1314},
           {1321, 1322, 1323, 1324}}},
         {{{2111, 2112, 2113, 2114},
           {2121, 2122, 2124, 2124}},
          {{2211, 2212, 2213, 2214},
           {2221, 2222, 2223, 2224}},
          {{2311, 2312, 2313, 2314},
           {2321, 2322, 2323, 2324}}}};
        Array anArray = new Array(data);
        System.out.println(anArray);
        assertEquals(int.class, anArray.getDataType());
        assertArrayEquals(new int[]{2, 3, 2, 4}, anArray.getShape());
        assertEquals(1111, anArray.getInt(0, 0, 0, 0));
        assertEquals(2314, anArray.getInt(1, 2, 0, 3));
        Array subArray = anArray.getSubArray(1, 2);
        System.out.println("Subarray(1,2)");
        System.out.println(subArray);
        assertArrayEquals(new int[]{2, 4}, subArray.getShape());
        int k = 0;
        int[] expected = new int[]
           {2311, 2312, 2313, 2314, 
            2321, 2322, 2323, 2324};
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(expected[k++], subArray.getInt(i, j));
            }
        }
    }
    
    @Test
    public void testTranspose() {
        int[][] data = new int[][]
        {{1, 2, 3, 4},
         {5, 6, 7, 8}};
        Array anArray = new Array(data);
        System.out.println("Original array");
        System.out.println(anArray);
        Array anArrayT = anArray.transpose();
        System.out.println("Transpose");
        String anArrayT_toString = anArrayT.toString();
        System.out.println(anArrayT);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                assertEquals(data[j][i], anArrayT.getInt(i, j));
            }
        }
    }
    
    @Test
    public void reShape2dto1d() {
        int[][] data = new int[][]
        {{1, 2, 3, 4},
         {5, 6, 7, 8}};
        Array anArray = new Array(data);
        Array reShaped = anArray.reShape(new int[]{8});
        for (int i = 0; i < 8; i++) {
            assertEquals(i+1, reShaped.getInt(i));
        }
    }

    @Test
    public void reShape1dto2d() {
        int[][] data = new int[][]
        {{1, 2, 3, 4},
         {5, 6, 7, 8}};
        Array anArray = new Array(new int[]{1, 2, 3, 4, 5, 6, 7, 8});
        Array reShaped = anArray.reShape(new int[]{2, 4});
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(data[i][j], reShaped.getInt(i, j));
            }
        }
    }
    
    @Test
    public void constructSingleton() {
        Array anArray = new Array(10);
        assertEquals(int.class, anArray.getDataType());
        assertEquals(10, anArray.getInt(0));
    }
    
    @Test
    public void iteratorOverSingleton() {
        Array anArray = new Array(10);
        PrimitiveIterator.OfDouble itr = anArray.iterator();
        assertTrue(itr.hasNext());
        assertEquals(10.0, itr.nextDouble(), 1e-18);
        assertFalse(itr.hasNext());
    }
    
    @Test
    public void rowIteratorOverTwoDimArrayOfFloat() {
        Array anArray = createTwoDimArray();
        Array secondRow = anArray.getSubArray(1);
        PrimitiveIterator.OfDouble itr = secondRow.iterator();
        assertTrue(itr.hasNext());
        assertEquals(5.0, itr.next(), 1e-18);
        assertEquals(6.0, itr.next(), 1e-18);
        assertEquals(7.0, itr.next(), 1e-18);
        assertEquals(8.0, itr.next(), 1e-18);
        assertFalse(itr.hasNext());
    }

    @Test
    public void colIteratorOverTwoDimArrayOfFloat() {
        Array anArray = createTwoDimArray();
        Array secondCol = anArray.transpose().getSubArray(1);
        PrimitiveIterator.OfDouble itr = secondCol.iterator();
        assertTrue(itr.hasNext());
        assertEquals(2.0, itr.next(), 1e-18);
        assertEquals(6.0, itr.next(), 1e-18);
        assertEquals(11.0, itr.next(), 1e-18);
        assertFalse(itr.hasNext());
    }
    
    @Test
    public void testDotOfTwoRows() {
        Array anArray = createTwoDimArray();
        Array firstRow = anArray.getSubArray(0);
        Array secondRow = anArray.getSubArray(1);
        Array dotProd = firstRow.dot(secondRow);
        assertEquals(new Array(70.0f), dotProd);
    }
    
    @Test 
    public void testDotOfTwoCols() {
        Array anArray = createTwoDimArray().transpose();
        Array dotProd = anArray.getSubArray(0).dot(anArray.getSubArray(1));
        assertEquals(new Array(142.0f), dotProd);
    }
    
    @Test
    public void testOfMatrixMul_2x4_by_4x2() {
        int[][] a = {{1, 2, 3, 4},
            {5, 6, 7, 8}};
        int[][] b = {{9, 10},
            {11, 12},
            {13, 14},
            {15, 16}};
        
        int[][] c = {{130, 140},
            {322, 348}};
        
        Array arrayA = new Array(a);
        Array arrayB = new Array(b);
        Array arrayC = new Array(c);
        assertEquals(arrayC, arrayA.dot(arrayB));
    }

    @Test
    public void testOfMatrixMul_2x4_by_4x1() {
        int[][] a = {{1, 2, 3, 4},
            {5, 6, 7, 8}};
        int[][] b = {{9}, {11}, {13}, {15}};
        
        int[][] c = {{130}, {322}};
        
        Array arrayA = new Array(a);
        Array arrayB = new Array(b);
        Array arrayC = new Array(c);
        Array result = arrayA.dot(arrayB);
        assertEquals(arrayC, result);
    }

    @Test
    public void testOfMatrixMul_2x4_by_one_dim_array() {
        int[][] a = {{1, 2, 3, 4},
            {5, 6, 7, 8}};
        int[] b = {9, 11, 13, 15};
        
        int[] c = {130, 322};
        
        Array arrayA = new Array(a);
        Array arrayB = new Array(b);
        Array arrayC = new Array(c);
        Array result = arrayA.dot(arrayB);
        assertEquals(arrayC, result);
    }
    
    @Test
    public void testOf2x2x4byOneDimArray() {
        int [][][]a = {{{1, 2, 3, 4}, {5, 6, 7, 8}},{{20, 21, 22, 23}, {24, 24, 26, 27}}};
        int[] b = {9, 11, 13, 15};
        int[][] c = {{130, 322}, {1042, 1223}};
        Array arrayA = new Array(a);
        Array arrayB = new Array(b);
        Array arrayC = new Array(c);
        Array result = arrayA.dot(arrayB);
        assertEquals(arrayC, result);

    }
    
    @Test
    public void testOfNdim_by_Mdim() {
        Array a = new Array(new int[][][]
                {{{111, 112, 113}, {121, 122, 123}, {131, 132, 133}},
                 {{211, 212, 213}, {221, 222, 223}, {231, 232, 233}},
                 {{311, 312, 313}, {321, 322, 323}, {331, 332, 333}}});
        Array rowSlice = a.getSubArray(1, 1);
        System.out.println(rowSlice);
        System.out.println(Arrays.toString((int[])rowSlice.data));
        Array colSlice = a.getSubArray(1).transpose().getSubArray(1);
        System.out.println(colSlice);
        System.out.println(Arrays.toString((int[])colSlice.data));
        Array expected = new Array(new int[][][][]
        {{{{ 40676,  41012,  41348},
           { 74276,  74612,  74948},
           {107876, 108212, 108548}},

          {{ 44306,  44672,  45038},
           { 80906,  81272,  81638},
           {117506, 117872, 118238}},

          {{ 47936,  48332,  48728},
           { 87536,  87932,  88328},
           {127136, 127532, 127928}}},


         {{{ 76976,  77612,  78248},
           {140576, 141212, 141848},
           {204176, 204812, 205448}},

          {{ 80606,  81272,  81938},
           {147206, 147872, 148538},
           {213806, 214472, 215138}},

          {{ 84236,  84932,  85628},
           {153836, 154532, 155228},
           {223436, 224132, 224828}}},


         {{{113276, 114212, 115148},
           {206876, 207812, 208748},
           {300476, 301412, 302348}},

          {{116906, 117872, 118838},
           {213506, 214472, 215438},
           {310106, 311072, 312038}},

          {{120536, 121532, 122528},
           {220136, 221132, 222128},
           {319736, 320732, 321728}}}});
        Array result = a.dot(a);
        assertEquals(expected, result);
    }
    
    @Test
    public void testOfCopy() {
        Array a = new Array(new int[][][]
                {{{111, 112, 113}, {121, 122, 123}, {131, 132, 133}},
                 {{211, 212, 213}, {221, 222, 223}, {231, 232, 233}},
                 {{311, 312, 313}, {321, 322, 323}, {331, 332, 333}}});
        Array colSlice = a.getSubArray(1).transpose().getSubArray(1);
        System.out.println(colSlice);
        System.out.println(Arrays.toString((int[])colSlice.data));
        Array colSliceCopy = Array.copyOf(colSlice);
        System.out.println(colSliceCopy);
        System.out.println(Arrays.toString((int[])colSliceCopy.data));
        Array expected = new Array(new int[]{212, 222, 232});
        assertEquals(expected, colSliceCopy);
        assertArrayEquals(new int[]{3}, colSliceCopy.getShape());
        assertArrayEquals(new int[]{1}, colSliceCopy.stride);
        assertEquals(0, colSliceCopy.offset);  
    }
    
    @Test
    public void arrayMinusSingleton() {
        Array left = new Array(new int[]{1, 2, 3, 4});
        Array right = new Array(2);
        Array expected = new Array(new int[]{-1, 0, 1, 2});
        assertEquals(expected, left.sub(right));
    }
    
    @Test
    public void arrayPlusArray() {
        Array left = new Array(new int[]{1, 2, 3, 4});
        Array right = new Array(new int[]{5, 6, 7, 8});
        Array expected = new Array(new int[]{6, 8, 10, 12});
        assertEquals(expected, left.add(right));
    }
    
    @Test
    public void nultiDimSubArray() {
        Array a = new Array(new int [][][]
        {{{1, 2, 3, 4}, {5, 6, 7, 8}}, {{20, 21, 22, 23}, {24, 24, 26, 27}}});
        Array b = new Array(new int[][]{{9, 11, 13, 15},{1, 2, 3, 4}});
        Array c = new Array(new int[][][]
        {{{10, 13, 16, 19}, {6,  8, 10, 12}},{{29, 32, 35, 38}, {25, 26, 29, 31}}});
        assertEquals(c, a.add(b));
    }
    
    @Test
    public void divByScalar() {
        Array a = new Array(new int[]{1, 2, 3, 4});
        Array b = new Array(2.0f);
        Array c = new Array(new float[]{0.5f, 1.0f, 1.5f, 2.0f});
        assertEquals(c, a.div(b));
    }
         
    @Test
    public void arrayTimesArray() {
        Array left = new Array(new float[]{1.0f, 2.0f, 3.0f, 4.0f});
        Array right = new Array(new int[]{5, 6, 7, 8});
        Array expected = new Array(new float[]{5.0f, 12.0f, 21.0f, 32.0f});
        assertEquals(expected, left.mul(right));
    }
    
    @Test 
    public void testRandom() {
        System.out.println("Test Random");
        Random rand = new Random(1);
        DoubleSupplier supplier = () -> 2.0*rand.nextDouble() - 1.0;
        Array anArray = Array.random(supplier, 2, 4);
        System.out.println(anArray);
    }
}
