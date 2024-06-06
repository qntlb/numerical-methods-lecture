package info.quantlab.numericalmethods.lecture.randomnumbers.experiments;

import java.util.ArrayList;
import java.util.List;

import info.quantlab.numericalmethods.lecture.randomnumbers.HaltonSequence;
import net.finmath.functions.NormalDistribution;
import net.finmath.plots.Plots;
import net.finmath.randomnumbers.MersenneTwister;

/**
 * Acceptance-Rejection sampling of the normal distribution.
 * 
 * 3D: The sign and the ICDF of the exponential are generated from two uniforms, one additional uniform for the acceptance condition.
 * 2D: The sign and the exponential on |Y| is generated from one uniform, one additional uniform for the acceptance condition.
 *
 * We illustrate the method with pseudo random numbers (MersenneTwister) and quasi random numbers (HaltonSequence).
 * 
 * @author Christian Fries
 */
public class NormalDistributionWithAcceptanceRejectionExperiment {

	private static int numberOfSamples = 10000000;	// 10^7

	public static void main(String[] args) {

		testARWithMersenneTwister3D();
		testARWithMersenneTwister2D();
		testICDFWithMersenneTwister();
		
		testARWithHalton3D();
		testARWithHalton2D();
		testICDFWithHalton();
	}

	/*
	 * Using the Acceptance-Rejection sampling 3 uniforms (sign, value of |X| and condition
	 */

	private static void testARWithMersenneTwister3D() {

		long timeStart = System.currentTimeMillis();

		MersenneTwister mersenne = new MersenneTwister(3636);

		List<Double> valuesNormal = new ArrayList<>();
		for(int i = 0; i<numberOfSamples; i++) {

			double x = 0;
			boolean isRejected = true;

			while(isRejected) {
				double u = mersenne.nextDouble();
				double v = mersenne.nextDouble();

				x = -Math.log(1-v);		// exponentially distributed

				isRejected = u >= Math.exp(-0.5 * (x-1)*(x-1));
			}

			double s = mersenne.nextDouble() >= 0.5 ? 1.0 : -1.0;
			double normal = s * x;

			valuesNormal.add(normal);
		}

		long timeEnd = System.currentTimeMillis();

		double timeSec = (timeEnd-timeStart) / 1000.0;

		System.out.println("Time AR from MersenneTwister 3D.....: " + timeSec + " sec.");

		Plots.createDensity(valuesNormal, 100, 4.0)
		.setTitle("Normal via AR from MersenneTwister 3D").show();

	}

	/*
	 * Using the Acceptance-Rejection sampling 2 uniforms (value of X and condition)
	 */

	private static void testARWithMersenneTwister2D() {

		long timeStart = System.currentTimeMillis();

		MersenneTwister mersenne = new MersenneTwister(3636);

		List<Double> valuesNormal = new ArrayList<>();
		for(int i = 0; i<numberOfSamples; i++) {

			double normal = 0;
			boolean isRejected = true;

			while(isRejected) {
				double u = mersenne.nextDouble();
				double v = mersenne.nextDouble();

				double x = -Math.log(1-Math.abs(2*v-1));

				isRejected = u >= Math.exp(-0.5 * (x-1)*(x-1));

				double s = Math.signum(2*v-1);

				normal = s * x;
			}

			valuesNormal.add(normal);
		}

		long timeEnd = System.currentTimeMillis();

		double timeSec = (timeEnd-timeStart) / 1000.0;

		System.out.println("Time AR from MersenneTwister 2D.....: " + timeSec + " sec.");

		Plots.createDensity(valuesNormal, 100, 4.0)
		.setTitle("Normal via AR from MersenneTwister 2D").show();

	}

	/*
	 * Using the ICDF Method
	 */
	
