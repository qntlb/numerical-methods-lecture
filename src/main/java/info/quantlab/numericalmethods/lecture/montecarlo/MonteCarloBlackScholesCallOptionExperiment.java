package info.quantlab.numericalmethods.lecture.montecarlo;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

import net.finmath.functions.AnalyticFormulas;
import net.finmath.functions.NormalDistribution;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromRandomNumberGenerator;
import net.finmath.randomnumbers.MersenneTwister;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class MonteCarloBlackScholesCallOptionExperiment {

	private double initialValue = 100.0;
	private double riskFreeRate = 0.05;
	private double volatility = 0.20;

	private double optionMaturity = 1.0;
	private double optionStrike = 105;

	private long seed = 3216;
	private long numberOfSamples = 20000000; // 2*10^7

	public static void main(String[] args) {

		long timeStart, timeEnd;

		MonteCarloBlackScholesCallOptionExperiment experiment = new MonteCarloBlackScholesCallOptionExperiment();

		timeStart = System.currentTimeMillis();
		double valueAnalytic = experiment.getAnalyticValue();
		timeEnd = System.currentTimeMillis();

		System.out.println("Analytic value.......................: " + valueAnalytic + " \t(" + (timeEnd-timeStart)/1000.0 + " sec.)");


		timeStart = System.currentTimeMillis();
		double valueMonteCarloWithLoop = experiment.getMonteCarloValueUsingLoop();
		timeEnd = System.currentTimeMillis();

		System.out.println("Monte-Carlo (using plain for-loop)...: " + valueMonteCarloWithLoop + " \t(" + (timeEnd-timeStart)/1000.0 + " sec.)");


		timeStart = System.currentTimeMillis();
		double valueMonteCarloWithStreams = experiment.getMonteCarloValueUsingStreams();
		timeEnd = System.currentTimeMillis();

		System.out.println("Monte-Carlo (using Java Stream api)..: " + valueMonteCarloWithStreams + " \t(" + (timeEnd-timeStart)/1000.0 + " sec.)");


		timeStart = System.currentTimeMillis();
		double valueMonteCarloWithRandomVariables = experiment.getMonteCarloValueRandomVariables();
		timeEnd = System.currentTimeMillis();

		System.out.println("Monte-Carlo (using RandomVariable)...: " + valueMonteCarloWithRandomVariables + " \t(" + (timeEnd-timeStart)/1000.0 + " sec.)");

	}

	private double getMonteCarloValueRandomVariables() {

		// Uniform
		MersenneTwister mersenne = new MersenneTwister(seed);

		TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(0.0, 1, optionMaturity);

		BrownianMotion brownianMotion = new BrownianMotionFromRandomNumberGenerator(
				timeDiscretization, 1, (int) numberOfSamples, mersenne);

		RandomVariable deltaW = brownianMotion.getBrownianIncrement(0.0, 0);

		// Model
		double drift = riskFreeRate * optionMaturity - 0.5 * volatility * volatility * optionMaturity;
		RandomVariable diffusion = deltaW.mult(volatility);
		RandomVariable underlying = diffusion.add(drift).exp().mult(initialValue);

		// Product
		RandomVariable payoffDiscounted = underlying.sub(optionStrike).floor(0.0).mult(Math.exp(-riskFreeRate * optionMaturity));

		double value = payoffDiscounted.average().doubleValue();

		return value;
	}

	private double getMonteCarloValueUsingStreams() {

		/*
		 * Model
		 */
		DoubleUnaryOperator model = z -> initialValue * Math.exp(riskFreeRate * optionMaturity - 0.5 * volatility * volatility * optionMaturity
				+ volatility * Math.sqrt(optionMaturity) * z);

		/*
		 * Product
		 */
		DoubleUnaryOperator payoffDiscounted = s -> Math.max(s - optionStrike, 0)
				* Math.exp(-riskFreeRate * optionMaturity);

		/*
		 * Numerical Method
		 */
		MersenneTwister mersenne = new MersenneTwister(seed);
		DoubleStream uniform = DoubleStream.generate(mersenne).limit(numberOfSamples);
		DoubleStream normalStream = uniform.map(NormalDistribution::inverseCumulativeDistribution);

		DoubleStream underlying = normalStream.map(model);

		DoubleStream payoffStream = underlying.map(payoffDiscounted);

		double value = payoffStream.sum() / numberOfSamples;

		return value;

	}

	private double getMonteCarloValueUsingLoop() {

		// Uniform randoms
		MersenneTwister mersenne = new MersenneTwister(seed);

		double sum = 0.0;
		for(int sampleIndex=0; sampleIndex<numberOfSamples; sampleIndex++) {
			double uniform = mersenne.nextDouble();
			double normal = NormalDistribution.inverseCumulativeDistribution(uniform);
			double underlying = initialValue * Math.exp(
					riskFreeRate * optionMaturity - 0.5 * volatility * volatility * optionMaturity
					+ volatility * Math.sqrt(optionMaturity) * normal);
			double payoff = Math.max(underlying-optionStrike, 0.0);

			sum += payoff * Math.exp(-riskFreeRate * optionMaturity);
		}

		double value = sum / numberOfSamples;

		return value;
	}

	private double getAnalyticValue() {
		return AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, volatility, optionMaturity, optionStrike);
	}

}
