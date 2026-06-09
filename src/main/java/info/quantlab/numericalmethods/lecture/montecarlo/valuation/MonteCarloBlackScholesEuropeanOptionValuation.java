package info.quantlab.numericalmethods.lecture.montecarlo.valuation;

import net.finmath.exception.CalculationException;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AssetMonteCarloProduct;
import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;
import net.finmath.montecarlo.model.ProcessModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.montecarlo.process.MonteCarloProcess;
import net.finmath.plots.DoubleToRandomVariableFunction;
import net.finmath.plots.PlotProcess2D;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class MonteCarloBlackScholesEuropeanOptionValuation {

	public static void main(String[] args) throws CalculationException {
		final double initialValue = 100.0;
		final double riskFreeRate = 0.05;
		final double volatility = 0.20;

		final double initialTime = 0.0;
		final int numberOfTimeSteps = 100;
		final double deltaT = 0.05;

		final int numberOfPaths = 2000000;
		final int numberOfFactors = 1;
		final int seed = 3141;

		final double maturity = 5.0;
		final double strike = 105;


		final ProcessModel blackScholesModel = new BlackScholesModel(initialValue, riskFreeRate, volatility);

		final TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(initialTime, numberOfTimeSteps, deltaT);

		final BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretization, numberOfFactors, numberOfPaths, seed);

		final MonteCarloProcess process = new EulerSchemeFromProcessModel(blackScholesModel, brownianMotion);

		final MonteCarloAssetModel blackScholesMonteCarloModel = new MonteCarloAssetModel(process);

		final DoubleToRandomVariableFunction processPaths = t -> blackScholesMonteCarloModel.getAssetValue(t, 0);
		(new PlotProcess2D(timeDiscretization, processPaths, 100))
		.setTitle("Path of a Black Scholes Model")
		.setXAxisLabel("Time t")
		.setYAxisLabel("Stock Value S(t)")
		.show();

		final AssetMonteCarloProduct option = new EuropeanOption(maturity, strike);

		final double value = option.getValue(initialTime, blackScholesMonteCarloModel).getAverage();

		final double valueAnalytic = AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, volatility, maturity, strike);
		System.out.println("Monte-Carlo valuation...: " + value);
		System.out.println("Analytic valuation......: " + valueAnalytic);
	}

}
