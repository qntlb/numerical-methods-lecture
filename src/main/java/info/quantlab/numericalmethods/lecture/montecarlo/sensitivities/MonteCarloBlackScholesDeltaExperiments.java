package info.quantlab.numericalmethods.lecture.montecarlo.sensitivities;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

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
import net.finmath.stochastic.Scalar;
import net.finmath.time.TimeDiscretizationFromArray;

public class MonteCarloBlackScholesDeltaExperiments {

	private static double initialValue = 100.0;
	private static int seed = 3216;

	public static void main(String[] args) throws CalculationException {

		double riskFreeRate = 0.05;
		double volatility = 0.30;

		double maturity = 5.0;
		double strike = 125.0;

		double timeHorizon = maturity;
		double dt = 5.0;

		int numberOfPaths = 10000;

		MonteCarloAssetModel model = getModel(riskFreeRate, volatility, timeHorizon, dt, numberOfPaths);
		double valueAnalytic = AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, volatility, maturity, strike);

		RandomOperator payoffFunctionZero = underlying -> new Scalar(0.0);
		RandomOperator payoffFunctionBond = underlying -> new Scalar(1.0);
		RandomOperator payoffFunctionForward = underlying -> underlying;
		RandomOperator payoffFunctionOption = underlying -> underlying.sub(strike).floor(0.0);
		RandomOperator payoffFunctionDigital = underlying -> underlying.sub(strike).choose(new Scalar(1.0), new Scalar(0.0));
		RandomOperator payoffFunctionNaN = underlying -> new Scalar(Double.NaN);

		double valueMonteCarlo = getValueMonteCarlo(model, maturity, payoffFunctionOption);

		printResult("Valuation...:", valueMonteCarlo, valueAnalytic);

		System.out.println("\nBond:");
		double valueDeltaBondAnalytic = 0.0;
		checkDeltaApproximationMethods(model, maturity, payoffFunctionBond, payoffFunctionZero, valueDeltaBondAnalytic);

		System.out.println("\nForward:");
		double valueDeltaForwardAnalytic = 1.0;
		checkDeltaApproximationMethods(model, maturity, payoffFunctionForward, payoffFunctionBond, valueDeltaForwardAnalytic);

		System.out.println("\nEuropean Option:");
		double valueDeltaAnalytic = AnalyticFormulas.blackScholesOptionDelta(initialValue, riskFreeRate, volatility, maturity, strike);
		checkDeltaApproximationMethods(model, maturity, payoffFunctionOption, payoffFunctionDigital, valueDeltaAnalytic);

		System.out.println("\nDigital Option:");
		double valueDeltaDigitalAnalytic = AnalyticFormulas.blackScholesDigitalOptionDelta(initialValue, riskFreeRate, volatility, maturity, strike);
		checkDeltaApproximationMethods(model, maturity, payoffFunctionDigital, payoffFunctionNaN, valueDeltaDigitalAnalytic);


		Plot2D plotEuropean = getPlotDeltaApproximationByShift(2, 12, model, maturity, payoffFunctionOption);
		plotEuropean.setTitle("Finite Difference Approximation of Delta European Option")
		.setXAxisLabel("scale (shift = S(0)*10^{-scale}")
		.setYAxisLabel("value")
		.setYAxisNumberFormat(new DecimalFormat("0.#####"));
		plotEuropean.show();

