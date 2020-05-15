		package info.quantlab.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

import org.junit.Assert;

import info.quantlab.numericalmethods.lecture.montecarlo.Integrator;
import info.quantlab.numericalmethods.lecture.montecarlo.MonteCarloIntegrator;
import net.finmath.randomnumbers.MersenneTwister;
import net.finmath.randomnumbers.RandomNumberGenerator1D;

public class MonteCarloIntegralOfUnitCircleQuasiRandomNumbersParallel {

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		long numberOfSimulations = 100000000L;
		long numberOfTask = 10;
		long numberOfSimulationPerTask = numberOfSimulations / numberOfTask;


		long calulationMillisStart = System.currentTimeMillis();

		System.out.print("Distributing tasks");

		List<Future<Double>> results = new ArrayList<Future<Double>>();
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		
		double lowerBound = 1.0;
		double upperBound = 5.0;
		DoubleUnaryOperator integrand = x -> Math.cos(x);
		RandomNumberGenerator1D randomNumberGenerator = new MersenneTwister(3141);
		final double range = upperBound-lowerBound;
		for(long taskIndex = 0; taskIndex<numberOfTask; taskIndex++) {

			Callable<Double> task = new Callable<Double>() {
				@Override
				public Double call() throws Exception {
					DoubleStream randomNumbers = DoubleStream.generate(randomNumberGenerator).limit(numberOfSimulationPerTask);

					double sum = randomNumbers.map(x -> integrand.applyAsDouble(lowerBound+range*x)).sum();

					return sum/numberOfSimulationPerTask * range;
				}
			};
			
			Future<Double> integral = executor.submit(task);

			results.add(integral);
		}
		System.out.println("done.");

		/*
		 * All task started. Now wait for the results.
		 */
		
		System.out.print("Collecting results");
		
		double piApprox = 0.0;
		for(int taskIndex = 0; taskIndex<numberOfTask; taskIndex++) {
			System.out.print(".");

			Future<Double> piArroxInTask = results.get(taskIndex);
			piApprox += piArroxInTask.get();
		}
		piApprox = piApprox /numberOfTask;
		System.out.println("done.");

		
		long calulationMillisEnd = System.currentTimeMillis();

		double calculationTimeSec = (calulationMillisEnd-calulationMillisStart) / 1000.0;

		double pi = Math.PI;

		double error = piApprox - pi;

		System.out.println("Approximation of pi via Quasi-Monte-Carlo.....: " + piApprox);
		System.out.println("Approximation error -.........................: " + error);
		System.out.println("Theoretical Monte-Carlo error O-of............; " + 1.0/Math.sqrt(numberOfSimulations));
		System.out.println("Theoretical Halton-Seq. error O-of............; " + (Math.pow(Math.log(numberOfSimulations),2)/numberOfSimulations));
		System.out.println("Calculation time..............................; " + calculationTimeSec + " sec.");
	}


}
