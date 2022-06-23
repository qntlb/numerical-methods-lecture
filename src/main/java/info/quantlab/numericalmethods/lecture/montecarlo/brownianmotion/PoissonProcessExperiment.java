package info.quantlab.numericalmethods.lecture.montecarlo.brownianmotion;

import java.util.ArrayList;
import java.util.List;

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

		/*
		 * Part 1: Generate an array (numberOfPaths) containing the list of jump times < maturity.
		 */
		List<List<Double>> jumpTimesPaths = new ArrayList<List<Double>>();
		for(int pathIndex = 0; pathIndex<numberOfPaths; pathIndex++) {
			List<Double> jumpTimes = new ArrayList<>();

			double nextJumpTime = 0;
			while(nextJumpTime < maturity) {

				double uniform = Math.random();
				double timeStep = - Math.log(uniform) / lambda;

				nextJumpTime += timeStep;

				if(nextJumpTime < maturity) jumpTimes.add(nextJumpTime);
			}
			jumpTimesPaths.add(jumpTimes);
		}

		/*
		 * Part 2: Generate the function t -> N(t)
		 */
		DoubleToRandomVariableFunction process = time -> {
			double[] values = new double[numberOfPaths];
			for(int pathIndex = 0; pathIndex<numberOfPaths; pathIndex++) {
				int count = 0;
				for(Double jumpTime : jumpTimesPaths.get(pathIndex)) {
					if(jumpTime > time) break;
					count++;
				}
				values[pathIndex] = count - lambda * time;
			}
			return new RandomVariableFromDoubleArray(0.0, values);
		};

		int numberOfTimeSteps = 1000;
		double deltaT = 0.01;
		TimeDiscretization td = new TimeDiscretizationFromArray(0.0, numberOfTimeSteps, deltaT);

		// Plot a Scatter of the two Brownian incements.
		var plot = new PlotProcess2D(td, process, 200 /* maxNumberOfPaths */);
		plot.setTitle("Paths of Poisson process").setXAxisLabel("time (t)").setYAxisLabel("value (W(t))");
		plot.show();
	}
}
