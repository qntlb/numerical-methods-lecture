package info.quantlab.numericalmethods.lecture.montecarlo.bermudan;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.montecarlo.process.MonteCarloProcess;
import net.finmath.plots.GraphStyle;
import net.finmath.plots.Plot;
import net.finmath.plots.Plot2D;
import net.finmath.plots.Plotable2D;
import net.finmath.plots.PlotablePoints2D;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretizationFromArray;

public class BermudanOptionExerciseInMonteCarloBlackScholesPlot {

	public static void main(String[] args) throws Exception {

		double initialValue = 100.0;
		double riskFreeRate = 0.05;
		double volatility = 0.30;

		double initialTime = 0.0;
		double timeHorizon = 2.0;
		double dt = 1.0;

		int numberOfPaths = 5000;
		int seed = 3141;

		double maturity1 = 1.0;	// T1
		double strike1 = 120.0;	// K1

		double maturity2 = 2.0;	// T2
		double strike2 = 140.0;	// K2

		BlackScholesModel blackScholesModel = new BlackScholesModel(initialValue, riskFreeRate, volatility);
		BrownianMotion bm = new BrownianMotionFromMersenneRandomNumbers(new TimeDiscretizationFromArray(initialTime, (int)Math.round(timeHorizon/dt), dt), 1, numberOfPaths, seed);
		MonteCarloProcess process = new EulerSchemeFromProcessModel(blackScholesModel, bm);
		MonteCarloAssetModel model = new MonteCarloAssetModel(process);

		RandomVariable stockInT2 = model.getAssetValue(maturity2, 0);		// S(T2)
		RandomVariable stockInT1 = model.getAssetValue(maturity1, 0);		// S(T1)

		RandomVariable valueOption2InT2 = stockInT2.sub(strike2).floor(0.0);	// V2(T2) = max(S(T2)-K,0)
		RandomVariable valueOption1InT1 = stockInT1.sub(strike1).floor(0.0);	// V1(T1) = max(S(T1)-K,0)
		RandomVariable valueOption2InT1 = AnalyticFormulas.blackScholesOptionValue(stockInT1, riskFreeRate, volatility, maturity2-maturity1, strike2);	// V2(T1)

		// Convert all time T-values to numeraire relative values by dividing by N(T)
		RandomVariable valueRelativeOption2InT2 = valueOption2InT2.div(model.getNumeraire(maturity2));
		RandomVariable valueRelativeOption1InT1 = valueOption1InT1.div(model.getNumeraire(maturity1));
		RandomVariable valueRelativeOption2InT1 = valueOption2InT1.div(model.getNumeraire(maturity1));

		// Exercise criteria: V2(T1) > V1(T1) ? V2(T2) ; V1(T1)
		RandomVariable bermudanPathwiseValueAdmissible = valueRelativeOption2InT1.sub(valueRelativeOption1InT1)
				.choose(valueRelativeOption2InT2, valueRelativeOption1InT1);

		// Perfect foresight: V2(T2) > V1(T1) ? V2(T2) ; V1(T1)
		RandomVariable bermudanPathwiseValueForesight = valueRelativeOption2InT2.sub(valueRelativeOption1InT1)
				.choose(valueRelativeOption2InT2, valueRelativeOption1InT1);

		var bermudanValue				= bermudanPathwiseValueAdmissible.mult(model.getNumeraire(initialTime)).expectation().doubleValue();
		var bermudanValueWithForsight	= bermudanPathwiseValueForesight.mult(model.getNumeraire(initialTime)).expectation().doubleValue();

		System.out.println("Bermudan value with admissible exercise strategy...: " + bermudanValue);
		System.out.println("Bermudan value with perfect forsight (wrong).......: " + bermudanValueWithForsight);

		/*
		 * Plot the stuff
		 */
		Plotable2D continuationValues			= PlotablePoints2D.of("Continuation Value max(S(T\u2082)-K\u2082, 0)",
				stockInT1, valueRelativeOption2InT2, new GraphStyle(new Rectangle(-1,-1,2,2), null, Color.RED));

		Plotable2D expectedContinuationValue	= PlotablePoints2D.of("Black Scholes Value of Option on S(T\u2082)-K\u2082, conditional to T\u2081",
				stockInT1, valueRelativeOption2InT1, new GraphStyle(new Rectangle(-3,-3,5,5),null, Color.BLUE));

		Plot plotCondExpEstimate = new Plot2D(
				List.of(
						continuationValues,
						expectedContinuationValue)
				)
				.setYRange(-5, 150)
				.setIsLegendVisible(true)
				.setXAxisLabel("S(T\u2081)")
				.setYAxisLabel("V\u2082(T\u2081), V\u2082(T\u2082)")
				.setTitle("Time T\u2081 and T\u2082 values related to a Bermudan option with exercises in T\u2081 and T\u2082.");
		plotCondExpEstimate.show();
//		plotCondExpEstimate.saveAsJPG(new File("BermudanT1T2Values.jpg"),1200,675);

		Plotable2D exerciseValues = PlotablePoints2D.of("Underlying 1 (S(T\u2081)-K\u2081)",
				stockInT1, valueRelativeOption1InT1, new GraphStyle(new Rectangle(-3,-3,5,5), null, Color.GREEN));

		Plotable2D bermudanPathwiseValueAdmissiblePlot = PlotablePoints2D.of("Bermudan pathwise value",
				stockInT1, bermudanPathwiseValueAdmissible, new GraphStyle(new Rectangle(-1,-1,2,2), null, Color.CYAN));
		
		Plot plotBermudanExercise = new Plot2D(
				List.of(
						bermudanPathwiseValueAdmissiblePlot,
						continuationValues,
						exerciseValues,
						expectedContinuationValue)
				)
				.setYRange(-5, 150)
				.setIsLegendVisible(true)
				.setXAxisLabel("S(T\u2081)")
				.setYAxisLabel("V\u2081(T\u2081), V\u2082(T\u2081), V\u2082(T\u2082)")
				.setTitle("Time T\u2081 and T\u2082 values related to a Bermudan option with exercises in T\u2081 and T\u2082.");
		plotBermudanExercise.show();
//		plotBermudanExercise.saveAsJPG(new File("BermudanT1T2ExericseValueAdmissible.jpg"),1200,675);

		Plotable2D bermudanPathwiseValueForesightPlot = PlotablePoints2D.of("Bermudan pathwise value with foresight",
				stockInT1, bermudanPathwiseValueForesight, new GraphStyle(new Rectangle(-1,-1,2,2), null, Color.CYAN));

		Plot plotBermudanExerciseWithForesight = new Plot2D(
				List.of(
						bermudanPathwiseValueForesightPlot,
						continuationValues,
						exerciseValues,
						expectedContinuationValue)
				)
				.setYRange(-5, 150)
				.setIsLegendVisible(true)
				.setXAxisLabel("S(T\u2081)")
				.setYAxisLabel("V\u2081(T\u2081), V\u2082(T\u2081), V\u2082(T\u2082)")
				.setTitle("Time T\u2081 and T\u2082 values related to a Bermudan option with exercises in T\u2081 and T\u2082.");
		plotBermudanExerciseWithForesight.show();
//		plotBermudanExerciseWithForesight.saveAsJPG(new File("BermudanT1T2ExerciseValueWithForesight.jpg"),1200,675);
	}
}
