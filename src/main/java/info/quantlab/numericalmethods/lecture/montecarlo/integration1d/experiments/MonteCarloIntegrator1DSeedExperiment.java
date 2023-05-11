package info.quantlab.numericalmethods.lecture.montecarlo.integration1d.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.Integrator1D;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.MonteCarloIntegrator1D;
import net.finmath.plots.Plots;
import net.finmath.randomnumbers.MersenneTwister;

public class MonteCarloIntegrator1DSeedExperiment {

	public static void main(String[] args) {

		System.out.println("""
				Testing Monte-Carlo integration usings different seed.
				"""
				);
		
		int numberOfEvaluationPoints = 10000;


		/*
		 * Test some special seeds
		 */
		for(int seed : new int[] { 3141, 1313, 1, 1632 }) {
			Integrator1D integratorMonteCarlo = new MonteCarloIntegrator1D(numberOfEvaluationPoints, new MersenneTwister(seed));
			testIntegrator(integratorMonteCarlo, true);
		}
		
		
		/*
		 * Test with random seeds (and plot the distribution of errors)
		 */
		List<Double> errors = new ArrayList<>();
		
		Random randomSeed = new Random(3141);
		for(int i=0; i< 10000; i++) {
			int seed = randomSeed.nextInt();
			Integrator1D integratorMonteCarlo = new MonteCarloIntegrator1D(numberOfEvaluationPoints, new MersenneTwister(seed));
			double error = testIntegrator(integratorMonteCarlo, false);
			errors.add(error);
		}
		
		Plots.createHistogram(errors, 50, 4.0).setTitle("Distribution of Monte-Carlo error").show();
				
		
	}

	private static double testIntegrator(Integrator1D integrator, boolean isPrintResult) {
		
		DoubleUnaryOperator integrand = x -> Math.cos(x);		
		DoubleUnaryOperator integralAnalytic = x -> Math.sin(x);
		
		double lowerBound = 0.0;
		double upperBound = 5.0;
		
		double integralValueAnalytic = integralAnalytic.applyAsDouble(upperBound) - integralAnalytic.applyAsDouble(lowerBound);
		double integralValueSimpsons = integrator.integrate(integrand, lowerBound, upperBound);
		
		double error = integralValueSimpsons - integralValueAnalytic;
		
		if(isPrintResult) System.out.println(String.format("%-35s  %20.16f  \u00b1 %5.3e", integrator.getClass().getSimpleName(), integralValueSimpsons, error));

		return error;
	}

}
