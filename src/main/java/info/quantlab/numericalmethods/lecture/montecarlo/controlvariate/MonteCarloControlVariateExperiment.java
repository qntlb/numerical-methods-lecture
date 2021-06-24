package info.quantlab.numericalmethods.lecture.montecarlo.controlvariate;

import net.finmath.exception.CalculationException;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;
import net.finmath.montecarlo.model.ProcessModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.montecarlo.process.MonteCarloProcess;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class MonteCarloControlVariateExperiment {

	public static void main(String[] args) throws CalculationException {

		double initialValue = 1.0;
		double riskFreeRate = 0.05;
		double volatility = 0.20;

		double initialTime = 0.0;
		int numberOfTimeSteps = 1;
		double deltaT = 5;

		int numberOfPaths = 10000000;
		int seed = 3141;

		double maturity = 5.0;
		double strike1 = 1.0;
		double strike2 = 1.6;

		// Model: Black Scholes
		ProcessModel blackScholesModel = new BlackScholesModel(initialValue, riskFreeRate, volatility);

		// Brownian Motion / Numerical Scheme
		TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(initialTime, numberOfTimeSteps, deltaT);
		BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretization, 1, numberOfPaths, seed);
		MonteCarloProcess process = new EulerSchemeFromProcessModel(blackScholesModel, brownianMotion);

		// Monte-Carlo Valuation Model
		MonteCarloAssetModel blackScholesMonteCarloModel = new MonteCarloAssetModel(process);

		// Analytic Value
		double valueAnalytic = AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, volatility, maturity, strike1);


		RandomVariable underlying = blackScholesMonteCarloModel.getAssetValue(maturity, 0);
		RandomVariable numeraireAtPayment = blackScholesMonteCarloModel.getNumeraire(maturity);
		RandomVariable numeraireAtEval	= blackScholesMonteCarloModel.getNumeraire(0.0);

		RandomVariable payoffCall = underlying.sub(strike1).floor(0.0);
		RandomVariable strangePayoff = underlying.sub(strike2).choose(payoffCall, payoffCall.squared().div(strike2-strike1));

		RandomVariable valueExotic = strangePayoff.div(numeraireAtPayment).mult(numeraireAtEval);
		RandomVariable valuePlain = payoffCall.div(numeraireAtPayment).mult(numeraireAtEval);

		double c = valuePlain.sub(valuePlain.average()).mult(valueExotic.sub(valueExotic.average())).getAverage() / valuePlain.getVariance();

		RandomVariable valueControled = valueExotic.sub(valuePlain.sub(valueAnalytic).mult(c));

		EuropeanOption option = new EuropeanOption(maturity, strike1);
		double value = option.getValue(blackScholesMonteCarloModel);

		System.out.println("Plain call analytic valuation..........: " + valueAnalytic);
		System.out.println("Plain call Monte-Carlo valuation (1)...: " + valuePlain.getAverage() + " \t\u00B1 " + valuePlain.getStandardError());
		System.out.println("Plain call Monte-Carlo valuation (2)...: " + value);
		System.out.println("Exotic product Monte-Carlo valuation...: " + valueExotic.getAverage() + " \t\u00B1 " + valueExotic.getStandardError());
		System.out.println("Exotic product controled MC valuation..: " + valueControled.getAverage() + "\t\u00B1 " + valueControled.getStandardError());
	}
}
