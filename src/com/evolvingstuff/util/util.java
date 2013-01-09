package com.evolvingstuff.util;

import processing.core.*;

public class util {
	
	public static double[][][] ImageToColorMatrix(String filename) {
		PApplet p = new PApplet();
		PImage img = p.loadImage(filename);
		double[][][] result = new double[img.height][img.width][3];
		for (int h = 0; h < img.height; h++) {
			for (int w = 0; w < img.width; w++) {
				result[h][w] = IntColorToRGB(img.get(w, h));
			}
		}
		return result;
	}
	
	public static void ColorMatrixToImage(double[][][] matrix, String filepath) {
		PApplet p = new PApplet();
		PImage img = p.createImage(matrix[0].length,matrix.length, PConstants.RGB);
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				double r = matrix[y][x][0]*255.0;
				if (r < 0) {
					r = 0;
				}
				if (r > 255) {
					r = 255;
				}
				double g = matrix[y][x][1]*255.0;
				if (g < 0) {
					g = 0;
				}
				if (g > 255) {
					g = 255;
				}
				double b = matrix[y][x][2]*255.0;
				if (b < 0) {
					b = 0;
				}
				if (b > 255) {
					b = 255;
				}
				int c = p.color((int)r, (int)g, (int)b);
				img.set(x, y, c);
			}
		}
		img.save(filepath);
	}
	
	public static PImage ColorMatrixToImage(double[][][] matrix) {
		PApplet p = new PApplet();
		PImage img = p.createImage(matrix[0].length,matrix.length, PConstants.RGB);
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				int c = p.color((int)(matrix[y][x][0]*255.0), (int)(matrix[y][x][1]*255.0), (int)(matrix[y][x][2]*255.0));
				img.set(x, y, c);
			}
		}
		return img;
	}
	
	private static double[] IntColorToRGB(int i) {
		i += 16777216;
		double[] result = new double[3];
		int R = (i-(i%65536))/65536;
		int G = (i%65536-(i%256))/256;
		int B = i%256;
		result[0] = (double)R/255.0;
		result[1] = (double)G/255.0;
		result[2] = (double)B/255.0;
		return result;
	}
}
