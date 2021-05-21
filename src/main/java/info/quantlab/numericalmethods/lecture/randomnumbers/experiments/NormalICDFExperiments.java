package info.quantlab.numericalmethods.lecture.randomnumbers.experiments;

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

		testNormalICDFJavaRandom();
		testNormalICDFWithMersenneTwister();
		testNormalICDFWithVanDerCorput();
		testNormalICDFWithSobol();

		testICDFImplementations();
	}

	private static void testICDFImplementations() {
		double p;

		p = 0.0;
		testICDFImplementationApacheMath3(p);
		testICDFImplementationFinmathWichura(p);
		System.out.println("=".repeat(80));

		p = Double.MIN_VALUE;
		testICDFImplementationApacheMath3(p);
		testICDFImplementationFinmathWichura(p);
		System.out.println("=".repeat(80));

		p = Math.pow(2, -53);
		testICDFImplementationApacheMath3(p);
		testICDFImplementationFinmathWichura(p);
		System.out.println("=".repeat(80));

		p = 1.0-Math.pow(2, -53);
		testICDFImplementationApacheMath3(p);
		testICDFImplementationFinmathWichura(p);
		System.out.println("=".repeat(80));

		p = 1.0;
		testICDFImplementationApacheMath3(p);
		testICDFImplementationFinmathWichura(p);
	}

	private static void testICDFImplementationFinmathWichura(double uniform) {

		System.out.println("Testing finmath lib Wichura implementation (u = uniform, x = normal)");
		System.out.println("_".repeat(80));

		org.apache.commons.math3.distribution.NormalDistribution normal = new org.apache.commons.math3.distribution.NormalDistribution();

		System.out.println("        u = " + uniform);

		double x = NormalDistribution.inverseCumulativeNormalDistributionWichura(uniform);

		System.out.println("     x(u) = " + x);

		double p = normal.cumulativeProbability(x);

		System.out.println("  u(x(u)) = " + p);

		double expPlusX = Math.exp(x);

		System.out.println("   exp(x) = " + expPlusX);

		double expMinusX = Math.exp(-x);

		System.out.println("  exp(-x) = " + expMinusX);

		System.out.println("_".repeat(80));
		System.out.println();
	}

	private static void testICDFImplementationApacheMath3(double uniform) {

		System.out.println("Testing Apache Common Math implementation (u = uniform, x = normal)");

		System.out.println("_".repeat(80));

		org.apache.commons.math3.distribution.NormalDistribution normal = new org.apache.commons.math3.distribution.NormalDistribution();

		System.out.println("        u = " + uniform);

		double x = normal.inverseCumulativeProbability(uniform);

		System.out.println("     x(u) = " + x);

		double p = normal.cumulativeProbability(x);

		System.out.println("  u(x(u)) = " + p);

		double expPlusX = Math.exp(x);

		System.out.println("   exp(x) = " + expPlusX);

		double expMinusX = Math.exp(-x);

		System.out.println("  exp(-x) = " + expMinusX);

		System.out.println("_".repeat(80));
		System.out.println();
	}


	private static void testNormalICDFJavaRandom() throws Exception {
		Random random = new Random(3636);

		List<Double> valuesUniform = new ArrayList<>();
		List<Double> valuesNormal = new ArrayList<>();
		for(int i = 0; i<100000; i++) {
			double uniform = random.nextFloat();

			double normal = NormalDistribution.inverseCumulativeNormalDistributionWichura(uniform);

			valuesUniform.add(uniform);
			valuesNormal.add(normal);
		}

		Plots.createDensity(valuesUniform, 300, 4.0)
		.setTitle("Normal via ICDF from Java Random (LCG) sequence").show();

		Plots.createDensity(valuesNormal, 300, 4.0)
		.setTitle("Normal via ICDF from Java Random (LCG) sequence").show();
	}


	private static void testNormalICDFWithSobol() throws Exception {
		SobolSequence1D sobol = new SobolSequence1D();
		sobol.nextDouble();	// remove first number in sequence being u=0.

		org.apache.commons.math3.distribution.NormalDistribution normalDistribution =
				new org.apache.commons.math3.distribution.NormalDistribution();

		List<Double> valuesUniform = new ArrayList<>();
		List<Double> valuesNormal = new ArrayList<>();
		for(int i = 0; i<100000; i++) {
			double uniform = sobol.nextDouble();

			double normal = normalDistribution.inverseCumulativeProbability(uniform);

			valuesUniform.add(uniform);
			valuesNormal.add(normal);
		}

		Plots.createDensity(valuesUniform, 300, 4.0)
		.setTitle("Normal via ICDF from Sobol sequence").show();

		Plots.createDensity(valuesNormal, 300, 4.0)
		.setTitle("Normal via ICDF from Sobol sequence").show();
	}

	private static void testNormalICDFWithMersenneTwister() throws Exception {

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

		Plots.createDensity(valuesUniform, 300, 4.0)
		.setTitle("Uniform from MersenneTwister").show();

		Plots.createDensity(valuesNormal, 300, 4.0)
		.setTitle("Normal via ICDF from MersenneTwister").show();
	}

	private static void testNormalICDFWithVanDerCorput() throws Exception {

		var uniformSequence = new net.finmath.randomnumbers.VanDerCorputSequence(2);

		List<Double> valuesNormal = new ArrayList<>();
		List<Double> valuesUniform = new ArrayList<>();
		for(int i = 0; i<100000; i++) {
			double uniform = uniformSequence.getAsDouble();

			double normal = net.finmath.functions.NormalDistribution.inverseCumulativeDistribution(uniform);

			valuesUniform.add(uniform);
			valuesNormal.add(normal);
		}

		Plots.createDensity(valuesUniform, 300, 4.0)
		.setTitle("Uniform from VanDerCorput").show();

		Plots.createDensity(valuesNormal, 300, 4.0)
		.setTitle("Normal via ICDF from VanDerCorput").show();
	}
}
