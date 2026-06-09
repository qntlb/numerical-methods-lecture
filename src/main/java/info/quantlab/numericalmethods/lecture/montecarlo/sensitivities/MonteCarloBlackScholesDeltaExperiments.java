package info.quantlab.numericalmethods.lecture.montecarlo.sensitivities;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

import net.finmath.exception.CalculationException;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.montecarlo.process.MonteCarloProcess;
import net.finmath.plots.Plot2D;
import net.finmath.plots.PlotableFunction2D;
import net.finmath.stochastic.RandomOperator;
import net.finmath.stochastic.RandomVariable;
import net.finmath.stochastic.Scalar;
import net.finmath.time.TimeDiscretizationFromArray;

public class MonteCarloBlackScholesDeltaExperiments {


	public static void main(String[] args) throws CalculationException {

		final double initialValue = 100.0;
		final double riskFreeRate = 0.05;
		final double volatility = 0.30;

		final double maturity = 5.0;
		final double strike = 125.0;

		final double timeHorizon = maturity;
		final double dt = 5.0;

		final int numberOfPaths = 10000;
		final int seed = 3216;

		final MonteCarloAssetModel model = getMonteCarloBlackScholesModel(initialValue, riskFreeRate, volatility, timeHorizon, dt, numberOfPaths, seed);
		final double valueAnalytic = AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, volatility, maturity, strike);

		final RandomOperator payoffFunctionForward = underlying -> underlying.sub(strike); // S(T)-K
		final RandomOperator payoffFunctionOne = underlying -> new Scalar(1.0); // 1.0
		final RandomOperator payoffFunctionZero = underlying -> new Scalar(0.0); // 0.0

		final RandomOperator payoffFunctionOption = underlying -> underlying.sub(strike).floor(0.0); // max(S(T)-K,0)
		final RandomOperator payoffFunctionDigital = underlying -> underlying.sub(strike).choose(new Scalar(1.0), new Scalar(0.0)); // indicator S(T)>K
		final RandomOperator payoffFunctionNaN = underlying -> new Scalar(Double.NaN); // "dirac" (NaN)

		final double valueMonteCarlo = getValueOfEuropeanProduct(model, maturity, payoffFunctionOption);

		System.out.println("\t\t\t\tMonte-Carlo\tAnalytic\tStd. Error");
		printResult("Valuation...:", valueMonteCarlo, valueAnalytic);

		System.out.println("\nBond:");
		final double valueDeltaBondAnalytic = 0.0;
		checkDeltaApproximationMethods(model, maturity, payoffFunctionOne, payoffFunctionZero, valueDeltaBondAnalytic);

		System.out.println("\nForward:");
		final double valueDeltaForwardAnalytic = 1.0;
		checkDeltaApproximationMethods(model, maturity, payoffFunctionForward, payoffFunctionOne, valueDeltaForwardAnalytic);

		System.out.println("\nEuropean Option:");
		final double valueDeltaAnalytic = AnalyticFormulas.blackScholesOptionDelta(initialValue, riskFreeRate, volatility, maturity, strike);
		checkDeltaApproximationMethods(model, maturity, payoffFunctionOption, payoffFunctionDigital, valueDeltaAnalytic);

		System.out.println("\nDigital Option:");
		final double valueDeltaDigitalAnalytic = AnalyticFormulas.blackScholesDigitalOptionDelta(initialValue, riskFreeRate, volatility, maturity, strike);
		checkDeltaApproximationMethods(model, maturity, payoffFunctionDigital, payoffFunctionNaN, valueDeltaDigitalAnalytic);


		final Plot2D plotForward = getPlotDeltaApproximationByShift(-13, -1, model, maturity, payoffFunctionForward, valueDeltaForwardAnalytic);
		plotForward.setTitle("Finite Difference Approximation of Delta of a Forward (f(S(T) = S(T)-K)").show();

		final Plot2D plotEuropean = getPlotDeltaApproximationByShift(-13, -1, model, maturity, payoffFunctionOption, valueDeltaAnalytic);
		plotEuropean.setTitle("Finite Difference Approximation of Delta of a European Option (f(S(T) = max(S(T)-K,0))").show();

		final Plot2D plotDigital = getPlotDeltaApproximationByShift(-13, -1, model, maturity, payoffFunctionDigital, valueDeltaDigitalAnalytic);
		plotDigital.setTitle("Finite Difference Approximation of Delta of a Digital Option (f(S(T) = 1 for S(T) > K, otherwise 0)").show();

		final Plot2D plotLRForward = getPlotDeltaLRApproximationByShift(-13, -1, model, maturity, payoffFunctionForward, valueDeltaForwardAnalytic);
		plotLRForward.setTitle("Finite Difference Approximation of Likelihood Ratio Delta of a Forward (f(S(T) = S(T)-K)").show();

		final Plot2D plotLREuropean = getPlotDeltaLRApproximationByShift(-13, -1, model, maturity, payoffFunctionOption, valueDeltaAnalytic);
		plotLREuropean.setTitle("Finite Difference Approximation of Likelihood Ratio Delta of a European Option (f(S(T) = max(S(T)-K,0))").show();

