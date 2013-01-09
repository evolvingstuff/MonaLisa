package com.evolvingstuff.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.evolvingstuff.agent.IAgentSupervised;
import com.evolvingstuff.evaluator.IInteractiveEvaluatorSupervised;
import com.evolvingstuff.util.util;

public class MonaLisa implements IInteractiveEvaluatorSupervised {
	
	class PixelLocation {
		public PixelLocation(int h, int w) {
			this.h = h;
			this.w = w;
		}
		public int h;
		public int w;
	}

	private double[][][] data;
	private int height;
	private int width;
	private boolean validation_mode = false;
	private int iteration = 0;
	private boolean visualize = true;
	private Random r;
	private List<PixelLocation> fixed_mix = new ArrayList<PixelLocation>();
	private String folder;

	public MonaLisa(Random r, String folder, String file) {
		System.out.println("\nloading picture for Mona Lisa task.");
		this.folder = folder;
		data = util.ImageToColorMatrix(folder + file);
		height = data.length;
		width = data[0].length;
		System.out.println("DIM: "+height+"x" + width);
		this.r = r;
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				fixed_mix.add(new PixelLocation(h,w));
			}
		}
	}
	
	public double EvaluateFitnessSupervised(IAgentSupervised agent) throws Exception {
		double tot = 0;
		double max_tot = 0;
		double[][][] guess = new double[height][width][3];
		List<PixelLocation> pixels_mixed = new ArrayList<PixelLocation>();
		if (validation_mode == false) {
			List<PixelLocation> pixels = new ArrayList<PixelLocation>();
			for (int h = 0; h < height; h++) {
				for (int w = 0; w < width; w++) {
					pixels.add(new PixelLocation(h,w));
				}
			}
			while (pixels.size() > 0) {
				int loc = r.nextInt(pixels.size());
				pixels_mixed.add(pixels.get(loc));
				pixels.remove(loc);
			}
		}
		else {
			pixels_mixed = fixed_mix;
		}

		int count = 0;
		for (PixelLocation p : pixels_mixed) {
			if (count%2000 == 1999) {
				System.out.print(".");
			}
			count++;
			double[] actual_output;
			double[] input = {(double)p.h/((double)(height-1)), (double)p.w/((double)(width-1))};
			if (validation_mode) {
				actual_output = agent.Next(input);
			}
			else {
				actual_output = agent.Next(input, data[p.h][p.w]);
			}

			for (int c = 0; c < 3; c++) {
				tot += 1 - Math.abs(data[p.h][p.w][c] - actual_output[c]);
				max_tot++;
				guess[p.h][p.w][c] = actual_output[c];
			}
		}
		System.out.print("\n");
		if (validation_mode && visualize) {
			System.out.println("making visualization v"+iteration+".");
			util.ColorMatrixToImage(guess, folder + "learned_" + (iteration++) + ".jpg");
		}
		return tot/max_tot;
	}

	public int GetActionDimension() {
		return 3;
	}

	public int GetObservationDimension() {
		return 2;
	}

	public void SetValidationMode(boolean validation) {
		validation_mode = validation;
	}
}