	private static void testICDFWithMersenneTwister() {

		long timeStart = System.currentTimeMillis();

		MersenneTwister mersenne = new MersenneTwister(3636);

		List<Double> valuesNormal = new ArrayList<>();
		for(int i = 0; i<numberOfSamples; i++) {
			double uniform = mersenne.nextDouble();

			double normal = NormalDistribution.inverseCumulativeNormalDistributionWichura(uniform);

			valuesNormal.add(normal);
		}

		long timeEnd = System.currentTimeMillis();

		double timeSec = (timeEnd-timeStart) / 1000.0;

		System.out.println("Time ICDF from MersenneTwister 1D...: " + timeSec + " sec.");

		Plots.createDensity(valuesNormal, 100, 4.0).
		setTitle("Normal via ICDF from MersenneTwister").show();
	}


	/*
	 * The same experiment with the Halton sequence
	 */

	private static void testARWithHalton3D() {

		long timeStart = System.currentTimeMillis();

		HaltonSequence haltonSequence = new HaltonSequence(new int[] { 2, 3, 5 });

		int j = 0;				// Counting accepted AND rejected numbers.
		List<Double> valuesNormal = new ArrayList<>();
		for(int i = 0; i<numberOfSamples; i++) {

			double normal = 0;
			boolean isRejected = true;

			while(isRejected) {
				j++;
				double[] randomVector = haltonSequence.getNext();
				double u = randomVector[0];
				double v = randomVector[1];
				double w = randomVector[2];

				double s = w < 0.5 ? 1.0 : -1.0;

				double x = -Math.log(1-v);

				isRejected = u >= Math.exp(-0.5 * (x-1)*(x-1));

				normal = s*x;
			}

			valuesNormal.add(normal);
		}

		long timeEnd = System.currentTimeMillis();

		double timeSec = (timeEnd-timeStart) / 1000.0;

		double acceptanceRate = (double)numberOfSamples/j;
		System.out.println("Time AR from Halton-Sequence 3D.....: " + timeSec + " sec. Acceptance rate: " + acceptanceRate);

		Plots.createDensity(valuesNormal, 100, 4.0)
		.setTitle("Normal via AR from Halton-Sequence 3D").show();
	}

	private static void testARWithHalton2D() {

		long timeStart = System.currentTimeMillis();

		HaltonSequence haltonSequence = new HaltonSequence(new int[] { 2, 3 });

		List<Double> valuesNormal = new ArrayList<>();
		for(int i = 0; i<numberOfSamples; i++) {

			double normal = 0;
			boolean isRejected = true;

			while(isRejected) {
				double[] randomVector = haltonSequence.getNext();
				double u = randomVector[0];
				double v = randomVector[1];

				double x = -Math.log(1-Math.abs(2*v-1));

				isRejected = u >= Math.exp(-0.5 * (x-1)*(x-1));

				double s = Math.signum(2*v-1);

				normal = s * x;
			}

			valuesNormal.add(normal);
		}

		long timeEnd = System.currentTimeMillis();

		double timeSec = (timeEnd-timeStart) / 1000.0;

		System.out.println("Time AR from Halton-Sequence 2D.....: " + timeSec + " sec.");

		Plots.createDensity(valuesNormal, 100, 4.0)
		.setTitle("Normal via AR from Halton-Seq. 2D").show();
	}

	private static void testICDFWithHalton() {

		long timeStart = System.currentTimeMillis();

		int j = 0;
		List<Double> valuesNormal = new ArrayList<>();
		for(int i = 0; i<numberOfSamples; i++) {
			double uniform = info.quantlab.numericalmethods.lecture.randomnumbers.HaltonSequence.getHaltonNumberForGivenBase(j++, 2);

			double normal = NormalDistribution.inverseCumulativeNormalDistributionWichura(uniform);

			valuesNormal.add(normal);
		}

		long timeEnd = System.currentTimeMillis();

		double timeSec = (timeEnd-timeStart) / 1000.0;

		System.out.println("Time ICDF from Halton-Sequence 1D...: " + timeSec + " sec.");

		Plots.createDensity(valuesNormal, 300, 4.0)
		.setTitle("Normal via ICDF from Halton-Sequence").show();
	}
}
