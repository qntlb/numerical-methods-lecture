package info.quantlab.numericalmethods.lecture.montecarlo.brownianmotion;

import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.plots.DoubleToRandomVariableFunction;
import net.finmath.plots.PlotProcess2D;
import net.finmath.plots.Plots;
import net.finmath.stochastic.RandomVariable;
import net.finmath.stochastic.Scalar;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class BrownianMotionExperiment {

	public static void main(String[] args) {

		plotBrownianIncrementsScatter();
		plotBrownianPaths();

	}

	private static void plotBrownianIncrementsScatter() {
		int numberOfTimeSteps = 300;
		double deltaT = 0.01;
		int numberOfPaths = 1000;

		TimeDiscretization td = new TimeDiscretizationFromArray(0.0, numberOfTimeSteps, deltaT);
		BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(td, 1, numberOfPaths, 3231);

		RandomVariable bm1 = brownianMotion.getBrownianIncrement(0, 0);
		RandomVariable bm2 = brownianMotion.getBrownianIncrement(1, 0);

		// Plot a Scatter of the two Brownian increments.
		var plot = Plots.createScatter(bm1, bm2, -1, 1);
		plot.setTitle("Two independent (Brownian) increments").setXAxisLabel("\u0394 W(0)").setYAxisLabel("\u0394 W(0.01)");
		plot.show();
	}

	private static void plotBrownianPaths() {
		int numberOfTimeSteps = 300;
		double deltaT = 0.01;
		int numberOfPaths = 1000;

		TimeDiscretization td = new TimeDiscretizationFromArray(0.0, numberOfTimeSteps, deltaT);
		BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(td, 1, numberOfPaths, 3231);

		RandomVariable[] processDiscrete = new RandomVariable[td.getNumberOfTimes()];
		processDiscrete[0] = new Scalar(0.0);
		for(int i=0; i<td.getNumberOfTimeSteps();i++) {
			processDiscrete[i+1] = processDiscrete[i].add(brownianMotion.getBrownianIncrement(i, 0));
		}

		DoubleToRandomVariableFunction process = time -> processDiscrete[td.getTimeIndex(time)];

		// Plot a Scatter of the two Brownian incements.
		var plot = new PlotProcess2D(td, process, 200 /* maxNumberOfPaths */);
		plot.setTitle("Paths of Brownian motion").setXAxisLabel("time (t)").setYAxisLabel("value (W(t))");
		plot.show();
	}
}
