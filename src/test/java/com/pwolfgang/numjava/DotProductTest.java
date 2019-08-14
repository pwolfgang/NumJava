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

/**
 *
 * @author Paul
 */
public class DotProductTest {
    
    Random random = new Random();
    
    public DotProductTest() {
    }


    /**
     * Test of fXfMMUL method, of class DotProduct.
     */
    @Test
    public void testFXfMMUL() {
        doTest(100);
        doTest(100);
        for (int i = 100; i < 1700; i *= 1.4142135624) {
            long time = doTest(i);
            System.out.printf("%6d %10.3f%n", i, time/1e9);
        }
        
    }

    
    public long doTest(int n) {
        float[] data = new float[n * n];
        for (int i = 0; i < n*n; i++) {
            data[i] = random.nextFloat();
        }
        long start = System.nanoTime();
        DotProduct.fXfMMUL(n, n, n, 0, 1, 0, n, data, data, 1, n);
        long end = System.nanoTime();
        return end-start;
    }
    
}
