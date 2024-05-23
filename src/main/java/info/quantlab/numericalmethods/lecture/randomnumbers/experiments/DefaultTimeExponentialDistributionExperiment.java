package info.quantlab.numericalmethods.lecture.randomnumbers.experiments;

import java.util.ArrayList;
import java.util.List;

import info.quantlab.numericalmethods.lecture.randomnumbers.DefaultTimeExponentialDistribution;
import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator1D;
import info.quantlab.numericalmethods.lecture.randomnumbers.VanDerCorputSequence;
import net.finmath.plots.Plots;

/**
 * Experiment that generates default times
 * (i.e., exponentially distributed random times)
 * with parameter lambda.
 * 
 * It calculated the expectation E(τ) and the survival probability P(τ > T) via the
 * Monte-Carlo method / Monte-Carlo integration and compares them with the analytic results
 * E(τ) = 1/lambda and P(τ > T) = exp(- lambda T).
 */
public class DefaultTimeExponentialDistributionExperiment {

	public static void main(String[] args) {		
		int numberOfSamples = 10000;
		double lambda = 0.2;
		
		plot(new MersenneTwister(3141), lambda, numberOfSamples);
		plot(new VanDerCorputSequence(2), lambda, numberOfSamples);
	}

	private static void plot(RandomNumberGenerator1D uniformSequence, double lambda, int numberOfSamples) {

		String title = "Exponential Distribution with " + uniformSequence.toString() + ", \u03bb = " + lambda;
		
		System.out.println(title);
		System.out.println("_".repeat(79));

		DefaultTimeExponentialDistribution defaultTime = new DefaultTimeExponentialDistribution(uniformSequence, lambda);

		double maturity = 5.0;
		
		List<Double> times = new ArrayList<>();
		double sumOfTimes = 0;
		double defaultCounter = 0;
		for(int i=0; i<numberOfSamples; i++) {
			double time = defaultTime.getNext();

			times.add(time);

			if(i < 10) System.out.println(i + " :\t" + time);
			
			if(time > maturity) defaultCounter++;
			
			sumOfTimes += time;
		}
		defaultCounter /= numberOfSamples;
		double averageTime = sumOfTimes /= numberOfSamples;
		
		System.out.println();
		System.out.println("E(\u03c4) = " + averageTime);
		System.out.println("P(\u03c4 > T) = " + defaultCounter);
		System.out.println("exp(- \u03bb T) = " + Math.exp(-lambda * maturity));
		System.out.println("_".repeat(79) + "\n");
		
		Plots.createDensity(times, 500, 8.0)
		.setTitle(title)
		.setXRange(-10, 40)
		.show();
	}
}
