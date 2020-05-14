package info.quantlab.numericalmethods.lecture.randomnumbers;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import net.finmath.randomnumbers.MersenneTwister;

public class QuasiRandomNumberExperiments {

	public static void main(String[] args) {
		
		final int numberOfSamplePoints = 100000;
		
		// f(x) = x^3 then \int_0^1 f(x) dx = 0.25
		DoubleUnaryOperator function = x -> x*x*x;
		
		final double integralAnalytic = 0.25;
		
		/*
		 * Monte-Carlo with pseudo rng
		 */
		DoubleStream randomNumbers = DoubleStream.generate(new MersenneTwister(1)).limit(numberOfSamplePoints);
		
		double integralPseudoMC = randomNumbers.map(function).sum() / numberOfSamplePoints;
		
		System.out.println("integralPseudoRng..: " + integralPseudoMC + " \terror: " + (integralPseudoMC-integralAnalytic));

		/*
		 * Using Quasi rng
		 */
		DoubleStream randomNumberQuasi = IntStream.range(0,numberOfSamplePoints).mapToDouble(i -> (double)i/numberOfSamplePoints);
		
		double integralQuasiMC = randomNumberQuasi.map(function).sum() / numberOfSamplePoints;
		
		System.out.println("integralPseudoRng..: " + integralQuasiMC + " \terror: " + (integralQuasiMC-integralAnalytic));
	}

}
