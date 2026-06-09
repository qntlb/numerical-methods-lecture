package info.quantlab.numericalmethods.lecture.parallelization.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import net.finmath.randomnumbers.HaltonSequence;

/**
 * Monte-Carlo Integration of \( x^2 + y^2 \lt 1 \) to approximate pi.
 *
 * As the random number generator generates \( x,y \in (0,1) \), we integrate
 * \( x^2 + y^2 \lt 1 \) with \( x,y \in (0,1) \) (quarter of the area of the unit circle)
 * and multiply by 4.
 *
 * This experiment uses different implementation and compares the efficiency.
 *
 * @author Christian Fries
 */
public class MonteCarloIntegrationParallelExperiment {

	private static double piAnalytic = Math.PI;

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		final int numberOfSamples = 200000000; // 2 * 10^8

		System.out.println("Monte-Carlo approximation of Pi:                                  error           time");
		System.out.println("_".repeat(100));

		testHaltonWithStreamSeq(numberOfSamples);
		testHaltonWithStreamPar(numberOfSamples);
		testMersenneWithStreamSeq(numberOfSamples);
		testMersenneWithStreamPar(numberOfSamples);
		testHaltonWithExecutor(numberOfSamples);
		testMersenneWithExecutor(numberOfSamples);
	}

	private static void testHaltonWithStreamSeq(int numberOfSamples) {

		final long timeStart = System.currentTimeMillis();
		final double piHalton = 4.0 * IntStream.range(0, numberOfSamples).mapToDouble(
				i -> {
					final double x = 2.0 * (HaltonSequence.getHaltonNumberForGivenBase(i, 2)-0.5);
					final double y = 2.0 * (HaltonSequence.getHaltonNumberForGivenBase(i, 3)-0.5);
					if(x*x+y*y < 1) {
						return 1.0;
					} else {
						return 0.0;
					}
				}).sum() / numberOfSamples;
		final long timeEnd = System.currentTimeMillis();

		final double timeInSeconds = (timeEnd-timeStart) / 1000.0;

		System.out.println("Halton, sequential using stream..............................: " +
				String.format("%10.2E", piHalton-piAnalytic) + "\t" + timeInSeconds + " sec.");
	}

	private static void testHaltonWithStreamPar(int numberOfSamples) {

		final long timeStart = System.currentTimeMillis();
		final double piHalton = 4.0 * IntStream.range(0, numberOfSamples).parallel().mapToDouble(
				i -> {
					final double x = 2.0 * (HaltonSequence.getHaltonNumberForGivenBase(i, 2)-0.5);
					final double y = 2.0 * (HaltonSequence.getHaltonNumberForGivenBase(i, 3)-0.5);
					if(x*x+y*y < 1) {
						return 1.0;
					} else {
						return 0.0;
					}
				}).sum() / numberOfSamples;
		final long timeEnd = System.currentTimeMillis();

		final double timeInSeconds = (timeEnd-timeStart) / 1000.0;

		System.out.println("Halton, parallel using stream................................: " +
				String.format("%10.2E", piHalton-piAnalytic) + "\t" + timeInSeconds + " sec.");
	}

	private static  void testMersenneWithStreamSeq(int numberOfSamples) {

		final org.apache.commons.math3.random.MersenneTwister mersenne = new org.apache.commons.math3.random.MersenneTwister(3141);

		final long timeStart = System.currentTimeMillis();
		final double piMersenne = 4.0 * IntStream.range(0, numberOfSamples).mapToDouble(
				i -> {
					final double x = 2.0 * (mersenne.nextDouble()-0.5);
					final double y = 2.0 * (mersenne.nextDouble()-0.5);
					if(x*x+y*y < 1) {
						return 1.0;
					} else {
						return 0.0;
					}
				}).sum() / numberOfSamples;
		final long timeEnd = System.currentTimeMillis();

		final double timeInSeconds = (timeEnd-timeStart) / 1000.0;

		System.out.println("Mersenne, sequential using stream............................: " +
				String.format("%10.2E", piMersenne-piAnalytic) + "\t" + timeInSeconds + " sec.");
	}

	private static  void testMersenneWithStreamPar(int numberOfSamples) {

		final org.apache.commons.math3.random.MersenneTwister mersenne = new org.apache.commons.math3.random.MersenneTwister(3141);

		final long timeStart = System.currentTimeMillis();
		final double piMersenne = 4.0 * IntStream.range(0, numberOfSamples).parallel().mapToDouble(
				i -> {
					synchronized (mersenne) {
						final double x = 2.0 * (mersenne.nextDouble()-0.5);
						final double y = 2.0 * (mersenne.nextDouble()-0.5);
						if(x*x+y*y < 1) {
							return 1.0;
						} else {
							return 0.0;
						}
					}
				}).sum() / numberOfSamples;
		final long timeEnd = System.currentTimeMillis();

		final double timeInSeconds = (timeEnd-timeStart) / 1000.0;

		System.out.println("Mersenne, parallel using stream, synchonized.................: " +
				String.format("%10.2E", piMersenne-piAnalytic) + "\t" + timeInSeconds + " sec.");
	}

	private static void testMersenneWithExecutor(int numberOfSamples) throws InterruptedException, ExecutionException {

		final long timeStart = System.currentTimeMillis();

		final int numberOfTask = 100;
		final int numberOfSamplesPerTask = numberOfSamples / numberOfTask;

		final int numberOfThreads = Runtime.getRuntime().availableProcessors();
		final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

		try {
			final Random randomSeed = new Random(3216);

			/*
			 * Distribute the tasks.
			 */
			final List<Future<Double>> results = new ArrayList<>();
			for(int taskIndex = 0; taskIndex<numberOfTask; taskIndex++) {
				final long seed = randomSeed.nextLong();

				final Future<Double> value = executor.submit(() -> getApproximationOfPiWithMersenne(seed, numberOfSamplesPerTask));
				results.add(value);
			}

			/*
			 * Collect the results
			 */
			double sum = 0.0;
			for(int taskIndex = 0; taskIndex<numberOfTask; taskIndex++) {
				sum += results.get(taskIndex).get();
			}
			final double piMersenne = sum / numberOfTask;

			final long timeEnd = System.currentTimeMillis();

			final double timeInSeconds = (timeEnd-timeStart) / 1000.0;

			System.out.println("Mersenne, parallel Executor w/ thread local generator........: " +
					String.format("%10.2E", piMersenne-piAnalytic) + "\t" + timeInSeconds + " sec.");
		}
		finally {
			executor.shutdown();
		}
	}

	private static void testHaltonWithExecutor(int numberOfSamples) throws InterruptedException, ExecutionException {

		final long timeStart = System.currentTimeMillis();

		final int numberOfTask = 100;
		final int numberOfSamplesPerTask = numberOfSamples / numberOfTask;

		final int numberOfThreads = Runtime.getRuntime().availableProcessors();
		final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

		try {
			/*
			 * Distribute the tasks.
			 */
			final List<Future<Double>> results = new ArrayList<>();
			for(int taskIndex = 0; taskIndex<numberOfTask; taskIndex++) {
				final int startIndex = taskIndex * numberOfSamplesPerTask;

				final Future<Double> value = executor.submit(() -> getApproximationOfPiWithHalton(startIndex, numberOfSamplesPerTask));
				results.add(value);
			}

			/*
			 * Collect the results
			 */
			double sum = 0.0;
			for(int taskIndex = 0; taskIndex<numberOfTask; taskIndex++) {
				sum += results.get(taskIndex).get();
			}
			final double piMersenne = sum / numberOfTask;

			final long timeEnd = System.currentTimeMillis();

			final double timeInSeconds = (timeEnd-timeStart) / 1000.0;

			System.out.println("Halton, parallel using Executor..............................: " +
					String.format("%10.2E", piMersenne-piAnalytic) + "\t" + timeInSeconds + " sec.");
		}
		finally {
			executor.shutdown();
		}
	}

	private static double getApproximationOfPiWithMersenne(long seed, int numberOfSamples) {
		final org.apache.commons.math3.random.MersenneTwister mersenne = new org.apache.commons.math3.random.MersenneTwister(seed);

		int numberOfSamplesInUnitCircle = 0;

		for(int i = 0; i<numberOfSamples; i++) {
			final double x = 2.0 * (mersenne.nextDouble()-0.5);
			final double y = 2.0 * (mersenne.nextDouble()-0.5);
			if(x*x + y*y < 1) {
				numberOfSamplesInUnitCircle++;
			}
		}

		return 4.0 * numberOfSamplesInUnitCircle / numberOfSamples;
	}

	private static double getApproximationOfPiWithHalton(int startIndex, int numberOfSamples) {

		int numberOfSamplesInUnitCircle = 0;

		for(int i = startIndex; i<startIndex+numberOfSamples; i++) {
			final double x = 2.0 * (HaltonSequence.getHaltonNumberForGivenBase(i, 2)-0.5);
			final double y = 2.0 * (HaltonSequence.getHaltonNumberForGivenBase(i, 3)-0.5);
			if(x*x + y*y < 1) {
				numberOfSamplesInUnitCircle++;
			}
		}

		return 4.0 * numberOfSamplesInUnitCircle / numberOfSamples;
	}
}
