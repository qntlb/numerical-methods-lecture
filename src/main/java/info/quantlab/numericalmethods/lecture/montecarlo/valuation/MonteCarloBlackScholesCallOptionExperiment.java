package info.quantlab.numericalmethods.lecture.montecarlo.valuation;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

import net.finmath.exception.CalculationException;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.functions.NormalDistribution;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.BrownianMotionFromRandomNumberGenerator;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AssetMonteCarloProduct;
import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;
import net.finmath.montecarlo.model.ProcessModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.montecarlo.process.MonteCarloProcess;
import net.finmath.randomnumbers.MersenneTwister;
import net.finmath.randomnumbers.RandomNumberGenerator;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

/**
 * This experiment illustrates different means of calculating
 * the value of a European option under a Black-Scholes model
 * using Monte-Carlo simulation.
 * 
 * The Euler scheme is the exact solution using one time step
 * (Euler scheme applied to log(S)).
 * 
 * The valuation is illustrated:
 * <ol>
 * 	<li>Direct loop evaluating the valuation function V(T) on the uniform random numbers.</li>
 * 	<li>Stream of uniform random numbers, mapped by the model function to S(T), mapped by the product function to V(T), then averaged.</li>
 * 	<li>Arithmetic operations on <code>RandomVariable</code> using a <code>TimeDiscretization</code> and <code>BrownianMotion</code>.</li>
 * 	<li>Using a <code>Model</code>, an <code>EulerScheme</code> and a <code>Product</code>.</li>
 * </ol>
 * 
 * @author Christian Fries
 */
public class MonteCarloBlackScholesCallOptionExperiment {

	private double	initialValue = 100.0;		// S_0 = S(t_0)
	private double	riskFreeRate = 0.05;		// r
	private double	volatility = 0.20;			// sigma

	private double	optionMaturity = 1.0;		// T
	private double	optionStrike = 105;			// K

	private double	initialTime = 0.0;			// t_0
	private int		numberOfTimeSteps = 1;
	
	private long	seed = 3216;
	private int		numberOfFactors = 1;		// dimension of the Brownian Motion 
	private long	numberOfSamples = 20000000;	// 2*10^7

	public static void main(String[] args) throws CalculationException {

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

		timeStart = System.currentTimeMillis();
		double valueMonteCarloWithLib = experiment.getMonteCarloValueLib();
		timeEnd = System.currentTimeMillis();

		System.out.println("Monte-Carlo (using Lib)..............: " + valueMonteCarloWithLib + " \t(" + (timeEnd-timeStart)/1000.0 + " sec.)");
	}

	/*
	 * The analytic valuation formula
	 */
	private double getAnalyticValue() {
		return AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, volatility, optionMaturity, optionStrike);
	}

	/*
	 * Calculation of the expectation using a classical loop.
	 */
	private double getMonteCarloValueUsingLoop() {

		// Uniform random numbers
		MersenneTwister mersenne = new MersenneTwister(seed);

		double sum = 0.0;
		for(int sampleIndex=0; sampleIndex<numberOfSamples; sampleIndex++) {
			double uniform = mersenne.nextDouble();
			double normal = NormalDistribution.inverseCumulativeDistribution(uniform);
			// S(T)
			double underlying = initialValue * Math.exp(
					riskFreeRate * optionMaturity - 0.5 * volatility * volatility * optionMaturity
					+ volatility * Math.sqrt(optionMaturity) * normal);
			// V(T)
			double payoff = Math.max(underlying-optionStrike, 0.0);

			// sum += V(T) * N(t)/N(T)
			sum += payoff * Math.exp(-riskFreeRate * optionMaturity);
		}
		double value = sum / numberOfSamples;
		return value;
	}

	/*
	 * Calculation of the expectation using a stream and two separate transformations (operators): model and product
	 */
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

	/*
	 * Calculation using RandomVariable and RandomNumberGenerator, TimeDiscretization, BrownianMotion
	 * 
	 * Remark: the library implementation in getMonteCarloValueLib uses a different order which explains a small difference in rounding error. To get the same value use
	 * RandomVariable underlying = diffusion.add(Math.log(initialValue)).add(drift).exp();		// S(T)
	 */
	private double getMonteCarloValueRandomVariables() {

		// Uniform
		RandomNumberGenerator randomNumberGenerator = new MersenneTwister(seed);

		// Time Discretization
		TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(initialTime, numberOfTimeSteps, (optionMaturity-initialTime)/numberOfTimeSteps);

		// Stochastic Driver: Brownian Motion.
		BrownianMotion brownianMotion = new BrownianMotionFromRandomNumberGenerator(timeDiscretization, 1, (int) numberOfSamples, randomNumberGenerator);

		RandomVariable deltaW = brownianMotion.getBrownianIncrement(initialTime, 0);		// W(T)

		// Model
		double drift = riskFreeRate * optionMaturity - 0.5 * volatility * volatility * optionMaturity;
		RandomVariable diffusion = deltaW.mult(volatility);
		RandomVariable underlying = diffusion.add(drift).exp().mult(initialValue);		// S(T)
		
		// Product // V(T) * N(t) / N(T)
		RandomVariable payoffDiscounted = underlying.sub(optionStrike).floor(0.0).mult(Math.exp(-riskFreeRate * (optionMaturity-initialTime)));

		// Expectation
		double value = payoffDiscounted.expectation().doubleValue();

		return value;
	}

	/*
	 * Calculation using ProcessModel and EulerSchemeFromProcessModel and AssetMonteCarloProduct (and TimeDiscretization and BrownianMotion)
	 */
	private double getMonteCarloValueLib() throws CalculationException {

		ProcessModel blackScholesModel = new BlackScholesModel(initialValue, riskFreeRate, volatility);

		TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(initialTime, numberOfTimeSteps, (optionMaturity-initialTime)/numberOfTimeSteps);

		BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretization, numberOfFactors, (int)numberOfSamples, (int)seed);

		MonteCarloProcess process = new EulerSchemeFromProcessModel(blackScholesModel, brownianMotion);

		MonteCarloAssetModel blackScholesMonteCarloModel = new MonteCarloAssetModel(process);

		AssetMonteCarloProduct option = new EuropeanOption(optionMaturity, optionStrike);

		double value = option.getValue(initialTime, blackScholesMonteCarloModel).expectation().doubleValue();

		return value;
	}
}
