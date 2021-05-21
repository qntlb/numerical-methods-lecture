package info.quantlab.numericalmethods.lecture.randomnumbers.experiments;

import java.util.ArrayList;
import java.util.List;

import net.finmath.functions.NormalDistribution;
import net.finmath.plots.Plots;
import net.finmath.randomnumbers.MersenneTwister;

/**
 * Acceptance-Rejection sampling of the normal distribution.
 *
 * @author Christian Fries
 */
public class NormalDistributionWithAcceptanceRejectionExperiment {

	private static int numberOfSamples = 10000000;	// 10^7

	public static void main(String[] args) {

		testARWithMersenneTwister3D();
		testARWithMersenneTwister2D();
		testARWithHalton3D();
		testARWithHalton2D();
		testICDFWithMersenneTwister();
		testICDFWithHalton();
	}

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

				x = -Math.log(1-v);

				isRejected = u >= Math.exp(-0.5 * (x-1)*(x-1));
			}

			double s = mersenne.nextDouble() >= 0.5 ? 1.0 : -1.0;
			double normal = x * s;

			valuesNormal.add(normal);
		}

		long timeEnd = System.currentTimeMillis();

		double timeSec = (timeEnd-timeStart) / 1000.0;

		System.out.println("Time AR from MersenneTwister 3D...: " + timeSec + " sec.");

		Plots.createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via AR from MersenneTwister 3D").show();

	}

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

				normal = x * Math.signum(2*v-1);
			}

			valuesNormal.add(normal);
		}

		long timeEnd = System.currentTimeMillis();

		double timeSec = (timeEnd-timeStart) / 1000.0;

		System.out.println("Time AR from MersenneTwister 2D...: " + timeSec + " sec.");

		Plots.createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via AR from MersenneTwister 2D").show();

	}

	private static void testARWithHalton3D() {

		long timeStart = System.currentTimeMillis();

		int j = 0;
		List<Double> valuesNormal = new ArrayList<>();
		for(int i = 0; i<numberOfSamples; i++) {

			double normal = 0;
			boolean isRejected = true;

			while(isRejected) {
				double u = info.quantlab.numericalmethods.lecture.randomnumbers.HaltonSequence.getHaltonNumberForGivenBase(j, 2);
				double v = info.quantlab.numericalmethods.lecture.randomnumbers.HaltonSequence.getHaltonNumberForGivenBase(j, 3);
				double w = info.quantlab.numericalmethods.lecture.randomnumbers.HaltonSequence.getHaltonNumberForGivenBase(j++, 5);
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
		System.out.println("Time AR from Halton-Sequence 3D...: " + timeSec + " sec. Acceptance rate: " + acceptanceRate);

		Plots.createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via AR from Halton-Sequence 3D").show();
	}

	private static void testARWithHalton2D() {

		long timeStart = System.currentTimeMillis();

		int j = 0;
		List<Double> valuesNormal = new ArrayList<>();
		for(int i = 0; i<numberOfSamples; i++) {

			double normal = 0;
			boolean isRejected = true;

			while(isRejected) {
				double u = info.quantlab.numericalmethods.lecture.randomnumbers.HaltonSequence.getHaltonNumberForGivenBase(j, 2);
				double v = info.quantlab.numericalmethods.lecture.randomnumbers.HaltonSequence.getHaltonNumberForGivenBase(j++, 3);

				double x = -Math.log(1-Math.abs(2*v-1));

				isRejected = u >= Math.exp(-0.5 * (x-1)*(x-1));

				normal = x * Math.signum(2*v-1);
			}

			valuesNormal.add(normal);
		}

		long timeEnd = System.currentTimeMillis();

		double timeSec = (timeEnd-timeStart) / 1000.0;

		System.out.println("Time AR from Halton-Sequence 2D...: " + timeSec + " sec.");

		Plots.createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via AR from Halton-Seq. 2D").show();
	}

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

		System.out.println("Time ICDF from MersenneTwister 1D.: " + timeSec + " sec.");

		Plots.createHistogram(valuesNormal, 100, 4.0).
		setTitle("Normal via ICDF from MersenneTwister").show();
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

		System.out.println("Time ICDF from Halton-Sequence 1D.: " + timeSec + " sec.");

		Plots .createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via ICDF from Halton-Sequence").show();

	}
}
