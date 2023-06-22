package info.quantlab.numericalmethods.lecture.montecarlo.brownianmotion;

import java.io.File;
import java.io.IOException;

import net.finmath.montecarlo.BrownianBridge;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.plots.DoubleToRandomVariableFunction;
import net.finmath.plots.PlotProcess2D;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class BrownianBridgeExperiment {

	public static void main(String[] args) throws IOException {

		plotBrownianBridgePaths(100);

	}

	/*
	 * Show a Brownian Bridge.
	 */
	private static void plotBrownianBridgePaths(int bridgeNumberOfTimeSteps) {
		int numberOfTimeSteps = 2;
		double deltaT = 1.0;
		int numberOfPaths = 10;

		// W(0), W(1), W(2)
		TimeDiscretization td = new TimeDiscretizationFromArray(0.0, numberOfTimeSteps, deltaT);
		BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(td, 1, numberOfPaths, 3231);

		RandomVariable valuesStart = brownianMotion.getBrownianIncrement(0, 0); // W(1) = Delta W(0)
		RandomVariable valuesEnd = valuesStart.add(brownianMotion.getBrownianIncrement(1, 0)); // W(2) = W(1) + Delta W(1)

		double bridgeDeltaT = 1.0/bridgeNumberOfTimeSteps;
		TimeDiscretization bridgeTimeDiscretization = new TimeDiscretizationFromArray(1.0, bridgeNumberOfTimeSteps, bridgeDeltaT);

		BrownianMotion brownianBridge = new BrownianBridge(bridgeTimeDiscretization, numberOfPaths, 1, valuesStart, valuesEnd);

		RandomVariable[] processDiscrete = new RandomVariable[bridgeTimeDiscretization.getNumberOfTimes()];
		processDiscrete[0] = valuesStart;
		for(int i=0; i<bridgeTimeDiscretization.getNumberOfTimeSteps();i++) {
			processDiscrete[i+1] = processDiscrete[i].add(brownianBridge.getBrownianIncrement(i, 0));
		}

		DoubleToRandomVariableFunction process = time -> processDiscrete[bridgeTimeDiscretization.getTimeIndex(time)];

		// Plot a Scatter of the two Brownian incements.
		var plot = new PlotProcess2D(bridgeTimeDiscretization, process, 200 /* maxNumberOfPaths */);
		plot.setTitle("Paths of Brownian bridge").setXAxisLabel("time (t)").setYAxisLabel("value (W(t))");
		plot.show();
	}

	private static void plotBrownianBridgePathsAnimated() throws IOException {
		int numberOfTimeSteps = 2;
		double deltaT = 1.0;
		int numberOfPaths = 10;

		TimeDiscretization td = new TimeDiscretizationFromArray(0.0, numberOfTimeSteps, deltaT);
		BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(td, 1, numberOfPaths, 3231);

		RandomVariable valuesStart = brownianMotion.getBrownianIncrement(0, 0);
		RandomVariable valuesEnd = valuesStart.add(brownianMotion.getBrownianIncrement(1, 0));

		for(int numberOfTimeSteps2=1; numberOfTimeSteps2<=100; numberOfTimeSteps2++) {
			//		int numberOfTimeSteps2 = 2;
			double deltaT2 = 1.0/numberOfTimeSteps2;
			TimeDiscretization td2 = new TimeDiscretizationFromArray(1.0, numberOfTimeSteps2, deltaT2);
			BrownianMotion brownianBridge = new BrownianBridge(td2, numberOfPaths, 1, valuesStart, valuesEnd);

			RandomVariable[] processDiscrete = new RandomVariable[td2.getNumberOfTimes()];
			processDiscrete[0] = valuesStart;
			for(int i=0; i<td2.getNumberOfTimeSteps();i++) {
				processDiscrete[i+1] = processDiscrete[i].add(brownianBridge.getBrownianIncrement(i, 0));
			}

			DoubleToRandomVariableFunction process = time -> processDiscrete[td2.getTimeIndex(time)];

			// Plot a Scatter of the two Brownian incements.
			var plot = new PlotProcess2D(td2, process, 200 /* maxNumberOfPaths */);
			plot.setTitle("Paths of Brownian bridge").setXAxisLabel("time (t)").setYAxisLabel("value (W(t))");
			plot.show();
			plot.saveAsJPG(new File("images/plot-"+String.format("%03d", numberOfTimeSteps2)+".png"), 1600, 1000);
		}
	}
}