		final Plot2D plotLRDigital = getPlotDeltaLRApproximationByShift(-13, -1, model, maturity, payoffFunctionDigital, valueDeltaDigitalAnalytic);
		plotLRDigital.setTitle("Finite Difference Approximation of Likelihood Ratio Delta of a Digital Option (f(S(T) = 1 for S(T) > K, otherwise 0)").show();
	}

	private static void checkDeltaApproximationMethods(MonteCarloAssetModel model, double maturity,
			RandomOperator payoffFunction, RandomOperator payoffFunctionDerivative, double valueDeltaAnalytic) {

		final double valueDeltaFiniteDifference1 = getDeltaFiniteDifference(model, maturity, payoffFunction, 1E-1);
		printResult("Finite Diff (h=1e-1):", valueDeltaFiniteDifference1, valueDeltaAnalytic);

		final double valueDeltaFiniteDifference3 = getDeltaFiniteDifference(model, maturity, payoffFunction, 1E-3);
		printResult("Finite Diff (h=1e-3):", valueDeltaFiniteDifference3, valueDeltaAnalytic);

		final double valueDeltaPathwise = getDeltaPathwise(model, maturity, payoffFunctionDerivative);
		printResult("Pathwise Method:", valueDeltaPathwise, valueDeltaAnalytic);

		final double valueDeltaLikehoodRatio = getDeltaLikelihoodRatio(model, maturity, payoffFunction);
		printResult("Likelihood Ratio:", valueDeltaLikehoodRatio, valueDeltaAnalytic);

	}

	private static double getDeltaFiniteDifference(MonteCarloAssetModel model, double maturity, RandomOperator payoffFunction, double shift) {
		try {
			final double initialValue = model.getAssetValue(0.0, 0).doubleValue();

			final MonteCarloAssetModel modelUpShift = model.getCloneWithModifiedData(Map.of("initialValue", initialValue+shift));
			final double valueUpShift = getValueOfEuropeanProduct(modelUpShift, maturity, payoffFunction);

			final MonteCarloAssetModel modelDnShift = model.getCloneWithModifiedData(Map.of("initialValue", initialValue-shift));
			final double valueDnShift = getValueOfEuropeanProduct(modelDnShift, maturity, payoffFunction);

			return (valueUpShift-valueDnShift)/(2*shift);
		} catch (final CalculationException e) {
			return Double.NaN;
		}
	}

	private static double getDeltaPathwise(MonteCarloAssetModel model, double maturity, RandomOperator payoffFunctionDerivative) {
		try {
			final double initialValue = model.getAssetValue(0.0, 0).doubleValue();

			final var underlying = model.getAssetValue(maturity, 0);

			final var payoffDerivative = payoffFunctionDerivative.apply(underlying).mult(underlying).div(initialValue); // f'(S(T)) *

			final var valueDelta = payoffDerivative.div(model.getNumeraire(maturity)).mult(model.getNumeraire(0.0));

			return valueDelta.average().doubleValue();
		} catch (final CalculationException e) {
			return Double.NaN;
		}
	}

	private static double getDeltaLikelihoodRatio(MonteCarloAssetModel model, double maturity, RandomOperator payoffFunction) {
		try {
			final BlackScholesModel blackScholesModel = (BlackScholesModel)model.getModel();

			final var initialValue = model.getAssetValue(0.0, 0).doubleValue();

			final var riskFreeRate = blackScholesModel.getRiskFreeRate();
			final var sigma = blackScholesModel.getVolatility();
			final var T = maturity;
			final var ST = model.getAssetValue(T, 0);

			final var x = ST.div(initialValue).log().sub(riskFreeRate.mult(T)).add(sigma.squared().mult(0.5*T)).div(sigma).div(Math.sqrt(T));

			final var likelihoodRatio = x.div(initialValue).div(sigma).div(Math.sqrt(T));

			final RandomOperator payoffLikelihoodRatioWeighted = underlying -> payoffFunction.apply(underlying).mult(likelihoodRatio);

			return getValueOfEuropeanProduct(model, maturity, payoffLikelihoodRatioWeighted);
		} catch (final CalculationException e) {
			return Double.NaN;
		}
	}


	private static double getDeltaLikelihoodRatioFiniteDifferenceOfDensity(MonteCarloAssetModel model, double maturity, RandomOperator payoffFunction, double shift) {
		try {
			final BlackScholesModel blackScholesModel = (BlackScholesModel)model.getModel();

			final double initialValue = model.getAssetValue(0.0, 0).doubleValue();

			final var riskFreeRate = blackScholesModel.getRiskFreeRate();
			final var sigma = blackScholesModel.getVolatility();
			final var T = maturity;
			final var ST = model.getAssetValue(T, 0);

			final Function<Double, RandomVariable> logOfdensity = h
					-> ST.log()
					.sub(Math.log(initialValue+h))
					.sub(riskFreeRate.mult(maturity))
					.add(sigma.squared().mult(0.5 * maturity))
					.div(sigma).div(Math.sqrt(maturity))
					.squared().mult(-0.5).exp()
					.div(ST).div(sigma).div(Math.sqrt(maturity)).log();

					final var likelihoodRatio = logOfdensity.apply(shift).sub(logOfdensity.apply(0.0)).div(shift);

					final RandomOperator payoffLikelihoodRatioWeighted = underlying -> payoffFunction.apply(underlying).mult(likelihoodRatio);

					return getValueOfEuropeanProduct(model, maturity, payoffLikelihoodRatioWeighted);
		} catch (final CalculationException e) {
			return Double.NaN;
		}
	}

	private static Plot2D getPlotDeltaApproximationByShift(double xmin, double xmax, MonteCarloAssetModel model, double maturity, RandomOperator payoffFunction, double valueAnalytic) throws CalculationException {
		final double initialValue = model.getAssetValue(0.0, 0).doubleValue();

		final DoubleUnaryOperator finiteDifferenceApproximationByShift = scale -> getDeltaFiniteDifference(
				model, maturity, payoffFunction, initialValue*Math.pow(10, scale));

		final Plot2D plot = (new Plot2D(List.of(
				new PlotableFunction2D(xmin, xmax, 400, finiteDifferenceApproximationByShift),
				new PlotableFunction2D(xmin, xmax, 400, x -> valueAnalytic)
				)))
				.setXAxisLabel("scale (shift = S\u2080*10^{scale})")
				.setYAxisLabel("value")
				.setYAxisNumberFormat(new DecimalFormat("0.#####"));

		return plot;
	}

	private static Plot2D getPlotDeltaLRApproximationByShift(double xmin, double xmax, MonteCarloAssetModel model, double maturity, RandomOperator payoffFunction, double valueAnalytic) throws CalculationException {
		final double initialValue = model.getAssetValue(0.0, 0).doubleValue();

		final DoubleUnaryOperator finiteDifferenceApproximationByShift = scale -> getDeltaLikelihoodRatioFiniteDifferenceOfDensity(
				model, maturity, payoffFunction, initialValue*Math.pow(10, scale));

		final Plot2D plot = (new Plot2D(List.of(
				new PlotableFunction2D(xmin, xmax, 400, finiteDifferenceApproximationByShift),
				new PlotableFunction2D(xmin, xmax, 400, x -> valueAnalytic)
				)))
				.setXAxisLabel("scale (shift = S\u2080*10^{scale})")
				.setYAxisLabel("value")
				.setYAxisNumberFormat(new DecimalFormat("0.#####"));

		return plot;
	}

	private static Plot2D getPlotDeltaPathwiseApproximationByShift(double xmin, double xmax, MonteCarloAssetModel model, double maturity, RandomOperator payoffFunctionDerivative, double valueAnalytic) {
		final DoubleUnaryOperator finiteDifferenceApproximationByShift = scale -> getDeltaPathwise(
				model, maturity, payoffFunctionDerivative);

		final Plot2D plot = (new Plot2D(List.of(
				new PlotableFunction2D(xmin, xmax, 400, finiteDifferenceApproximationByShift),
				new PlotableFunction2D(xmin, xmax, 400, x -> valueAnalytic)
				)))
				.setXAxisLabel("scale (shift = S\u2080*10^{scale})")
				.setYAxisLabel("value")
				.setYAxisNumberFormat(new DecimalFormat("0.#####"));

		return plot;
	}

	private static void printResult(String name, double valueMonteCarlo, double valueAnalytic) {
		System.out.format("%24s\t%+10.6f\t%+10.6f\t%+9.5e\n", name, valueMonteCarlo, valueAnalytic, valueMonteCarlo-valueAnalytic);
	}

	private static double getValueOfEuropeanProduct(MonteCarloAssetModel model, double maturity, RandomOperator payoffFunction) throws CalculationException {

		final var underlying = model.getAssetValue(maturity, 0); // S(T)

		final var payoff = payoffFunction.apply(underlying); // f(S(T))

		final var value = payoff.div(model.getNumeraire(maturity).mult(model.getNumeraire(0.0))); // f(S(T)) / N(T) * N(t)

		return value.expectation().doubleValue(); // E(...)
	}

	private static MonteCarloAssetModel getMonteCarloBlackScholesModel(double initialValue, double riskFreeRate, double volatility, double timeHorizon, double dt, int numberOfPaths, int seed) {

		final BlackScholesModel blackScholesModel = new BlackScholesModel(initialValue, riskFreeRate, volatility);
		final BrownianMotion bm = new BrownianMotionFromMersenneRandomNumbers(new TimeDiscretizationFromArray(0.0, (int)Math.round(timeHorizon/dt), dt),
				1, numberOfPaths, seed);
		final MonteCarloProcess process = new EulerSchemeFromProcessModel(blackScholesModel, bm);

		return new MonteCarloAssetModel(process);
	}

}
