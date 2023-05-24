package info.quantlab.numericalmethods.assignments.montecarlo.check;

import java.util.function.DoubleBinaryOperator;

import info.quantlab.numericalmethods.lecture.montecarlo.integration.IntegratorFactory;
import info.quantlab.numericalmethods.lecture.montecarlo.integration.MonteCarloIntegratorFactory;

public interface MonteCarloIntegrationAssignment {

	/**
	 * The solution of the first part of exercise, implementing a MonteCarloIntegratorFactory providing a MonteCarloIntegrator
	 *
	 * @return A class implementing MonteCarloIntegratorFactory
	 */
	MonteCarloIntegratorFactory getMonteCarloIntegratorFactory();

	/**
	 * The solution of the second part of exercise.
	 *
	 * Calculating the integral f(x,y) dx dy for a general binary operator
	 * using the integration domain [a,b] x [c,d]
	 * using your Monte-Carlo Integrator.
	 *
	 * @param function The function to integrate, given as a {@link DoubleBinaryOperator}.
	 * @param lowerBoundX The lower bound a for the integral of dx.
	 * @param upperBoundX The upper bound b for the integral of dx.
	 * @param lowerBoundY The lower bound c for the integral of dy.
	 * @param upperBoundY The upper bound d for the integral of dy.
	 * @return The value of the integral f(x,y) dx dy.
	 */
	double getIntegral(DoubleBinaryOperator function, double lowerBoundX, double upperBoundX, double lowerBoundY, double upperBoundY);

	/**
	 * The solution of the third part of exercise, implementing a IntegratorFactory providing a SimpsonsIntegrator
	 *
	 * @return A class implementing IntegratorFactory
	 */
	IntegratorFactory getSimpsonsIntegratorFactory();
}
