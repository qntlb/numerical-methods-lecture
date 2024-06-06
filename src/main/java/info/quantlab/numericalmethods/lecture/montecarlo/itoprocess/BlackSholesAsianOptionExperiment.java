package info.quantlab.numericalmethods.lecture.montecarlo.itoprocess;

import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator1D;
import net.finmath.functions.NormalDistribution;

public class BlackSholesAsianOptionExperiment {

	// Model parameters
	private final double initialStockValue = 100;
	private final double riskFreeRate = 0.05;
	private final double volatility = 0.30;

	// Product parameters
	private final double[] timesForAveraging = new double[] { 5.0 };
	private final double optionMaturity = 5.0;
	private final double optionStrike = 150;
	
	// Monte-Carlo parameters
	private final long seed = 3141;
	private final int numberOfSamples = 100000;
	
	public static void main(String[] args) {

		BlackSholesAsianOptionExperiment blackSholesAsianOptionExperiment = (new BlackSholesAsianOptionExperiment());
		
		double value = blackSholesAsianOptionExperiment.getValueOfAsianOption();
		
		System.out.println("Value of Asian Option....: " + value);
		
	}

	private double getValueOfAsianOption() {

		double sum = 0.0;
		final RandomNumberGenerator1D randomNumberGenerator = new MersenneTwister(seed);

		for(int i=0; i<numberOfSamples; i++) {

			int numberOfTimeSteps = timesForAveraging.length;

			double sumOfStockValues = 0.0;
			double time = 0.0;
			double valueOfStockAtTime = initialStockValue;	// S(T_0)
			for(int timeStepIndex=0; timeStepIndex<numberOfTimeSteps; timeStepIndex++) {
				double uniform = randomNumberGenerator.nextDouble();
				double standardNormal = NormalDistribution.inverseCumulativeDistribution(uniform);
				
				double timeNext = timesForAveraging[timeStepIndex];
				double timeStep = timeNext - time;

				// Time step
				valueOfStockAtTime = valueOfStockAtTime * Math.exp(riskFreeRate * timeStep - 0.5 * volatility * volatility * timeStep + volatility * Math.sqrt(timeStep) * standardNormal);	
				time = timeNext;
				
				sumOfStockValues += valueOfStockAtTime;
			}
			double averageOfStockValues = sumOfStockValues / numberOfTimeSteps;

			double payoffDiscounted = Math.max(averageOfStockValues - optionStrike,  0) * Math.exp(-riskFreeRate * optionMaturity);

			sum += payoffDiscounted;
		}
		
		double value = sum / numberOfSamples;
		
		return value;
	}
}
