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
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Paul
 */
public class IndexIterator implements Iterator<int[]> {
    
    private final int numDim;
    private final int[] shape;
    private final int[] currentIndex;
    private boolean weHaveNext = true;
    private final int[] zeros;
    
    public IndexIterator(int... shape) {
        numDim = shape.length;
        this.shape = shape;
        currentIndex = new int[numDim];
        zeros = new int[numDim];
    }
    
    public boolean hasNext() {
        return weHaveNext;
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    public int[] next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        int[] result = Arrays.copyOf(currentIndex, numDim);
        for (int i = numDim-1; i >= 0; i--) {
            currentIndex[i]++;
            if (currentIndex[i] < shape[i]) {
                break;
            }
            currentIndex[i] = 0;
        }
        weHaveNext = !Arrays.equals(zeros, currentIndex);
        return result;
    }
    
}
