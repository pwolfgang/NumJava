/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pwolfgang.numjava;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Paul
 */
public class MNISTTest1Test {
    
    public MNISTTest1Test() {
    }

    @Test
    public void testReadimage() {
        int[] expected_int = new int[]{0,0,0,0,0,0,0,198,253,190,0,0,0,0,0,0,0,0,0,0,255,253,196,0,0,0,0,0};
        Array data = ReadMNIST.readImages("..\\MNIST\\train-images-idx3-ubyte");
        float[] expected_float = new float[28];
        for (int i = 0; i < 28; i++) {
            expected_float[i] = expected_int[i]/255.0f;
        }
        Array expected = new Array(expected_float);
        Array slice = data.getSubArray(1).reShape(28, 28).getSubArray(14);
        assertEquals(expected, slice);
    }
    
    @Test
    public void testReadLabels() {
        int[][] expected = new int[][]{
            {0,0,0,0,0,1,0,0,0,0},
            {1,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,0,0,0,0,0},
            {0,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,1}
        };
        Array lables = ReadMNIST.readLables("..\\MNIST\\train-labels-idx1-ubyte");
        Array range = lables.getRange(0, 5);
        assertEquals(new Array(expected), range);
    }
    
}
