package info.quantlab.numericalmethods.lecture.randomnumbers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.finmath.functions.NormalDistribution;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;
import net.finmath.plots.Plots;
import net.finmath.randomnumbers.MersenneTwister;
import net.finmath.stochastic.RandomVariable;

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
				double u = net.finmath.randomnumbers.HaltonSequence.getHaltonNumberForGivenBase(j, 2);
				double v = net.finmath.randomnumbers.HaltonSequence.getHaltonNumberForGivenBase(j, 3);
				double w = net.finmath.randomnumbers.HaltonSequence.getHaltonNumberForGivenBase(j++, 5);
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
				double u = net.finmath.randomnumbers.HaltonSequence.getHaltonNumberForGivenBase(j, 2);
				double v = net.finmath.randomnumbers.HaltonSequence.getHaltonNumberForGivenBase(j++, 3);

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
			double uniform = net.finmath.randomnumbers.HaltonSequence.getHaltonNumberForGivenBase(j++, 2);

			double normal = NormalDistribution.inverseCumulativeNormalDistributionWichura(uniform);

			valuesNormal.add(normal);
		}

		long timeEnd = System.currentTimeMillis();
		
		double timeSec = (timeEnd-timeStart) / 1000.0;
		
		System.out.println("Time ICDF from Halton-Sequence 1D.: " + timeSec + " sec.");

		Plots.createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via ICDF from Halton-Sequence").show();

	}
	
	private static void testARWithHalton() {

		HaltonSequence halton = new HaltonSequence(new int[] {2, 3, 5});
		MersenneTwister mersenne = new MersenneTwister(3636);

		List<Double> valuesNormal = new ArrayList<>();
		int j = 0;
		for(int i = 0; i<numberOfSamples; i++) {
			double u = 1.0, x = 0.0, s = 1.0;
			double[] uniform = null;
			while(u >= Math.exp(-0.5 * (x-1)*(x-1))) {
				uniform = halton.getSamplePoint(j++);
				u = uniform[0];
				double v = 2*(uniform[1]-0.5);
				s = Math.signum(v);
				double y = Math.abs(v);
				x = -Math.log(y);
			}
			double normal = x * s;
			
			valuesNormal.add(normal);
		}
		
		Plots.createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via AR from Halton (" + valuesNormal.size() + " samples)").show();

		
	}

	private static void testAR2WithHalton() throws Exception {

		
		MersenneTwister mersenne = new MersenneTwister(3636);

		List<Double> valuesNormal = new ArrayList<>();
		int j = 0;
		double p = Math.sqrt(numberOfSamples);
		for(int i = 0; i<numberOfSamples; i++) {
			double u = 1.0, x = 0.0, s = 1.0;
	
			while(u >= Math.exp(-0.5 * (x-1)*(x-1))) {
				double uniform = VanDerCorputSequence.getVanDerCorputNumber(j++, 2);
				double uniform1 = ((int)(uniform*p))/p;
				double uniform2 = uniform*p - uniform1*p;
				u = uniform1;
				double v = 2*(uniform2-0.5);
				s = Math.signum(v);
				double y = Math.abs(v);
				x = -Math.log(y);
			}
			double normal = x * s;
			
			valuesNormal.add(normal);
		}
		
		Plots.createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via AR from Halton (" + valuesNormal.size() + " samples)").show();

		
	}

	private static void testAR3WithHalton() throws Exception {

		

		List<Double> valuesNormal = new ArrayList<>();
		int j = 0;
		double p = Math.sqrt(numberOfSamples);
		for(int i = 0; i<numberOfSamples; i++) {
			double u = 1.0, x = 0.0, s = 1.0;
	
				while(u >= Math.exp(-0.5 * (x-1)*(x-1))) {

					double uniform1 = VanDerCorputSequence.getVanDerCorputNumber(j, 2);
					double uniform2 = VanDerCorputSequence.getVanDerCorputNumber(j++, 3);

//				double uniform1 = ((int)(uniform*p))/p;
//				double uniform2 = uniform*p - uniform1*p;
				u = uniform1;
				double v = 2*(uniform2-0.5);
				s = Math.signum(v);
				double y = Math.abs(v);
				x = -Math.log(y);
			}
			double normal = x * s;
			
			valuesNormal.add(normal);
		}
		
		Plots.createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via AR from Halton (" + valuesNormal.size() + " samples)").show();

		RandomVariable xrv = new RandomVariableFromDoubleArray(0.0, ArrayUtils.toPrimitive(valuesNormal.toArray(new Double[valuesNormal.size()])));
		Plots.createHistogramBehindValues(xrv, xrv.apply(x -> 1.0/Math.sqrt(2*Math.PI) * Math.exp(-0.5*x*x)), 100, 4.0).show();
		
	}

}
