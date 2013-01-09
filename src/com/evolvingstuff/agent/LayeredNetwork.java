package com.evolvingstuff.agent;

import java.util.List;

public class LayeredNetwork implements IAgentSupervised {
	
	int input_dimension;
	int output_dimension;
	List<IFeedforwardLayer> layers;
	boolean dropoutMode = false;
	
	public LayeredNetwork(List<IFeedforwardLayer> layers) throws Exception {
		this.layers = layers;
		input_dimension = layers.get(0).GetInputDimension();
		output_dimension = layers.get(layers.size()-1).GetOutputDimension();
		if (layers.size() == 0) {
			throw new Exception("missing layers");
		}
		for (int i = 1; i < layers.size(); i++) {
			if (layers.get(i).GetInputDimension() != layers.get(i-1).GetOutputDimension()) {
				throw new Exception("Layer mismatch");
			}
		}
	}

	public double[] Next(double[] input, double[] target_output) throws Exception {
		
		double[] next = input;
		for (IFeedforwardLayer layer : layers) {
			next = layer.Forward(next);
		}
		double[] output = next;
		
		if (target_output != null) {
			double[] delta = new double[output_dimension];
			for (int k = 0; k < output_dimension; k++) {
				delta[k] = target_output[k] - output[k];
			}
			
			next = delta;
			for (int i = layers.size() - 1; i >= 0; i--) {
				if (next == null) {
					throw new Exception("No backprop signal from post layer");
				}
				try {
					next = layers.get(i).Backprop(next);
				}
				catch (Exception e) {
					throw new Exception(e.getMessage() + " caught at layer " + i);
				}
			}
		}
		return output;
	}

	public double[] Next(double[] input) throws Exception {
		return Next(input, null);
	}
}
