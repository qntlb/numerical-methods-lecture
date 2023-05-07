package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;

public class SimpsonsIntegrator1D implements Integrator1D {

	private final int numberOfEvaluationPoints;
	
	public SimpsonsIntegrator1D(int numberOfEvaluationPoints) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
		
		if(numberOfEvaluationPoints%2 != 1) throw new IllegalArgumentException("numberOfEvaluationPoints needs to be odd");
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		int numberOfIntervals = numberOfEvaluationPoints-1;
		
		int numberOfDoubleSizeIntervals = numberOfIntervals / 2;
		
		double domain = upperBound-lowerBound;
		double integralStep = domain/numberOfIntervals;		// h 
		
		double integral = 0.0;
		for(int i=1; i<numberOfDoubleSizeIntervals; i++) {
			integral += 2 * integrand.applyAsDouble(lowerBound + (2*i+0) * integralStep);
			integral += 4 * integrand.applyAsDouble(lowerBound + (2*i+1) * integralStep);
		}

		// Add first and second and last evaluation point
		integral += integrand.applyAsDouble(lowerBound);
		integral += 4*integrand.applyAsDouble(lowerBound + integralStep);
		integral += integrand.applyAsDouble(upperBound);
		
		return integral * integralStep / 3;
	}

}
