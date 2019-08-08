/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pwolfgang.numjava;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 *
 * @author Paul
 */
public class ReadMNIST {

    public static Array readImages(String fileName) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(fileName))) {
            int magicNumber = readInt(in);
            if (magicNumber != 2051) {
                throw new RuntimeException("Unrecognized magic number " + magicNumber);
            }
            int numItems = readInt(in);
            int numRows = readInt(in);
            int numCols = readInt(in);
            int total = numItems*numRows*numCols;
            float[] data = new float[total];
            for (int i = 0; i < total; i++) {
                data[i] = in.read()/255.0f;
            }
            int[] shape = new int[]{numItems, numRows*numCols};
            int[] stride = new int[]{numRows*numCols, 1};
            return new Array(shape, stride, float.class, 0, data);
            
        } catch (IOException ioex) {
            throw new UncheckedIOException(ioex);
        }
    }
    
    public static Array readLabels(String fileName) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(fileName))) {
            int magicNumber = readInt(in);
            if (magicNumber != 2049) {
                throw new RuntimeException("Unrecognized magic number " + magicNumber);
            }
            int numItems = readInt(in);
            float[][] data = new float[numItems][10];
            for (int i = 0; i < numItems; i++) {
                int b = in.read();
                data[i][b] = 1.0f;
            }
            return new Array(data);
        } catch (IOException ioex) {
            throw new UncheckedIOException(ioex);
        }
    }
    

    public static int readInt(InputStream in) throws IOException {
        byte[] bytes = new byte[4];
        int numRead = in.read(bytes);
        if (numRead != 4) {
            throw new IOException("Expected an int");
        }
        int value = bytes[0] << 24;
        value += (bytes[1] & 0xff) << 16;
        value += (bytes[2] & 0xff) << 8;
        value += (bytes[3] & 0xff);
        return value;
    }

}
