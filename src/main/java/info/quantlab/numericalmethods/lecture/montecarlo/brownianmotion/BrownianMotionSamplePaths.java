package info.quantlab.numericalmethods.lecture.montecarlo.brownianmotion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator1D;
import net.finmath.functions.NormalDistribution;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;
import net.finmath.plots.DoubleToRandomVariableFunction;
import net.finmath.plots.Plot2D;
import net.finmath.plots.PlotProcess2D;
import net.finmath.time.TimeDiscretizationFromArray;

public class BrownianMotionSamplePaths {

	private final int numberOfTimeSteps = 100;
	private final double timeStep = 0.01;

	private final int numberOfPaths = 1000;
	private final int seed = 3141;

	public static void main(String[] args) {

		(new BrownianMotionSamplePaths()).plot();
	}

	private void plot() {

		final RandomNumberGenerator1D randomNumberGenerator = new MersenneTwister(seed);

		final double[] timeDiscretization = new double[numberOfTimeSteps+1];
		for(int i=0; i<timeDiscretization.length; i++) {
			timeDiscretization[i] = i * timeStep;
		}

		final List<double[]> brownianMotionSamplePaths = new ArrayList<>();

		for(int pathIndex = 0; pathIndex<numberOfPaths; pathIndex++) {

			// Allocate array W(t_i, 𝜔) on the current path 𝜔
			final double[] brownianMotionSamplePath = new double[timeDiscretization.length];

			// Initial value (but the array is initialized to 0 anyway)
			brownianMotionSamplePath[0] = 0.0;

			for(int timeIndex = 0; timeIndex<timeDiscretization.length-1; timeIndex++) {

				final double uniform = randomNumberGenerator.nextDouble();

				// Standard normal
				final double normal = NormalDistribution.inverseCumulativeDistribution(uniform);

				final double timeStep = timeDiscretization[timeIndex+1] - timeDiscretization[timeIndex];
				final double brownianIncrement = Math.sqrt(timeStep) * normal;

				brownianMotionSamplePath[timeIndex+1] = brownianMotionSamplePath[timeIndex] + brownianIncrement;
			}

			brownianMotionSamplePaths.add(brownianMotionSamplePath);
		}


		/*
		 * Plot the sample paths.
		 * We use two different ways/classes to create the plot. One will draw dots, the other one not.
		 */
		final int numberOfPathsToPlot = 100;

		// Array of functions that map t to W(t,𝜔) (the array is over all 𝜔), i.e., array of paths.
		final DoubleUnaryOperator[] doubleUnaryOperators = brownianMotionSamplePaths.stream().limit(numberOfPathsToPlot).map(
				samplePath -> {
					final DoubleUnaryOperator operator = t -> samplePath[getTimeIndexLessOrEqual(timeDiscretization, t)];
					return operator;
				}).toArray(DoubleUnaryOperator[]::new);

		// Plot the sample paths
		final Plot2D plot = new Plot2D(
				timeDiscretization[0],
				timeDiscretization[timeDiscretization.length-1],
				timeDiscretization.length /* points */,
				doubleUnaryOperators);
		plot.setTitle("Brownian Motion (observed at discrete times for selected 𝜔)")
		.setXAxisLabel("time t")
		.setYAxisLabel("W(t,𝜔)");
		plot.show();

		// Function that maps t to W(t), i.e., function from time to random variable.
		final DoubleToRandomVariableFunction timeToRandomVariable = t ->
		{ return new RandomVariableFromDoubleArray(t, brownianMotionSamplePaths.stream().mapToDouble(
				samplePath -> samplePath[getTimeIndexLessOrEqual(timeDiscretization, t)]
				).toArray());
		};

		final PlotProcess2D process = new PlotProcess2D(new TimeDiscretizationFromArray(Arrays.stream(timeDiscretization), timeStep/10), timeToRandomVariable, 100);
		process.setTitle("Brownian Motion (observed at discrete times for selected 𝜔)")
		.setXAxisLabel("time t")
		.setYAxisLabel("W(t,𝜔)");
		process.show();
	}

	private int getTimeIndexLessOrEqual(double[] timeDiscretization, double time) {
		int timeIndex = Arrays.binarySearch(timeDiscretization, time);
		// If there is no match, we get a negative value such that -timeIndex-1 is the index of the next time being smaller
		if(timeIndex < 0) {
			timeIndex = -timeIndex-1;
		}
		return timeIndex;
	}
}
