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
        Array anArray = new Array(new int[]{1, 2, 3, 4});
        assertArrayEquals(new int[]{4}, anArray.getShape());
        assertEquals(int.class, anArray.getDataType());
        assertArrayEquals(new int[]{1, 2, 3, 4}, (int[])anArray.data);
        assertEquals(1, anArray.getInt(0));
    }
    
    @Test
    public void constructTwoDimArrayOfFloat() {
        Array anArray = createTwoDimArray();
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
        assertEquals(int.class, anArray.getDataType());
        assertArrayEquals(new int[]{2, 3, 2, 4}, anArray.getShape());
        assertEquals(1111, anArray.getInt(0, 0, 0, 0));
        assertEquals(2314, anArray.getInt(1, 2, 0, 3));
        Array subArray = anArray.getSubArray(1, 2);
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
        Array anArrayT = anArray.transpose();
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
        assertEquals(Integer.class, anArray.getDataType());
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
}
