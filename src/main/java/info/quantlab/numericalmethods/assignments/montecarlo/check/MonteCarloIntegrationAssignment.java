package info.quantlab.numericalmethods.assignments.montecarlo.check;

import java.util.function.DoubleBinaryOperator;

import info.quantlab.numericalmethods.lecture.montecarlo.integration.MonteCarloIntegratorFactory;

public interface MonteCarloIntegrationAssignment {

	MonteCarloIntegratorFactory getMonteCarloIntegratorFactory();

	double getIntegral(DoubleBinaryOperator function, double lowerBoundX, double upperBoundX, double lowerBoundY, double upperBoundY);
}
