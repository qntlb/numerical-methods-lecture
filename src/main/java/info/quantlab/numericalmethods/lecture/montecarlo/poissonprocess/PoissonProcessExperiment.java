package info.quantlab.numericalmethods.lecture.montecarlo.poissonprocess;

import java.util.ArrayList;
import java.util.List;

import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator1D;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;
import net.finmath.plots.DoubleToRandomVariableFunction;
import net.finmath.plots.PlotProcess2D;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

/**
 * Plot the path of a Poisson process.
 * 
 * @author Christian Fries
 */
public class PoissonProcessExperiment {

	public static void main(String[] args) {
		plotPoissonPaths();
	}

	private static void plotPoissonPaths() {

		int numberOfPaths = 1000;
		double maturity = 10.0;
		double lambda = 1.0;

		RandomNumberGenerator1D randomNumberGenerator = new MersenneTwister(3141);
		/*
		 * Part 1: Generate array (paths) containing the list of jump times<maturity.
		 */
		List<List<Double>> jumpTimesPaths = new ArrayList<List<Double>>();
		for(int pathIndex = 0; pathIndex<numberOfPaths; pathIndex++) {
			List<Double> jumpTimes = new ArrayList<>();

			double nextJumpTime = 0;
			while(nextJumpTime < maturity) {

				double uniform = randomNumberGenerator.nextDouble();
				double timeStep = - Math.log(uniform) / lambda;

				nextJumpTime += timeStep;
				if(nextJumpTime < maturity) jumpTimes.add(nextJumpTime);
			}
			jumpTimesPaths.add(jumpTimes);
		}

		/*
		 * Part 2: Generate the function t -> N(t) - lambda t
		 */
		DoubleToRandomVariableFunction process = time -> {
			double[] values = new double[numberOfPaths];
			for(int pathIndex = 0; pathIndex<numberOfPaths; pathIndex++) {
				long count = jumpTimesPaths.get(pathIndex).stream().filter(t -> t <= time).count();
				values[pathIndex] = count - lambda * time;
			}
			return new RandomVariableFromDoubleArray(maturity, values);
		};

		/*
		 * Plot N(t) on a fixed time grid (t_i, N(t_i))
		 */
		int numberOfTimeSteps = 1000;
		double deltaT = 0.01;
		TimeDiscretization td = new TimeDiscretizationFromArray(0.0, numberOfTimeSteps, deltaT);

		// Plot a Scatter of the two Brownian incements.
		var plot = new PlotProcess2D(td, process, 200 /* maxNumberOfPaths */);
		plot.setTitle("Paths of Poisson process").setXAxisLabel("time (t)").setYAxisLabel("value (N(t) - lambda t)");
		plot.show();
	}
}