		Plot2D plotDigital = getPlotDeltaApproximationByShift(2, 12, model, maturity, payoffFunctionDigital);
		plotDigital.setTitle("Finite Difference Approximation of Delta Digital Option")
		.setXAxisLabel("scale (shift = S(0)*10^{-scale}")
		.setYAxisLabel("value")
		.setYAxisNumberFormat(new DecimalFormat("0.#####"));
		plotDigital.show();
	}

	private static void checkDeltaApproximationMethods(MonteCarloAssetModel model, double maturity,
			RandomOperator payoffFunction, RandomOperator payoffFunctionDerivative, double valueDeltaAnalytic) throws CalculationException {

		double valueDeltaFiniteDifference1 = getDeltaFiniteDifference(model, maturity, payoffFunction, 1E-1);
		printResult("Finite Diff (h=1e-1):", valueDeltaFiniteDifference1, valueDeltaAnalytic);

		double valueDeltaFiniteDifference3 = getDeltaFiniteDifference(model, maturity, payoffFunction, 1E-3);
		printResult("Finite Diff (h=1e-3):", valueDeltaFiniteDifference3, valueDeltaAnalytic);

		double valueDeltaPathwise = getDeltaPathwise(model, maturity, payoffFunctionDerivative);
		printResult("Pathwise Method:", valueDeltaPathwise, valueDeltaAnalytic);

		double valueDeltaLikehoodRatio = getDeltaLikelihoodRation(model, maturity, payoffFunction);
		printResult("Likelihood Ratio:", valueDeltaLikehoodRatio, valueDeltaAnalytic);

	}

	private static double getDeltaLikelihoodRation(MonteCarloAssetModel model, double maturity, RandomOperator payoffFunction) throws CalculationException {

		BlackScholesModel blackScholesModel = (BlackScholesModel)model.getModel();

		var riskFreeRate = blackScholesModel.getRiskFreeRate();
		var sigma = blackScholesModel.getVolatility();
		var T = maturity;
		var ST = model.getAssetValue(T, 0);

		var x = ST.div(initialValue).log().sub(riskFreeRate.mult(T)).add(sigma.squared().mult(0.5*T)).div(sigma).div(Math.sqrt(T));

		var likelihoodRatio = x.div(initialValue).div(sigma).div(Math.sqrt(T));

		RandomOperator payoffLikelihoodRatioWeighted = underlying -> payoffFunction.apply(underlying).mult(likelihoodRatio);

		return getValueMonteCarlo(model, maturity, payoffLikelihoodRatioWeighted);
	}

	private static double getDeltaPathwise(MonteCarloAssetModel model, double maturity,
			RandomOperator payoffFunctionDerivative) throws CalculationException {

		var underlying = model.getAssetValue(maturity, 0);

		var payoffDerivative = payoffFunctionDerivative.apply(underlying).mult(underlying).div(initialValue);

		var valueDelta = payoffDerivative.div(model.getNumeraire(maturity)).mult(model.getNumeraire(0.0));

		return valueDelta.average().doubleValue();
	}

	private static Plot2D getPlotDeltaApproximationByShift(double xmin, double xmax, MonteCarloAssetModel model, double maturity, RandomOperator payoffFunction) {
		DoubleUnaryOperator finiteDifferenceApproximationByShift = scale -> getDeltaFiniteDifference(
				model, maturity, payoffFunction, initialValue*Math.pow(10, -scale));

		return new Plot2D(List.of(new PlotableFunction2D(xmin, xmax, 400, finiteDifferenceApproximationByShift)));
	}

	private static double getDeltaFiniteDifference(MonteCarloAssetModel model, double maturity, RandomOperator payoffFunction, double shift) {
		try {
			MonteCarloAssetModel modelUpShift = model.getCloneWithModifiedData(Map.of("initialValue", initialValue+shift));
			double valueUpShift = getValueMonteCarlo(modelUpShift, maturity, payoffFunction);

			MonteCarloAssetModel modelDnShift = model.getCloneWithModifiedData(Map.of("initialValue", initialValue-shift));
			double valueDnShift = getValueMonteCarlo(modelDnShift, maturity, payoffFunction);

			return (valueUpShift-valueDnShift)/(2*shift);
		} catch (CalculationException e) {
			e.printStackTrace();
			return Double.NaN;
		}
	}

	private static void printResult(String name, double valueMonteCarlo, double valueAnalytic) {
		System.out.format("%24s\t%+10.6f\t%+10.6f\t%+9.5e\n", name, valueMonteCarlo, valueAnalytic, valueMonteCarlo-valueAnalytic);		
	}

	private static double getValueMonteCarlo(MonteCarloAssetModel model, double maturity, RandomOperator payoffFunction) throws CalculationException {

		var underlying = model.getAssetValue(maturity, 0);
		var payoff = payoffFunction.apply(underlying);
		var value = payoff.div(model.getNumeraire(maturity).mult(model.getNumeraire(0.0)));

		return value.average().doubleValue();
	}

	private static MonteCarloAssetModel getModel(double riskFreeRate, double volatility, double timeHorizon, double dt, int numberOfPaths) {

		BlackScholesModel blackScholesModel = new BlackScholesModel(initialValue, riskFreeRate, volatility);
		BrownianMotion bm = new BrownianMotionFromMersenneRandomNumbers(new TimeDiscretizationFromArray(0.0, (int)Math.round(timeHorizon/dt), dt),
				1, numberOfPaths, seed);
		MonteCarloProcess process = new EulerSchemeFromProcessModel(blackScholesModel, bm);

		return new MonteCarloAssetModel(process);
	}

}
