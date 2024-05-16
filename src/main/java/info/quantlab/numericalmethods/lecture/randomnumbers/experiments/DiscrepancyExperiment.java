package info.quantlab.numericalmethods.lecture.randomnumbers.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator1D;
import info.quantlab.numericalmethods.lecture.randomnumbers.VanDerCorputSequence;
import net.finmath.plots.Plot2D;
import net.finmath.plots.PlotableFunction2D;
import net.finmath.plots.PlotablePoints2D;
import net.finmath.plots.Point2D;

/**
 * Plots the function that appears in the definition of the star-discrepancy
 * for different sequences of sample points.
 *
 * @author Christian Fries
 */
public class DiscrepancyExperiment {

	public static void main(String[] args) {

		analyse("Random, n=5",
				DoubleStream.generate(new MersenneTwister(3216)).limit(5).boxed().toList());

		System.out.println();
		
		analyse("Van der Corput, n=5",
				DoubleStream.generate(new VanDerCorputSequence(2)).limit(5).boxed().toList());

		analyse("Van der Corput, n=7",
				DoubleStream.generate(new VanDerCorputSequence(2)).limit(7).boxed().toList());

		analyse("Van der Corput, n=9",
				DoubleStream.generate(new VanDerCorputSequence(2)).limit(9).boxed().toList());

		System.out.println();

		analyse("Refined left to right, n=5",
				List.of(1.0/8, 2.0/8, 3.0/8, 2.0/4,        3.0/4 ));

		analyse("Refined like v.d.C., n=5",
				List.of(1.0/8, 2.0/8,        2.0/4, 5.0/8, 3.0/4 ));
		
		System.out.println();

		/*
		 * Radom sequence with n = 1, 10, 100, 
		 */
		RandomNumberGenerator1D random = new MersenneTwister(3216);
		List<Double> randomNumbers = new ArrayList<Double>();
		for(int n=0; n<=100000; n++) {
			if(Math.log10(n) == Math.round(Math.log10(n))) {
				analyse("Random, n = " + n, randomNumbers);
			}
			randomNumbers.add(random.nextDouble());
		}
	}

	private static void analyse(String label, List<Double> samples) {
		System.out.println(String.format("%32s \t D = %f", label, getDiscrepancy(samples)));
		plotDiscrepancyFunction(label, samples);
	}

	private static double getDiscrepancy(List<Double> samples) {

		// Sort the list of sample points
		List<Double> samplesSorted = samples.stream().sorted().toList();

		double discrepance = 0.0;

		int count = 0;
		for(double sample : samplesSorted) {							// For a sample point x_i ...  

			double low = sample - (double)count/samples.size();			// ... evaluate lambda([0,x)) - i/n = x - i/n ...
			count++;
			double high = (double)count/samples.size() - sample;		// ... and (i+i)/n - lambda([0,x]) = i+1/n - x ...

			discrepance = Math.max(discrepance, Math.max(low, high));	// ... and take the maximum over all these values.
		}

		return discrepance;
	}

	private static void plotDiscrepancyFunction(String label, List<Double> samples) {

		DoubleUnaryOperator lambda = x -> {

			double d = x - (double)samples.stream().filter(y -> y < x).count()/samples.size();

			return d;
		};

		Plot2D plot = new Plot2D(List.of(
				new PlotableFunction2D(0, 1, 100, lambda),
				new PlotablePoints2D("Samples", samples.stream().map(x -> new Point2D(x,0)).collect(Collectors.toList()), null)
				));
		plot.setYRange(-0.5, 0.5)
		.setTitle(label)
		.setXAxisLabel("x")
		.setYAxisLabel("\u03bb([0,x) - |x\u1d62 \u2208 [0,x)| / n");
		plot.show();
	}
}
