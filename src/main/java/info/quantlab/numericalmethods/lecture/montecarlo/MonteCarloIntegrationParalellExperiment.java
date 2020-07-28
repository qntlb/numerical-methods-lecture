package info.quantlab.numericalmethods.lecture.montecarlo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import net.finmath.randomnumbers.HaltonSequence;

public class MonteCarloIntegrationParalellExperiment {

	private static double piAnalytic = Math.PI;

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		int numberOfSamples = 200000000; // 2 * 10^8

		testHaltonWithStreamSeq(numberOfSamples);
		testHaltonWithStreamPar(numberOfSamples);
		testHaltonWithExecutor(numberOfSamples);
		testMersenneWithStreamSeq(numberOfSamples);
		testMersenneWithStreamPar(numberOfSamples);
		testMersenneWithExecutor(numberOfSamples);
	}

	private static void testHaltonWithStreamSeq(int numberOfSamples) {

		long timeStart = System.currentTimeMillis();
		double piHalton = 4.0 * IntStream.range(0, numberOfSamples).mapToDouble(
				i -> {
					double x = 2.0 * (HaltonSequence.getHaltonNumberForGivenBase(i, 2)-0.5);
					double y = 2.0 * (HaltonSequence.getHaltonNumberForGivenBase(i, 3)-0.5);
					if(x*x+y*y < 1) return 1.0;
					else 			return 0.0;
				}).sum() / numberOfSamples;
		long timeEnd = System.currentTimeMillis();

		double timeInSeconds = (timeEnd-timeStart) / 1000.0;

		System.out.println("Halton, sequential using stream....: " + (piHalton-piAnalytic) + "\t" + timeInSeconds + " sec.");
	}

	private static void testHaltonWithStreamPar(int numberOfSamples) {

		long timeStart = System.currentTimeMillis();
		double piHalton = 4.0 * IntStream.range(0, numberOfSamples).parallel().mapToDouble(
				i -> {
					double x = 2.0 * (HaltonSequence.getHaltonNumberForGivenBase(i, 2)-0.5);
					double y = 2.0 * (HaltonSequence.getHaltonNumberForGivenBase(i, 3)-0.5);
					if(x*x+y*y < 1) return 1.0;
					else 			return 0.0;
				}).sum() / numberOfSamples;
		long timeEnd = System.currentTimeMillis();

		double timeInSeconds = (timeEnd-timeStart) / 1000.0;

		System.out.println("Halton, parallel using stream......: " + (piHalton-piAnalytic) + "\t" + timeInSeconds + " sec.");
	}

	private static  void testMersenneWithStreamSeq(int numberOfSamples) {

		org.apache.commons.math3.random.MersenneTwister mersenne = new org.apache.commons.math3.random.MersenneTwister(3141);

		long timeStart = System.currentTimeMillis();
		double piMersenne = 4.0 * IntStream.range(0, numberOfSamples).mapToDouble(
				i -> {
					double x = 2.0 * (mersenne.nextDouble()-0.5);
					double y = 2.0 * (mersenne.nextDouble()-0.5);
					if(x*x+y*y < 1) return 1.0;
					else 			return 0.0;
				}).sum() / numberOfSamples;
		long timeEnd = System.currentTimeMillis();

		double timeInSeconds = (timeEnd-timeStart) / 1000.0;

		System.out.println("Mersenne, sequential using stream..: " + (piMersenne-piAnalytic) + "\t" + timeInSeconds + " sec.");
	}

	private static  void testMersenneWithStreamPar(int numberOfSamples) {

		org.apache.commons.math3.random.MersenneTwister mersenne = new org.apache.commons.math3.random.MersenneTwister(3141);

		long timeStart = System.currentTimeMillis();
		double piMersenne = 4.0 * IntStream.range(0, numberOfSamples).parallel().mapToDouble(
				i -> {
					synchronized (mersenne) {
						double x = 2.0 * (mersenne.nextDouble()-0.5);
						double y = 2.0 * (mersenne.nextDouble()-0.5);
						if(x*x+y*y < 1) return 1.0;
						else 			return 0.0;
					}
				}).sum() / numberOfSamples;
		long timeEnd = System.currentTimeMillis();

		double timeInSeconds = (timeEnd-timeStart) / 1000.0;

		System.out.println("Mersenne, parallel stream + synchronize....: " + (piMersenne-piAnalytic) + "\t" + timeInSeconds + " sec.");
	}

	private static void testMersenneWithExecutor(int numberOfSamples) throws InterruptedException, ExecutionException {

		long timeStart = System.currentTimeMillis();

		int numberOfTask = 100;
		int numberOfSamplesPerTask = numberOfSamples / numberOfTask;

		int numberOfThreads = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

		try {
			Random randomSeed = new Random(3216);

			/*
			 * Distribute the tasks.
			 */
			List<Future<Double>> results = new ArrayList<>();
			for(int taskIndex = 0; taskIndex<numberOfTask; taskIndex++) {
				long seed = randomSeed.nextLong();

				Future<Double> value = executor.submit(() -> getApproximationOfPiWithMersenne(seed, numberOfSamplesPerTask));
				results.add(value);
			}

			/*
			 * Collect the results
			 */
			double sum = 0.0;
			for(int taskIndex = 0; taskIndex<numberOfTask; taskIndex++) {
				sum += results.get(taskIndex).get();
			}
			double piMersenne = sum / numberOfTask;

			long timeEnd = System.currentTimeMillis();

			double timeInSeconds = (timeEnd-timeStart) / 1000.0;

			System.out.println("Mersenne, parallel Executor w/ thread local generator..: " + (piMersenne-piAnalytic) + "\t" + timeInSeconds + " sec.");
		}
		finally {
			executor.shutdown();
		}
	}

	private static void testHaltonWithExecutor(int numberOfSamples) throws InterruptedException, ExecutionException {

		long timeStart = System.currentTimeMillis();

		int numberOfTask = 100;
		int numberOfSamplesPerTask = numberOfSamples / numberOfTask;

		int numberOfThreads = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

		try {
			/*
			 * Distribute the tasks.
			 */
			List<Future<Double>> results = new ArrayList<>();
			for(int taskIndex = 0; taskIndex<numberOfTask; taskIndex++) {
				int startIndex = taskIndex * numberOfSamplesPerTask;

				Future<Double> value = executor.submit(() -> getApproximationOfPiWithHalton(startIndex, numberOfSamplesPerTask));
				results.add(value);
			}

			/*
			 * Collect the results
			 */
			double sum = 0.0;
			for(int taskIndex = 0; taskIndex<numberOfTask; taskIndex++) {
				sum += results.get(taskIndex).get();
			}
			double piMersenne = sum / numberOfTask;

			long timeEnd = System.currentTimeMillis();

			double timeInSeconds = (timeEnd-timeStart) / 1000.0;

			System.out.println("Halton, parallel using Executor....: " + (piMersenne-piAnalytic) + "\t" + timeInSeconds + " sec.");
		}
		finally {
			executor.shutdown();
		}
	}

	private static double getApproximationOfPiWithMersenne(long seed, int numberOfSamples) {
		org.apache.commons.math3.random.MersenneTwister mersenne = new org.apache.commons.math3.random.MersenneTwister(seed);

		int numberOfSamplesInUnitCircle = 0;

		for(int i = 0; i<numberOfSamples; i++) {
			double x = 2.0 * (mersenne.nextDouble()-0.5);
			double y = 2.0 * (mersenne.nextDouble()-0.5);
			if(x*x + y*y < 1) numberOfSamplesInUnitCircle++;
		}

		return 4.0 * numberOfSamplesInUnitCircle / numberOfSamples;
	}

	private static double getApproximationOfPiWithHalton(int startIndex, int numberOfSamples) {

		int numberOfSamplesInUnitCircle = 0;

		for(int i = startIndex; i<startIndex+numberOfSamples; i++) {
			double x = 2.0 * (HaltonSequence.getHaltonNumberForGivenBase(i, 2)-0.5);
			double y = 2.0 * (HaltonSequence.getHaltonNumberForGivenBase(i, 3)-0.5);
			if(x*x + y*y < 1) numberOfSamplesInUnitCircle++;
		}

		return 4.0 * numberOfSamplesInUnitCircle / numberOfSamples;
	}
}
