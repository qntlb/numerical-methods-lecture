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

		final double initialValue	= 1.0;
		final double riskFreeRate	= 0.05;
		final double volatility	= 0.20;

		final double maturity	= 5.0;		// T
		final double strike1	= 1.0;		// K1
		final double strike2	= 1.6;		// K2

		final double initialTime		= 0.0;
		final int numberOfTimeSteps	= 1;
		final double deltaT			= (maturity-initialTime)/numberOfTimeSteps;

		final int numberOfPaths = 10000000; // 10 mio
		final int seed = 3141;

		/*
		 * Model
		 */

		// Model: Black Scholes
		final ProcessModel blackScholesModel = new BlackScholesModel(initialValue, riskFreeRate, volatility);

		/*
		 * Monte-Carlo / Numerical Scheme
		 */

		// Brownian Motion
		final TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(initialTime, numberOfTimeSteps, deltaT);
		final BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretization, 1, numberOfPaths, seed);

		// Numerical Scheme
		final MonteCarloProcess process = new EulerSchemeFromProcessModel(blackScholesModel, brownianMotion);

		// Monte-Carlo Valuation Model
		final AssetModelMonteCarloSimulationModel blackScholesMonteCarloModel = new MonteCarloAssetModel(process);

		/*
		 * Product valuation using blackScholesMonteCarloModel
		 */

		// Valuation of plain and exotic payoff
		final RandomVariable underlying = blackScholesMonteCarloModel.getAssetValue(maturity, 0);			// S(T)
		final RandomVariable numeraireAtPayment = blackScholesMonteCarloModel.getNumeraire(maturity);		// N(T)
		final RandomVariable numeraireAtEval	= blackScholesMonteCarloModel.getNumeraire(initialTime);	// N(t)

		// Plain option payoff V(T) = max(S(T)-K1, 0)
		final RandomVariable payoffPlain = underlying.sub(strike1).floor(0.0);							// V(T) for plain option

		// Exotic option payoff V(T) = S(T)-K2 > 0 ? max(S(T)-K1, 0) : (max(S(T)-K1, 0)^2 / (K2-K1)
		final RandomVariable payoffExotic = underlying.sub(strike2).choose(payoffPlain, payoffPlain.squared().div(strike2-strike1));		// V(T) for exotic option

		// Value of the Exotic Option (without control)
		final RandomVariable valueExotic = payoffExotic.div(numeraireAtPayment).mult(numeraireAtEval);// X

		// Value of the Plain Option
		final RandomVariable valuePlain = payoffPlain.div(numeraireAtPayment).mult(numeraireAtEval);	// Y

		// Analytic Value
		final double valueAnalytic = AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, volatility, maturity, strike1);

		// Z(1) = X - 1 ( Y - E(Y) )
		final RandomVariable valueControledWithC1 = valueExotic.sub(valuePlain.sub(valueAnalytic).mult(1));		// Z(1)

		// Controlled value - numerically calculate the optimal c  c = Cov(X,Y)/Var(Y)
		final double c = valuePlain.covariance(valueExotic).div(valuePlain.variance()).doubleValue();

		// Z(c) = X - c ( Y - E(Y) )
		final RandomVariable valueControlled = valueExotic.sub(valuePlain.sub(valueAnalytic).mult(c));			// Z(c)


		System.out.println("Plain product analytic valuation........: " + valueAnalytic + "\t\u00B1 0.0");
		System.out.println("Plain product Monte-Carlo valuation.....: " + valuePlain.getAverage() + " \t\u00B1 " + valuePlain.getStandardError());
		System.out.println();
		System.out.println("Exotic product Monte-Carlo valuation....: " + valueExotic.getAverage() + " \t\u00B1 " + valueExotic.getStandardError());
		System.out.println();
		System.out.println("\tc = " + 1);
		System.out.println("Exotic product controlled MC valuation..: " + valueControledWithC1.getAverage() + "\t\u00B1 " + valueControledWithC1.getStandardError());
		System.out.println();
		System.out.println("\tc = " + c);
		System.out.println("Exotic product controlled MC valuation..: " + valueControlled.getAverage() + "\t\u00B1 " + valueControlled.getStandardError());
	}
}
