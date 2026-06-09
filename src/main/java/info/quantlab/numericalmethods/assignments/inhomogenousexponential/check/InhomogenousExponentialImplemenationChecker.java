package info.quantlab.numericalmethods.assignments.inhomogenousexponential.check;

import java.util.Arrays;
import java.util.PrimitiveIterator.OfDouble;
import java.util.function.DoubleSupplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import info.quantlab.numericalmethods.assignments.inhomogenousexponential.InhomogenousExponentialAssignment;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator1D;
import info.quantlab.numericalmethods.lecture.randomnumbers.VanDerCorputSequence;

public class InhomogenousExponentialImplemenationChecker {

	public enum Check {
		BASIC("basic implementation check"),
		SAMPLING("sampling survival from 0.0 to 3.0"),
		ACCURACY("accurate generation of the distribution");

		private final String name;

		Check(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * Perform the test of different sub-tasks (whatToCheck) on solution.
	 *
	 * Perform exception handling.
	 *
	 * @param solution Solution of MonteCarloIntegrationAssignment
	 * @param whatToCheck Name of the subtask.
	 * @return true if successful, otherwise false.
	 */
	public static boolean check(InhomogenousExponentialAssignment solution, Check whatToCheck) {
		System.out.println("Running " + whatToCheck + " test on " + solution.getClass().getCanonicalName());

		boolean success = false;
		try {
			switch(whatToCheck) {
			case BASIC:
			default:
			{
				success = checkBasicFunctionality(solution);
			}
			break;
			case SAMPLING:
			{
				success = checkSampling(solution);
			}
			break;
			case ACCURACY:
			{
				success = checkAccuracy(solution);
			}
			break;
			}
		}
		catch(final Exception e) {
			System.out.println("\tTest '" + whatToCheck.getName() + "' failed with exception: " + e.getMessage());
			System.out.println("\nHere is a stack trace:");
			e.printStackTrace(System.out);
		}

		if(!success) {
			System.out.println("Sorry, the test failed.");
		}
		else {
			System.out.println("Congratulation! You solved the " + whatToCheck + " part of the exercise.");
		}
		System.out.println("_".repeat(79));
		return success;
	}

	private static boolean checkBasicFunctionality(InhomogenousExponentialAssignment solution) {
		final double[] times = new double[] { 1.0, 2.0, 3.0, 5.0 };
		final double[] intensities = new double[] { 1.0, 0.5, 2.0, 0.2, 0.1 };

		final RandomNumberGenerator1D uniforms = new VanDerCorputSequence(2);

		try {
			final DoubleSupplier defaultTimeSequence =
					solution.createRandomNumberGeneratorInhomogenousExponential(uniforms, times, intensities);

			final double test = defaultTimeSequence.getAsDouble();
		}
		catch(final Exception e) {
			System.out.println("\tCould not instanciate generator (DoubleSupplier): " + e.getMessage());
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private static boolean checkSampling(InhomogenousExponentialAssignment solution) {

		final double[] times = new double[] { 1.0, 2.0, 3.0, 5.0 };
		final double[] intensities = new double[] { 1.0, 0.5, 2.0, 0.2, 0.1 };

		System.out.println("\t\tUsing times " + Arrays.toString(times));
		System.out.println("\t\tUsing intensities " + Arrays.toString(intensities));

		final RandomNumberGenerator1D uniforms = new VanDerCorputSequence(2);
		final DoubleSupplier defaultTimeSequence = solution.createRandomNumberGeneratorInhomogenousExponential(uniforms, times, intensities);

		final int numberOfSamples = 100000;
		final double maturity = 3.0;

		long survivalCounter = 0;
		double sumOfTimes = 0.0;
		for(int i=0; i<numberOfSamples; i++) {

			final double time = defaultTimeSequence.getAsDouble();

			sumOfTimes += time;
			if(time > maturity) {
				survivalCounter++;
			}
		}

		final double averageTime = sumOfTimes / numberOfSamples;
		final double survivalProb = (double)survivalCounter / numberOfSamples;

		System.out.println();
		System.out.println("      E(\u03c4) = " + averageTime);
		System.out.println("  E(\u03c4 > T) = " + survivalProb + " (T = " + maturity + ")");
		System.out.println("exp(- \u03bb T) = " + Math.exp(-1.0) * Math.exp(-0.5) * Math.exp(-2.0));
		System.out.println("_".repeat(79));

		// Assert that we get the same survivalProb for maturity = 3.0;
		final double survialProbAnalytic = Math.exp(-1.0) * Math.exp(-0.5) * Math.exp(-2.0);
		final double deviation = Math.abs(survialProbAnalytic - survivalProb);
		if(deviation > 1E-4) {
			System.out.println("\tSurvival probability from 0 to 3.0 appears to be incorrect.");
			return false;
		}

		return true;
	}

	private static boolean checkAccuracy(InhomogenousExponentialAssignment solution) {
		final int numberOfSamples = 10000;
		final double[] inputUniforms = IntStream.range(0, numberOfSamples).mapToDouble(i -> (double)i/numberOfSamples).toArray();

		final double[] times = new double[] { 1.0, 2.0, 3.0, 5.0 };
		final double[] intensities = new double[] { 1.0, 0.5, 2.0, 0.2, 0.1 };

		System.out.println("\t\tUsing times " + Arrays.toString(times));
		System.out.println("\t\tUsing intensities " + Arrays.toString(intensities));

		final OfDouble iterator = Arrays.stream(inputUniforms).iterator();

		final RandomNumberGenerator1D uniformGenerator = () -> iterator.next();

		final DoubleSupplier defaultTimeSequence = solution.createRandomNumberGeneratorInhomogenousExponential(uniformGenerator, times, intensities);

		final DoubleStream defaultTimes = DoubleStream.generate(defaultTimeSequence).limit(numberOfSamples);

		final DoubleStream defaultTimesTransformed = defaultTimes.map(time -> transformedTime(time, times, intensities));

		final DoubleStream probabilities = defaultTimesTransformed.map(t -> 1.0-Math.exp(-t));

		final double[] probabilitiesArray = probabilities.toArray();

		for(int i=0; i<inputUniforms.length; i++) {
			if(Math.abs(inputUniforms[i]-probabilitiesArray[i]) > 1E-14) {
				System.out.println("\tUniform " + inputUniforms[i] + " appears to be converted inaccuarately.");
				return false;
			}
		}

		return true;
	}

	/**
	 * This is the function \( \Lambda(t) := \int_{0}^{t} \lambda(s) \mathrm{d}s \).
	 *
	 * @param time The time t.
	 * @param times The time discretization t_{i}
	 * @param intensities The intensity for the interval t_{i-1} to t_{i}, where t_{-1} := -\infinity and t_{n} := +\infinity.
	 * @return The time \Lambda(t)
	 */
	private static double transformedTime(double time, double[] times, double[] intensities) {

		double transformedTime = 0.0;
		double timeStart = 0.0;
		for(int i=0; i<intensities.length; i++) {
			final double timeEnd = i < times.length ? times[i] : Double.POSITIVE_INFINITY;
			final double transformedTimeStep = intensities[i] * (timeEnd - timeStart);
			if(timeEnd > time) {
				transformedTime += intensities[i] * (time-timeStart);
				break;
			}
			transformedTime += transformedTimeStep;
			timeStart = timeEnd;
		}

		return transformedTime;
	}
}
