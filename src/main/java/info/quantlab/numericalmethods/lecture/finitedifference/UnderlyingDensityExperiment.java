package info.quantlab.numericalmethods.lecture.finitedifference;

import java.util.function.DoubleUnaryOperator;

import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.MonteCarloProduct;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BachelierModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.models.HestonModel;
import net.finmath.montecarlo.assetderivativevaluation.models.HestonModel.Scheme;
import net.finmath.montecarlo.assetderivativevaluation.products.AssetMonteCarloProduct;
import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;
import net.finmath.montecarlo.model.ProcessModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.montecarlo.process.MonteCarloProcess;
import net.finmath.plots.Plot2D;
import net.finmath.time.TimeDiscretizationFromArray;

/**
 * Plotting the (numerically approximated) (risk neutral) probability density
 * of different models.
 *
 * Literature: Breeden &amp; Litzenberger (1978)
 *
 * Note that the Heston model requires a 2-factor Brownian motion. The other models only use the first factor of the Brownian motion.
 *
 * @author Christian Fries
 */
public class UnderlyingDensityExperiment {

	private final static double initialValue = 1.0;
	private final static double riskFreeRate = 0.05;
	private final static double volatility = 0.30;

	private final static double initialTime = 0.0;
	private final static int numberOfTimeSteps = 100;
	private final static double maturity = 5.0;

	private final static int numberOfPaths = 100000;
	private final static int seed = 3141;

	private final static double shift = 0.2;

	public static void main(String[] args) {

		plotDensityForModel(new BlackScholesModel(initialValue, riskFreeRate, volatility));

		plotDensityForModel(new BachelierModel(initialValue, riskFreeRate, volatility));

		final double theta = volatility;
		final double kappa = 0.2;
		final double xi = 0.2;
		final double rho = 0.8;

		plotDensityForModel(new HestonModel(initialValue, riskFreeRate, volatility, theta, kappa, xi, rho, Scheme.FULL_TRUNCATION));

		/*
		 * Plotting the density for an implied voatility curve
		 */
		double amplitude = 1.0;
		double width = 1.0;
		(new Plot2D(-1.0, 5.0, impliedBlackScholesVolatiltiyModel(amplitude, width))).setTitle("Implied volatility (\u03c3(K) = " + volatility + " * ( 1 + " + amplitude + " * cos((K-F)/(" + width + "/\\u03c0)").setXAxisLabel("K").setYAxisLabel("density").setYRange(0.0, (2+amplitude)*volatility).show();
		(new Plot2D(-1.0, 5.0, densityFromImpliedVol(impliedBlackScholesVolatiltiyModel(amplitude, width)))).setTitle("Density from implied volatility (\u03c3(K) = " + volatility + " * ( 1 + " + amplitude + " * cos(" + width + "/\u03c0 * (K-F))").setXAxisLabel("S").setYAxisLabel("density").show();
	}

	private static void plotDensityForModel(ProcessModel model) {
		(new Plot2D(-1.0, 5.0, density(model))).setTitle("Density of " + model.getClass().getSimpleName()).setXAxisLabel("S").setYAxisLabel("density").show();
	}

	private static DoubleUnaryOperator density(ProcessModel processModel) {
		/*
		 * Note: it would be more efficient to pre-calculate the value vector, then calculate the density vector.
		 * What we do here requires three times valuations - but it is quite fast as Monte-Carlo simulation is cached.
		 */
		BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(new TimeDiscretizationFromArray(initialTime, numberOfTimeSteps, (maturity-initialTime)/numberOfTimeSteps), 2, numberOfPaths, seed);
		MonteCarloProcess process = new EulerSchemeFromProcessModel(processModel, brownianMotion);
		MonteCarloAssetModel model = new MonteCarloAssetModel(process);

		// K -> V(K;T0) / N(T0)
		DoubleUnaryOperator value = strike -> {
			try {
				AssetMonteCarloProduct product = new EuropeanOption(maturity, strike);
				return product.getValue(initialTime, model).div(model.getNumeraire(initialTime)).getAverage();
			}
			catch(Exception e) {
				return Double.NaN;
			}
		};

		// Density (via Central Finite Difference)
		DoubleUnaryOperator density = strike -> ((value.applyAsDouble(strike+shift) - 2 * value.applyAsDouble(strike) + value.applyAsDouble(strike-shift)) / (shift * shift));

		return density;
	}

	private static DoubleUnaryOperator impliedBlackScholesVolatiltiyModel(double amplitude, double width) {
		double forward = initialValue*Math.exp(riskFreeRate * maturity);
		DoubleUnaryOperator volatilityCurve = strike -> volatility * (1 + amplitude * Math.cos((strike-forward)/width/Math.PI));

		return volatilityCurve;
	}

	private static DoubleUnaryOperator densityFromImpliedVol(DoubleUnaryOperator impliedBlackScholesVolatiltiyModel) {

		DoubleUnaryOperator value = strike -> AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, impliedBlackScholesVolatiltiyModel.applyAsDouble(strike), maturity, strike);

		DoubleUnaryOperator density = strike -> ((value.applyAsDouble(strike+shift) - 2 * value.applyAsDouble(strike) + value.applyAsDouble(strike-shift)) / (shift * shift));

		return density;
	}

}
