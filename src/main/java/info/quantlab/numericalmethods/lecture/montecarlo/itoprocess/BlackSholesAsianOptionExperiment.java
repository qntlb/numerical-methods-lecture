package info.quantlab.numericalmethods.lecture.montecarlo.itoprocess;

import java.util.Arrays;

import info.quantlab.numericalmethods.lecture.randomnumbers.HaltonSequence;
import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator1D;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGeneratorFrom1D;
import info.quantlab.numericalmethods.lecture.randomnumbers.VanDerCorputSequence;
import net.finmath.functions.NormalDistribution;

/**
 * Calculate the value of an Asian Option under the Black-Scholes model.
 * 
 * Uses log-euler discretization (exact scheme) and Monte-Carlo simulation.
 * Using pseudo- and quasi-random number generators.
 * Uses wrong and correct construction of sample-vector / sample path (dimension = number of time steps).
 * 
 * @author Christian Fries
 */
public class BlackSholesAsianOptionExperiment {

	// Model parameters
	private final double initialStockValue = 100;
	private final double riskFreeRate = 0.05;
	private final double volatility = 0.30;

	// Product parameters
	private final double[] timesForAveraging = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0 };
	private final double optionMaturity = 5.0;
	private final double optionStrike = 150;
	
	// Monte-Carlo parameters
	private final long seed = 3141;
	private final int numberOfSamples = 100000;
	private final int[] primes = new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 47, 53, 59 };

	
	public static void main(String[] args) {

		BlackSholesAsianOptionExperiment blackSholesAsianOptionExperiment = (new BlackSholesAsianOptionExperiment());
		blackSholesAsianOptionExperiment.run();
	}

	private void run() {
		
		final RandomNumberGenerator1D randomNumberGenerator = new MersenneTwister(seed);
		double value = getValueOfAsianOption(randomNumberGenerator);		
		System.out.println("Value of Asian Option (Mersenne Twister 1D)..: " + value);

		final RandomNumberGenerator1D randomNumberGeneratorVdC = new VanDerCorputSequence(2);
		double valueVdC = getValueOfAsianOption(randomNumberGeneratorVdC);		
		System.out.println("Value of Asian Option (with v.d.C. seq, 1D)..: " + valueVdC + "\t(wrong)");

		final int dimension = timesForAveraging.length;
		
		final RandomNumberGenerator randomNumberGeneratorPseudo = new RandomNumberGeneratorFrom1D(new MersenneTwister(seed), dimension);
		double valuePseudo = getValueOfAsianOption(randomNumberGeneratorPseudo);		
		System.out.println("Value of Asian Option (Mersenne Twister " + randomNumberGeneratorPseudo.getDimension() + "D)..: " + valuePseudo);

		final RandomNumberGenerator randomNumberGeneratorQuasi = new HaltonSequence(Arrays.copyOfRange(primes, 0, dimension));	
		double valueQuasi = getValueOfAsianOption(randomNumberGeneratorQuasi);		
		System.out.println("Value of Asian Option (Halton " + randomNumberGeneratorQuasi.getDimension() + "D)............: " + valueQuasi);
	}

	private double getValueOfAsianOption(RandomNumberGenerator1D randomNumberGenerator) {

		double sum = 0.0;

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

	private double getValueOfAsianOption(RandomNumberGenerator randomNumberGenerator) {

		double sum = 0.0;

		for(int i=0; i<numberOfSamples; i++) {

			double[] uniforms = randomNumberGenerator.getNext();
			
			int numberOfTimeSteps = timesForAveraging.length;

			double sumOfStockValues = 0.0;
			double time = 0.0;
			double valueOfStockAtTime = initialStockValue;	// S(T_0)
			for(int timeStepIndex=0; timeStepIndex<numberOfTimeSteps; timeStepIndex++) {
				double uniform = uniforms[timeStepIndex];
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
