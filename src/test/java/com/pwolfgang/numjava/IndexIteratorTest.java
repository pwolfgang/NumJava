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

import java.util.Iterator;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

/**
 *
 * @author Paul
 */
public class IndexIteratorTest {
    
    public IndexIteratorTest() {
    }

    /**
     * Test of remove method, of class IndexIterator.
     */
    @Test(expected=UnsupportedOperationException.class)
    public void testRemove() {
        Iterator<int[]> itr = new IndexIterator(2, 3, 2);
        itr.remove();
    }

    /**
     * Test of next method, of class IndexIterator.
     */
    @Test
    public void testNext() {
        Iterator<int[]> itr = new IndexIterator(2, 3, 2);
        int[][] expected = 
           {{0, 0, 0},
            {0, 0, 1},
            {0, 1, 0},
            {0, 1, 1},
            {0, 2, 0},
            {0, 2, 1},
            {1, 0, 0},
            {1, 0, 1},
            {1, 1, 0},
            {1, 1, 1},
            {1, 2, 0},
            {1, 2, 1}};
        for (int i = 0; i < expected.length; i++) {
            assertArrayEquals(expected[i], itr.next());
        }
    }
    
}
