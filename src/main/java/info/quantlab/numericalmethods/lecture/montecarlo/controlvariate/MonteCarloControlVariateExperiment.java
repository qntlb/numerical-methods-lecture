package info.quantlab.numericalmethods.lecture.montecarlo.controlvariate;

import net.finmath.exception.CalculationException;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import net.finmath.montecarlo.model.ProcessModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.montecarlo.process.MonteCarloProcess;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

/**
 * Experiment on control variates.
 *
 * We value an exotic option without and with a control variate constructed from a plain option.
 *
 * The payoff of the exotic option is
 * <ul>
 * 	<li>
 * 		S(T)-K1 for K2 &lt; S(T)
 * 	</li>
 * 	<li>
 * 		(S(T)-K1)^2/(K2-K1) for K1 &lt; S(T) &lt; K2
 * 	</li>
 * 	<li>
 * 		0 for S(T) &lt; K1 .
 * 	</li>
 * </ul>
 *
 * @author Christian Fries
 */
public class MonteCarloControlVariateExperiment {

	public static void main(String[] args) throws CalculationException {

		double initialValue	= 1.0;
		double riskFreeRate	= 0.05;
		double volatility	= 0.20;

		double maturity	= 5.0;
		double strike1	= 1.0;
		double strike2	= 1.6;

		double initialTime		= 0.0;
		int numberOfTimeSteps	= 1;
		double deltaT			= (maturity-initialTime)/numberOfTimeSteps;

		int numberOfPaths = 10000000; // 10 mio
		int seed = 3141;

		// Model: Black Scholes
		ProcessModel blackScholesModel = new BlackScholesModel(initialValue, riskFreeRate, volatility);

		// Brownian Motion
		TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(initialTime, numberOfTimeSteps, deltaT);
		BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretization, 1, numberOfPaths, seed);

		// Numerical Scheme
		MonteCarloProcess process = new EulerSchemeFromProcessModel(blackScholesModel, brownianMotion);

		// Monte-Carlo Valuation Model
		AssetModelMonteCarloSimulationModel blackScholesMonteCarloModel = new MonteCarloAssetModel(process);

		// Valuation of plain and exotic payoff
		RandomVariable underlying = blackScholesMonteCarloModel.getAssetValue(maturity, 0);		// S(T)
		RandomVariable numeraireAtPayment = blackScholesMonteCarloModel.getNumeraire(maturity);	// N(T)
		RandomVariable numeraireAtEval	= blackScholesMonteCarloModel.getNumeraire(initialTime);		// N(t)

		// Plain option payoff V(T) = max(S(T)-K1, 0)
		RandomVariable payoffPlain = underlying.sub(strike1).floor(0.0);						// V(T) for plain option

		// Exotic option payoff V(T) = S(T)-K2 > 0 ? max(S(T)-K1, 0) : (max(S(T)-K1, 0)^2 / (K2-K1)
		RandomVariable payoffExotic = underlying.sub(strike2).choose(payoffPlain, payoffPlain.squared().div(strike2-strike1));		// V(T) for exotic option

		// Value of the Exotic Option (without control)
		RandomVariable valueExotic = payoffExotic.div(numeraireAtPayment).mult(numeraireAtEval);// X

		// Value of the Plain Option
		RandomVariable valuePlain = payoffPlain.div(numeraireAtPayment).mult(numeraireAtEval);	// Y

		// Analytic Value
		double valueAnalytic = AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, volatility, maturity, strike1);

		// Controlled value - numericaly calculate the optimal c  c = Cov(X,Y)/Var(X)
		double c = valuePlain.covariance(valueExotic).div(valuePlain.variance()).doubleValue();
		RandomVariable valueControled = valueExotic.sub(valuePlain.sub(valueAnalytic).mult(c));			// Z

		System.out.println("Plain product analytic valuation........: " + valueAnalytic + "\t\u00B1 0.0");
		System.out.println("Plain product Monte-Carlo valuation.....: " + valuePlain.getAverage() + " \t\u00B1 " + valuePlain.getStandardError());
		System.out.println("Exotic product Monte-Carlo valuation....: " + valueExotic.getAverage() + " \t\u00B1 " + valueExotic.getStandardError());
		System.out.println("\tc = " + c);
		System.out.println("Exotic product controlled MC valuation..: " + valueControled.getAverage() + "\t\u00B1 " + valueControled.getStandardError());
	}
}
