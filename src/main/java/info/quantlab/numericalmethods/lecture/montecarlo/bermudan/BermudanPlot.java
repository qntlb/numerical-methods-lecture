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
import net.finmath.plots.Plot2D;
import net.finmath.plots.PlotablePoints2D;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretizationFromArray;

public class BermudanPlot {

	public static void main(String[] args) throws Exception {
		
		double intialValue = 100.0;
		double riskFreeRate = 0.00;
		double volatility = 0.30;

		double timeHorizon = 2.0;
		double dt = 1.0;

		int numberOfPaths = 5000;
		int seed = 3141;
		
		double maturity1 = 1.0;
		double strike1 = 120.0;

		double maturity2 = 2.0;
		double strike2 = 140.0;

		MonteCarloAssetModel model = getBlackScholesMonteCarloModel(intialValue, riskFreeRate, volatility, timeHorizon, dt, numberOfPaths, seed);

		RandomVariable stockInT2 = model.getAssetValue(2.0, 0);		// S(T2)
		RandomVariable stockInT1 = model.getAssetValue(1.0, 0);		// S(T1)
		
		RandomVariable valueOption2InT2 = stockInT2.sub(strike2).floor(0.0);	// max(S(T2)-K,0)
		RandomVariable valueOption1InT1 = stockInT1.sub(strike1).floor(0.0);	// max(S(T1)-K,0)
		
		RandomVariable valueOption2InT1 = AnalyticFormulas.blackScholesOptionValue(
				stockInT1, riskFreeRate, volatility, maturity2-maturity1, strike2);

		RandomVariable bermudanPathwiseValueCorrect = valueOption2InT1.sub(valueOption1InT1).choose(valueOption2InT2, valueOption1InT1);

		
		RandomVariable bermudanPathwiseValueForesight = valueOption2InT2.sub(valueOption1InT1).choose(valueOption2InT2, valueOption1InT1);
		
		/*
		 * Plot the stuff
		 */		
		PlotablePoints2D scatterContinuation = PlotablePoints2D.of("Continuation Value max(S(T\u2082)-K\u2082, 0)", stockInT1, valueOption2InT2, new GraphStyle(new Rectangle(-1,-1,2,2), null, Color.RED));
		PlotablePoints2D scatterExercise = PlotablePoints2D.of("Underlying 1 (S(T\u2081)-K\u2081)", stockInT1, valueOption1InT1, new GraphStyle(new Rectangle(-3,-3,5,5), null, Color.GREEN));
		PlotablePoints2D scatterBlackScholes = PlotablePoints2D.of("Black Scholes Value of Option on S(T\u2082)-K\u2082", stockInT1, valueOption2InT1, new GraphStyle(new Rectangle(-3,-3,5,5),null, Color.BLUE));

		PlotablePoints2D scatterBermudanPathwiseValueForesight = PlotablePoints2D.of("Exercise value with foresight", stockInT1, bermudanPathwiseValueForesight, new GraphStyle(new Rectangle(-1,-1,2,2), null, new Color(0.0f, 0.66f, 0.0f)));
		PlotablePoints2D scatterBermudanPathwiseValueCorrect = PlotablePoints2D.of("Bermudan exercise value", stockInT1, bermudanPathwiseValueCorrect, new GraphStyle(new Rectangle(-1,-1,2,2), null, new Color(0.0f, 0.66f, 0.0f)));

		new Plot2D(List.of(
				scatterBermudanPathwiseValueForesight,
				scatterContinuation, scatterExercise, scatterBlackScholes))
				.setYRange(-5, 150)
				.setIsLegendVisible(true)
				.setXAxisLabel("S(T\u2081)")
				.setYAxisLabel("V\u2081(T\u2081), V\u2082(T\u2081), V\u2082(T\u2082)")
				.setTitle("Time T\u2081 and T\u2082 values related to a Bermudan option with exercises in T\u2081 and T\u2082.")
				.show();
	}

	private static MonteCarloAssetModel getBlackScholesMonteCarloModel(double initialValue, double riskFreeRate, double volatility, double timeHorizon, double dt, int numberOfPaths, int seed) {

		BlackScholesModel blackScholesModel = new BlackScholesModel(initialValue, riskFreeRate, volatility);
		BrownianMotion bm = new BrownianMotionFromMersenneRandomNumbers(new TimeDiscretizationFromArray(0.0, (int)Math.round(timeHorizon/dt), dt),
				1, numberOfPaths, seed);
		MonteCarloProcess process = new EulerSchemeFromProcessModel(blackScholesModel, bm);
	
		return new MonteCarloAssetModel(process);
	}

}
