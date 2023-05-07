package info.quantlab.numericalmethods.lecture.montecarlo.integration1d.experiments;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.Integrator1D;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.MonteCarloIntegrator1D;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.MonteCarloIntegrator1DWithStreams;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.SimpsonsIntegrator1D;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.SimpsonsIntegrator1DWithStreams;
import net.finmath.randomnumbers.MersenneTwister;

public class Integrator1DExperiment {

	public static void main(String[] args) {

		System.out.println("""
				Testing several implementation of an 1D integrator.
				(Note that the Java streams implementation uses Kahan summation / error correction when performing .sum())
				"""
				);
		
		int numberOfEvaluationPoints = 10001;

		Integrator1D integratorSimpsons = new SimpsonsIntegrator1D(numberOfEvaluationPoints);
		testIntegrator(integratorSimpsons);

		Integrator1D integratorSimpsonsWithStreams = new SimpsonsIntegrator1DWithStreams(numberOfEvaluationPoints);
		testIntegrator(integratorSimpsonsWithStreams);

		Integrator1D integratorMonteCarlo = new MonteCarloIntegrator1D(numberOfEvaluationPoints, new MersenneTwister(3141));
		testIntegrator(integratorMonteCarlo);

		Integrator1D integratorMonteCarloWithStreams = new MonteCarloIntegrator1DWithStreams(new MersenneTwister(3141), numberOfEvaluationPoints);
		testIntegrator(integratorMonteCarloWithStreams);
	}

	private static void testIntegrator(Integrator1D integrator) {
		
		DoubleUnaryOperator integrand = x -> Math.cos(x);		
		DoubleUnaryOperator integralAnalytic = x -> Math.sin(x);
		
		double lowerBound = 0.0;
		double upperBound = 5.0;
		
		double integralValueAnalytic = integralAnalytic.applyAsDouble(upperBound) - integralAnalytic.applyAsDouble(lowerBound);
		double integralValueSimpsons = integrator.integrate(integrand, lowerBound, upperBound);
		
		double error = integralValueSimpsons - integralValueAnalytic;
		System.out.println(String.format("%-35s  %20.16f  \u00b1 %5.3e", integrator.getClass().getSimpleName(), integralValueSimpsons, error));
	}

}
