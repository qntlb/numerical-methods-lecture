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
 * Plot the path of a (compensated) Poisson process.
 *
 * @author Christian Fries
 */
public class PoissonProcessExperiment {

	public static void main(String[] args) {
		plotPoissonPaths();
	}

	private static void plotPoissonPaths() {

		final int numberOfPaths = 1000;
		final double maturity = 10.0;
		final double lambda = 1.0;

		final RandomNumberGenerator1D randomNumberGenerator = new MersenneTwister(3141);
		/*
		 * Part 1: Generate array (paths) containing the list of jump times<maturity.
		 * List of List: Inner list: the jump times. Outer list: the samples (paths).
		 */
		final List<List<Double>> jumpTimesPaths = new ArrayList<>();
		for(int pathIndex = 0; pathIndex<numberOfPaths; pathIndex++) {
			final List<Double> jumpTimes = new ArrayList<>();

			double nextJumpTime = 0;			// T_0
			while(nextJumpTime < maturity) {

				// Sample exponential distributed timeStep
				final double uniform = randomNumberGenerator.nextDouble();
				final double timeStep = - Math.log(uniform) / lambda;

				nextJumpTime += timeStep;
				if(nextJumpTime <= maturity) {
					jumpTimes.add(nextJumpTime);
				}

				System.out.print(nextJumpTime + ", ");
			}
			jumpTimesPaths.add(jumpTimes);

			System.out.println();
		}

		/*
		 * Part 2: Generate the function t -> M(t) = N(t) - lambda t
		 *
		 * M(t) for a fixed time t = <code>time</code>.
		 */
		final DoubleToRandomVariableFunction process = time -> {
			final double[] values = new double[numberOfPaths];
			for(int pathIndex = 0; pathIndex<numberOfPaths; pathIndex++) {
				final long count = jumpTimesPaths.get(pathIndex).stream().filter(t -> t <= time).count();	// Count T_{i} <= t
				values[pathIndex] = count - lambda * time;
			}
			return new RandomVariableFromDoubleArray(maturity, values);
		};

		/*
		 * Plot N(t) on a fixed time grid (t_i, N(t_i))
		 */
		final int numberOfTimeSteps = 1000;
		final double deltaT = 0.01;
		final TimeDiscretization td = new TimeDiscretizationFromArray(0.0, numberOfTimeSteps, deltaT);

		// Plot a Scatter of the two Brownian incements.
		final var plot = new PlotProcess2D(td, process, 200 /* maxNumberOfPaths */);
		plot.setTitle("Paths of Poisson process").setXAxisLabel("time (t)").setYAxisLabel("value (N(t) - lambda t)");
		plot.show();
	}
}
