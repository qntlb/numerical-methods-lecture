package info.quantlab.numericalmethods.lecture.montecarlo.weighted.experiments;

import java.util.ArrayList;
import java.util.List;

import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator1D;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.functions.NormalDistribution;
import net.finmath.plots.Plots;

/**
 * Small experiment illustrating <i>weighed Monte-Carlo</i> to achieve
 * an <i>importance sampling</i> for the valuation of an European option
 * under the Black-Scholes model.
 */
public class ImportanceSamplingExperiment {

	// Model parameters
	private final double initialStockValue = 100;
	private final double riskFreeRate = 0.05;
	private final double volatility = 0.30;

	// Product parameters
	private final double optionMaturity = 5.0;	
	private final double optionStrike = 150;
	
	// Monte-Carlo parameters
	private final long seed = 1616;
	private final int numberOfSamples = 10000;
	
	public static void main(String[] args) {
		
		(new ImportanceSamplingExperiment()).plot();
		
	}

	/**
	 * Plots the Monte-Carlo error for different shift sizes.
	 */
	private void plot() {

		List<Double> shifts = new ArrayList<>();
		List<Double> errors = new ArrayList<>();
		
		for(int i=0; i<=100; i++) {
			
			double shift = i/100.0 * 3.0;
			double monteCarloErrorForShift = getMCErrorForValueWithImportanceSamplingShift(shift);
			
			shifts.add(shift);
			errors.add(monteCarloErrorForShift);
		}
		
		Plots.createScatter(shifts, errors, 0.0, 3.0, 3)
		.setTitle(String.format("Monte-Carlo Approximation Error (seed=%d)", seed))
		.setXAxisLabel("Shift size (importance sampling)")
		.setYAxisLabel("Error")
		.show();
	}

	private double getMCErrorForValueWithImportanceSamplingShift(double shift) {
		final RandomNumberGenerator1D randomNumberGenerator = new MersenneTwister(seed);

		double valueAnalytic = AnalyticFormulas.blackScholesOptionValue(initialStockValue, riskFreeRate, volatility, optionMaturity, optionStrike);

		double sum = 0.0;
		double sumError = 0.0;
		for(int i=0; i<numberOfSamples; i++) {
			double uniform = randomNumberGenerator.nextDouble();
			double standardNormal = NormalDistribution.inverseCumulativeDistribution(uniform);

			double x = standardNormal;
			double y = x + shift;

			double underlying = initialStockValue * Math.exp(riskFreeRate * optionMaturity - 0.5 * volatility * volatility * optionMaturity + volatility * Math.sqrt(optionMaturity) * y);
			double payoffDiscounted = Math.max(underlying - optionStrike,  0) * Math.exp(-riskFreeRate * optionMaturity);

			double weight = Math.exp(-y*y/2 + (y-shift)*(y-shift)/2);
			
			double value = payoffDiscounted * weight;
			
			sum += value;
			sumError += Math.pow(value-valueAnalytic, 2);
		}

		double valueMonteCarlo = sum / numberOfSamples;
		double error = Math.sqrt(sumError / numberOfSamples);

		System.out.println(String.format("%10.2f \t %10.2f \t %10.2f \t %10.2f", shift, valueMonteCarlo, valueAnalytic, error));
		
		return error;
	}
}
