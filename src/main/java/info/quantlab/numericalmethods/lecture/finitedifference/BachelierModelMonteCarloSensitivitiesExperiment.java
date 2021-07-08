package info.quantlab.numericalmethods.lecture.finitedifference;

import java.util.function.DoubleUnaryOperator;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BachelierModel;
import net.finmath.montecarlo.model.ProcessModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.montecarlo.process.MonteCarloProcess;
import net.finmath.plots.Plot;
import net.finmath.plots.Plot2D;
import net.finmath.stochastic.RandomVariable;
import net.finmath.stochastic.Scalar;
import net.finmath.time.TimeDiscretizationFromArray;

public class BachelierModelMonteCarloSensitivitiesExperiment {

	private final static double riskFreeRate = 0.0;
	private final static double volatility = 1.0;

	private final static double initialTime = 0.0;
	private final static double maturity = 1.0;
	
	private final static int numberOfPaths = 10000;
	private final static int seed = 3141;

	private final static double shift = 0.5;

	public static void main(String[] args) {
		
		plotLinearFunction();
		plotIndicatorFunction();

	}

	private static void plotLinearFunction() {

		double scalingFactor = 7.0;
		DoubleUnaryOperator value = initialValue -> getRealizationAtMaturity(initialValue).mult(scalingFactor).expectation().doubleValue();
		
		DoubleUnaryOperator delta = initialValue -> (value.applyAsDouble(initialValue + shift) - value.applyAsDouble(initialValue - shift)) / (2*shift);
		
		(new Plot2D(-3.0, 5.0, value)).setTitle("Valuation E( a X ) for X(0) = X_0").setXAxisLabel("initial value X_0").setYAxisLabel("value").show();
		(new Plot2D(-3.0, 5.0, delta)).setTitle("FD appox. of partial derivative d/X_0 of E( a X ) for X(0) = X_0").setXAxisLabel("initial value X_0").setYAxisLabel("value").show();
				
	}

	private static void plotIndicatorFunction() {

		double strike = 1.0;
		DoubleUnaryOperator value = initialValue -> getRealizationAtMaturity(initialValue).sub(strike).choose(new Scalar(1.0), new Scalar(0.0)).expectation().doubleValue();
		
		DoubleUnaryOperator delta = initialValue -> (value.applyAsDouble(initialValue + shift) - value.applyAsDouble(initialValue - shift)) / (2*shift);
		
		(new Plot2D(-3.0, 5.0, value)).setTitle("Valuation E( 1(X > K) ) for X(0) = X_0").setXAxisLabel("initial value X_0").setYAxisLabel("value").show();
		(new Plot2D(-3.0, 5.0, delta)).setTitle("FD appox. of partial derivative d/X_0 of E( 1(X > K) ) for X(0) = X_0").setXAxisLabel("initial value X_0").setYAxisLabel("value").show();
				
	}

	private static RandomVariable getRealizationAtMaturity(double initialValue) {
		try {
			ProcessModel model = new BachelierModel(initialValue, riskFreeRate, volatility);
			BrownianMotion bm = new BrownianMotionFromMersenneRandomNumbers(new TimeDiscretizationFromArray(initialTime, 1, maturity-initialTime), 1, numberOfPaths, seed);
			MonteCarloProcess process = new EulerSchemeFromProcessModel(model, bm);
			
			return process.getProcessValue(1, 0);
		} catch (CalculationException e) {
			throw new RuntimeException(e);
		}
	}
}
