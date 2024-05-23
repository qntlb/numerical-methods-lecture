package info.quantlab.numericalmethods.lecture.montecarlo.integration1d.experiments;

import java.util.function.DoubleUnaryOperator;

import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.Integrator1D;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.MonteCarloIntegrator1D;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.MonteCarloIntegrator1DFromRandomGenerator1D;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.MonteCarloIntegrator1DWithStreams;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.QuasiMonteCarloIntegrator1D;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.QuasiMonteCarloIntegrator1DWithStreams;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.RiemannMidPointIntegrator1D;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.RiemannMidPointIntegrator1DWithStreams;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.SimpsonsIntegrator1D;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.SimpsonsIntegrator1DWithStreams;
import info.quantlab.numericalmethods.lecture.randomnumbers.VanDerCorputSequence;

public class Integrator1DExperiment {

	public static void main(String[] args) {

		System.out.println("""
				Testing several implementation of an 1D integrator.
				(Note that the Java streams implementation uses Kahan summation / error correction when performing .sum())
				"""
				);

		int numberOfEvaluationPoints = 10001;
		System.out.println("Number of evaluation points....: " + numberOfEvaluationPoints + String.format(" (\u2248 %5.2e)", (double)numberOfEvaluationPoints));
		System.out.println();

		System.out.println("Theoretical (relative) errors are:");
		System.out.println("\tMonte-Carlo integration.......(1/n)^{1/2}..: " + String.format("%5.2e", Math.pow(1.0/numberOfEvaluationPoints, 0.5)));
		System.out.println("\tSimpson's rule integration....(1/n)^{4}....: " + String.format("%5.2e", Math.pow(1.0/numberOfEvaluationPoints, 4.0)));
		System.out.println();

		Integrator1D integratorSimpsons = new SimpsonsIntegrator1D(numberOfEvaluationPoints);
		testIntegrator(integratorSimpsons);

		Integrator1D integratorSimpsonsWithStreams = new SimpsonsIntegrator1DWithStreams(numberOfEvaluationPoints);
		testIntegrator(integratorSimpsonsWithStreams);

		System.out.println();

		Integrator1D integratorMonteCarlo = new MonteCarloIntegrator1D(numberOfEvaluationPoints, 3141);
		testIntegrator(integratorMonteCarlo);

		Integrator1D integratorMonteCarloWithStreams = new MonteCarloIntegrator1DWithStreams(numberOfEvaluationPoints, 3141);
		testIntegrator(integratorMonteCarloWithStreams);

		System.out.println();

		Integrator1D integratorRiemannLeftPoints = new QuasiMonteCarloIntegrator1D(numberOfEvaluationPoints);
		testIntegrator(integratorRiemannLeftPoints);

		Integrator1D integratorRiemannLeftPointsWithStreams = new QuasiMonteCarloIntegrator1DWithStreams(numberOfEvaluationPoints);
		testIntegrator(integratorRiemannLeftPointsWithStreams);

		System.out.println();

		Integrator1D integratorRiemannMidPoints = new RiemannMidPointIntegrator1D(numberOfEvaluationPoints);
		testIntegrator(integratorRiemannMidPoints);

		Integrator1D integratorRiemannMidPointsWithStreams = new RiemannMidPointIntegrator1DWithStreams(numberOfEvaluationPoints);
		testIntegrator(integratorRiemannMidPointsWithStreams);
		
		System.out.println();

		Integrator1D monteCarloIntegratorWithVanDerCorputSeq = new MonteCarloIntegrator1DFromRandomGenerator1D(numberOfEvaluationPoints, () -> new VanDerCorputSequence(2));
		testIntegrator(monteCarloIntegratorWithVanDerCorputSeq);		
	}

	private static void testIntegrator(Integrator1D integrator) {

		DoubleUnaryOperator integrand = x -> Math.cos(x);
		DoubleUnaryOperator integralAnalytic = x -> Math.sin(x);

		double lowerBound = 0.0;
		double upperBound = 5.0;

		double integralValueAnalytic = integralAnalytic.applyAsDouble(upperBound) - integralAnalytic.applyAsDouble(lowerBound);

		double integralValueItegrator = integrator.integrate(integrand, lowerBound, upperBound);

		double error = integralValueItegrator - integralValueAnalytic;

		System.out.println(String.format("%-42s  %20.16f  \u00b1 %5.3e", integrator.getClass().getSimpleName(), integralValueItegrator, Math.abs(error)));
	}

}
