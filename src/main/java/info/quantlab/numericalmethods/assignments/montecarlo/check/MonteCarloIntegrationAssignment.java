package info.quantlab.numericalmethods.assignments.montecarlo.check;

import java.util.function.DoubleBinaryOperator;

import info.quantlab.numericalmethods.lecture.montecarlo.integration.MonteCarloIntegratorFactory;

public interface MonteCarloIntegrationAssignment {

	/**
	 * The solution of the first part of exercise, implementing a MonteCarloIntegratorFactory providing a MonteCarloIntegrator
	 * 
	 * @return A class implementing MonteCarloIntegratorFactory
	 */
	MonteCarloIntegratorFactory getMonteCarloIntegratorFactory();

	/**
	 * The solution of the second part of exercise. Calculating the integral sin(x) * sin(y) dx dy
	 * with integration domain [a,b] x [c,d] 
	 * using your Monte-Carlo Integrator.
	 * 
	 * @param function The function to integrate.
	 * @param lowerBoundX The lower bound a for the integral of dx.
	 * @param upperBoundX The upper bound b for the integral of dx.
	 * @param lowerBoundY The lower bound c for the integral of dy.
	 * @param upperBoundY The upper bound d for the integral of dy.
	 * @return The value of the integral.
	 */
	double getIntegral(DoubleBinaryOperator function, double lowerBoundX, double upperBoundX, double lowerBoundY, double upperBoundY);
}
