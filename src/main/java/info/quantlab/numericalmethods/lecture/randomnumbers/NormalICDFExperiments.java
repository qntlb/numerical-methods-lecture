package info.quantlab.numericalmethods.lecture.randomnumbers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.finmath.functions.NormalDistribution;
import net.finmath.plots.Plots;
import net.finmath.randomnumbers.MersenneTwister;
import net.finmath.randomnumbers.SobolSequence1D;

public class NormalICDFExperiments {

	public static void main(String[] args) throws Exception {

		/*
		 * Plot ICDF from different uniforms
		 */

		testICDFWithMersenneTwister();
		testICDFWithVanDerCorput();
		testICDFWithSobol();
		testICDFJavaRandom();
		testICDFWithMersenneTwister();


		testICDFImplementations();
	}

	private static void testICDFImplementations() {
		double p;

		p = 0.0;
		testICDFImplementationApacheMath3(p);
		testICDFImplementationFinmathWichura(p);

		p = 1.0;
		testICDFImplementationApacheMath3(p);
		testICDFImplementationFinmathWichura(p);

		p = Math.pow(2, -53);
		testICDFImplementationApacheMath3(p);
		testICDFImplementationFinmathWichura(p);

		p = Double.MIN_VALUE;
		testICDFImplementationApacheMath3(p);
		testICDFImplementationFinmathWichura(p);

		p = 1.0-Math.pow(2, -53);
		testICDFImplementationApacheMath3(p);
		testICDFImplementationFinmathWichura(p);

		p = 1.0-Math.pow(2, -54);
		testICDFImplementationApacheMath3(p);
		testICDFImplementationFinmathWichura(p);
	}

	private static void testICDFImplementationFinmathWichura(double p) {

		System.out.println("_".repeat(80));

		System.out.println("Testing Wichura implementation");

		org.apache.commons.math3.distribution.NormalDistribution normal = new org.apache.commons.math3.distribution.NormalDistribution();

		System.out.println("p       = " + p);

		double x = NormalDistribution.inverseCumulativeNormalDistributionWichura(p);

		System.out.println("x(p)    = " + x);

		double pFromX = normal.cumulativeProbability(x);

		System.out.println("p(x(p)) = " + pFromX);

		double expFromX = Math.exp(x);

		System.out.println("exp(x)  = " + expFromX);
	}

	private static void testICDFImplementationApacheMath3(double p) {

		System.out.println("_".repeat(80));

		System.out.println("Testing Apache Common Math implementation");

		org.apache.commons.math3.distribution.NormalDistribution normal = new org.apache.commons.math3.distribution.NormalDistribution();

		System.out.println("p       = " + p);

		double x = normal.inverseCumulativeProbability(p);

		System.out.println("x(p)    = " + x);

		double pFromX = normal.cumulativeProbability(x);

		System.out.println("p(x(p)) = " + pFromX);

		double expFromX = Math.exp(x);

		System.out.println("exp(x)  = " + expFromX);
	}


	private static void testICDFJavaRandom() throws Exception {
		Random random = new Random(3636);

		List<Double> valuesNormal = new ArrayList<>();
		for(int i = 0; i<100000; i++) {
			double uniform = random.nextFloat();

			double normal = NormalDistribution.inverseCumulativeNormalDistributionWichura(uniform);

			valuesNormal.add(normal);
		}

		Plots.createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via ICDF from Java Random (LCG) sequence").show();
	}


	private static void testICDFWithSobol() throws Exception {
		SobolSequence1D sobol = new SobolSequence1D();
		sobol.nextDouble();	// remove first number in sequence being u=0.

		org.apache.commons.math3.distribution.NormalDistribution normalDistribution =
				new org.apache.commons.math3.distribution.NormalDistribution();

		List<Double> valuesNormal = new ArrayList<>();
		for(int i = 0; i<100000; i++) {
			double uniform = sobol.nextDouble();

			double normal = normalDistribution.inverseCumulativeProbability(uniform);

			valuesNormal.add(normal);

		}

		Plots.createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via ICDF from Sobol sequence").show();
	}

	private static void testICDFWithMersenneTwister() throws Exception {

		MersenneTwister mersenne = new MersenneTwister(3636);

		org.apache.commons.math3.distribution.NormalDistribution normalDistribution =
				new org.apache.commons.math3.distribution.NormalDistribution();

		List<Double> valuesNormal = new ArrayList<>();
		List<Double> valuesUniform = new ArrayList<>();
		for(int i = 0; i<100000; i++) {
			double uniform = mersenne.nextDouble();

			double normal = normalDistribution.inverseCumulativeProbability(uniform);

			valuesUniform.add(uniform);
			valuesNormal.add(normal);
		}

		Plots.createHistogram(valuesUniform, 100, 4.0)
		.setTitle("Uniform from MersenneTwister").show();

		Plots.createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via ICDF from MersenneTwister").show();
	}

	private static void testICDFWithVanDerCorput() throws Exception {

		VanDerCorputSequence uniformSequence = new VanDerCorputSequence(2);

		org.apache.commons.math3.distribution.NormalDistribution normalDistribution =
				new org.apache.commons.math3.distribution.NormalDistribution();

		List<Double> valuesNormal = new ArrayList<>();
		List<Double> valuesUniform = new ArrayList<>();
		for(int i = 0; i<100000; i++) {
			double uniform = uniformSequence.getAsDouble();

			double normal = normalDistribution.inverseCumulativeProbability(uniform);

			valuesUniform.add(uniform);
			valuesNormal.add(normal);
		}

		Plots.createHistogram(valuesUniform, 100, 4.0)
		.setTitle("Uniform from VanDerCorput").show();

		Plots.createHistogram(valuesNormal, 100, 4.0)
		.setTitle("Normal via ICDF from VanDerCorput").show();
	}
}
