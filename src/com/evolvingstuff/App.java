package com.evolvingstuff;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.evolvingstuff.agent.IFeedforwardLayer;
import com.evolvingstuff.agent.IdentityNeuron;
import com.evolvingstuff.agent.Layer;
import com.evolvingstuff.agent.LayeredNetwork;
import com.evolvingstuff.agent.Neuron;
import com.evolvingstuff.agent.RectifiedLinearNeuron;
import com.evolvingstuff.task.MonaLisa;

public class App {
	public static void main(String[] args) throws Exception {
		System.out.println("MonaLisa");
		
		Random r = new Random(333);

		String folder = "data/";
		String file = "Mona Lisa.jpg";
		MonaLisa task = new MonaLisa(r, folder, file);

		System.out.println("\n");
		System.out.println("========================================================================");
		
		int epoches = 500;
		
		double init_weight_range = 0.3;
		double learning_rate = 0.1;
		Neuron nonlinear_neuron = new RectifiedLinearNeuron(0.01);
		int input_dimension = task.GetObservationDimension();
		int hidden_dimension = 300;
		int total_layers = 5;
		int output_dimension = task.GetActionDimension();
		
		Neuron linear_neuron = new IdentityNeuron();
		List<IFeedforwardLayer> layers = new ArrayList<IFeedforwardLayer>();
		layers.add(new Layer(r, input_dimension, hidden_dimension, nonlinear_neuron, init_weight_range, learning_rate));
		for (int i = 0; i < total_layers; i++) {
			layers.add(new Layer(r, hidden_dimension, hidden_dimension, nonlinear_neuron, init_weight_range, learning_rate));
		}
		layers.add(new Layer(r, hidden_dimension, output_dimension, linear_neuron, init_weight_range, learning_rate));
		LayeredNetwork agent = new LayeredNetwork(layers);
		
		int low_at = 1;
		double low = Double.POSITIVE_INFINITY;
		for (int t = 0; t < epoches; t++)
		{
			task.SetValidationMode(false);
			task.EvaluateFitnessSupervised(agent);
			task.SetValidationMode(true);
			double validation = task.EvaluateFitnessSupervised(agent);
			
			System.out.println("\n\t[epoch "+(t+1)+"]:\t" + (1-validation));
			
			if (1 - validation < low) {
				low = 1 - validation;
				low_at = t;
			}
			System.out.println("\tlowest error  = " + low + " @ epoch " + (low_at+1));
		}

		System.out.println("\n\ndone.");
	}

}
