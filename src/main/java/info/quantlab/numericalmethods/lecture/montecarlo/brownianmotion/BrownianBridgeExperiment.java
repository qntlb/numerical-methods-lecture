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

		plotBrownianBridgePaths(2);

	}

	/*
	 * Show a Brownian Bridge.
	 */
	private static void plotBrownianBridgePaths(int bridgeNumberOfTimeSteps) {

		// First the Brownian motion (inside of which we will bridge)
		final int numberOfTimeSteps = 2;
		final double deltaT = 1.0;
		final int numberOfPaths = 10;

		// Two steps W(t_0=0), W(t_1), W(t_2)
		final TimeDiscretization td = new TimeDiscretizationFromArray(0.0, numberOfTimeSteps, deltaT);
		final BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(td, 1, numberOfPaths, 3231);

		// Start value W(t_1) and end value W(t_2)
		final RandomVariable valuesStart = brownianMotion.getBrownianIncrement(0, 0); // W(1) = Delta W(0)
		final RandomVariable valuesEnd = valuesStart.add(brownianMotion.getBrownianIncrement(1, 0)); // W(2) = W(1) + Delta W(1)


		// Now: the Brownian bridge
		final double bridgeDeltaT = 1.0/bridgeNumberOfTimeSteps;
		final TimeDiscretization bridgeTimeDiscretization = new TimeDiscretizationFromArray(1.0, bridgeNumberOfTimeSteps, bridgeDeltaT);

		final BrownianMotion brownianBridge = new BrownianBridge(bridgeTimeDiscretization, numberOfPaths, 1, valuesStart, valuesEnd);

		final RandomVariable[] processDiscrete = new RandomVariable[bridgeTimeDiscretization.getNumberOfTimes()];
		processDiscrete[0] = valuesStart;
		for(int i=0; i<bridgeTimeDiscretization.getNumberOfTimeSteps();i++) {
			processDiscrete[i+1] = processDiscrete[i].add(brownianBridge.getBrownianIncrement(i, 0));
		}

		// Plot the bridge
		final DoubleToRandomVariableFunction process = time -> processDiscrete[bridgeTimeDiscretization.getTimeIndex(time)];

		// Plot a Scatter of the two Brownian incements.
		final var plot = new PlotProcess2D(bridgeTimeDiscretization, process, 200 /* maxNumberOfPaths */);
		plot.setTitle("Paths of Brownian bridge").setXAxisLabel("time (t)").setYAxisLabel("value (W(t))");
		plot.show();
	}

	private static void plotBrownianBridgePathsAnimated() throws IOException {
		final int numberOfTimeSteps = 2;
		final double deltaT = 1.0;
		final int numberOfPaths = 10;

		final TimeDiscretization td = new TimeDiscretizationFromArray(0.0, numberOfTimeSteps, deltaT);
		final BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(td, 1, numberOfPaths, 3231);

		final RandomVariable valuesStart = brownianMotion.getBrownianIncrement(0, 0);
		final RandomVariable valuesEnd = valuesStart.add(brownianMotion.getBrownianIncrement(1, 0));

		for(int numberOfTimeSteps2=1; numberOfTimeSteps2<=100; numberOfTimeSteps2++) {
			//		int numberOfTimeSteps2 = 2;
			final double deltaT2 = 1.0/numberOfTimeSteps2;
			final TimeDiscretization td2 = new TimeDiscretizationFromArray(1.0, numberOfTimeSteps2, deltaT2);
			final BrownianMotion brownianBridge = new BrownianBridge(td2, numberOfPaths, 1, valuesStart, valuesEnd);

			final RandomVariable[] processDiscrete = new RandomVariable[td2.getNumberOfTimes()];
			processDiscrete[0] = valuesStart;
			for(int i=0; i<td2.getNumberOfTimeSteps();i++) {
				processDiscrete[i+1] = processDiscrete[i].add(brownianBridge.getBrownianIncrement(i, 0));
			}

			final DoubleToRandomVariableFunction process = time -> processDiscrete[td2.getTimeIndex(time)];

			// Plot a Scatter of the two Brownian incements.
			final var plot = new PlotProcess2D(td2, process, 200 /* maxNumberOfPaths */);
			plot.setTitle("Paths of Brownian bridge").setXAxisLabel("time (t)").setYAxisLabel("value (W(t))");
			plot.show();
			plot.saveAsJPG(new File("images/plot-"+String.format("%03d", numberOfTimeSteps2)+".png"), 1600, 1000);
		}
	}
}
