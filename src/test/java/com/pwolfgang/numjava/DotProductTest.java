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

import java.util.Random;
import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author Paul
 */
public class DotProductTest {
    
    public DotProductTest() {
    }
    
    Random random;
    
    int[] intData;
    float[] floatData;
    
    @Before
    public void init() {
        random = new Random();
        intData = new int[10000];
        floatData = new float[10000];
        for (int i = 0; i < 10000; i++) {
            intData[i] = random.nextInt();
            floatData[i] = random.nextFloat();
        }
    }

    /**
     * Test of intXintInnerProduct method, of class DotProduct.
     */
    @Test
    public void testIntXintInnerProduct() {
        long start = System.nanoTime();
        int result = DotProduct.intXintInnerProduct(1, 10000, 0, 1, 0, intData, intData);
        long end = System.nanoTime();
        System.out.println("Time for int x int :" + (end - start));
    }

    /**
     * Test of intXfloatInnerProduct method, of class DotProduct.
     */
    @Test
    public void testIntXfloatInnerProduct() {
        long start = System.nanoTime();
        float result = DotProduct.intXfloatInnerProduct(1, 10000, 0, 1, 0, intData, floatData);
        long end = System.nanoTime();
        System.out.println("Time for int x float :" + (end - start));
    }

    /**
     * Test of floatXfloatInnerProduct method, of class DotProduct.
     */
    @Test
    public void testFloatXfloatInnerProduct() {
        long start = System.nanoTime();
        float result = DotProduct.floatXfloatInnerProduct(1, 10000, 0, 1, 0, floatData, floatData);
        long end = System.nanoTime();
        System.out.println("Time for float x float :" + (end - start));
    }
    
}
