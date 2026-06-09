package info.quantlab.numericalmethods.lecture.montecarlo;

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

/**
 * A small experiment for a control variate for an exotic option payoff using a European option
 * under the Black-Scholes model as a control.
 *
 * @author Christian Fries
 */
public class MonteCarloBlackScholesExoticPayoffControlVariateExperiment {

	public static void main(String[] args) throws CalculationException {

		final double initialValue = 1.0;
		final double riskFreeRate = 0.05;
		final double volatility = 0.20;

		final double initialTime = 0.0;
		final int numberOfTimeSteps = 10;
		final double deltaT = 0.5;

		final int numberOfPaths = 1000000;	// 1E-6
		final int seed = 3141;

		final double maturity = 5.0;
		final double strike1 = 1.0;		// K_1
		final double strike2 = 1.6;		// K_2

		// Model: Black Scholes
		final ProcessModel blackScholesModel = new BlackScholesModel(initialValue, riskFreeRate, volatility);

		// Brownian Motion / Numerical Scheme
		final TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(initialTime, numberOfTimeSteps, deltaT);
		final BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretization, 1, numberOfPaths, seed);
		final MonteCarloProcess process = new EulerSchemeFromProcessModel(blackScholesModel, brownianMotion);

		// Monte-Carlo Valuation Model
		final MonteCarloAssetModel blackScholesMonteCarloModel = new MonteCarloAssetModel(process);

		// Analytic Value
		final double valueAnalytic = AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, volatility, maturity, strike1);


		final RandomVariable underlying = blackScholesMonteCarloModel.getAssetValue(maturity, 0);
		final RandomVariable numeraireAtPayment = blackScholesMonteCarloModel.getNumeraire(maturity);
		final RandomVariable numeraireAtEval	= blackScholesMonteCarloModel.getNumeraire(0.0);

		final RandomVariable payoffCall = underlying.sub(strike1).floor(0.0);
		final RandomVariable strangePayoff = underlying.sub(strike2).choose(payoffCall, payoffCall.squared().div(strike2-strike1));

		final RandomVariable valueExotic = strangePayoff.div(numeraireAtPayment).mult(numeraireAtEval);
		final RandomVariable valuePlain = payoffCall.div(numeraireAtPayment).mult(numeraireAtEval);

		final double covXY = valueExotic.sub(valueExotic.average()).mult(valuePlain.sub(valuePlain.average())).getAverage();
		final double varY = valuePlain.getVariance();
		final double c = covXY/varY;		// A guess

		// Z = X - c*(Y - mu_Y)
		final RandomVariable valueExoticControlled = valueExotic.sub(valuePlain.sub(valueAnalytic).mult(c));

		// Alternative way to calculate a european option - just to check
		final EuropeanOption option = new EuropeanOption(maturity, strike1);
		final double value = option.getValue(blackScholesMonteCarloModel);

		System.out.println("Plain European call option:");
		System.out.println("\tplain call analytic valuation....................: " + valueAnalytic);
		System.out.println("\tplain call Monte-Carlo valuation.................: " + valuePlain.getAverage() + " \t\u00B1 " + valuePlain.getStandardError());
		System.out.println("\tplain call Monte-Carlo valuation (alternative)...: " + value);
		System.out.println();
		System.out.println("Plain European call option:");
		System.out.println("\texotic product Monte-Carlo valuation.............: " + valueExotic.getAverage() + " \t\u00B1 " + valueExotic.getStandardError());
		System.out.println("\texotic product controled MC valuation............: " + valueExoticControlled.getAverage() + "\t\u00B1 " + valueExoticControlled.getStandardError());
	}
}
