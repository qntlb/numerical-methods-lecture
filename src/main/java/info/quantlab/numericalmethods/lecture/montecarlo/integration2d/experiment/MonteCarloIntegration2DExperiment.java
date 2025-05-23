package info.quantlab.numericalmethods.lecture.montecarlo.integration2d.experiment;

import java.util.function.BiFunction;

/**
 * Monte-Carlo integration of
 * integral cos(x) * cos(y) dx dy, x = -pi/2 to pi/2, y = -pi/2 to pi/2
 */
public class MonteCarloIntegration2DExperiment {

	private static final int numberOfSamples = 1000000;

	public static void main(String[] args) {
		
		double xlower = -Math.PI/2;
		double xupper = +Math.PI/2;
		double ylower = -Math.PI/2;
		double yupper = +Math.PI/2;
		
		BiFunction<Double,Double,Double> integrand = (x,y) -> Math.cos(x)*Math.cos(y);
		
		double integral = integrate(integrand, xlower, xupper, ylower, yupper);

		double integralAnalytic = (Math.sin(xupper)-Math.sin(xlower)) * (Math.sin(yupper)-Math.sin(ylower));
		double error = integral - integralAnalytic;
		
		System.out.println("Monte-Carlo Integral....: " + integral);
		System.out.println("Analytic Integral.......: " + integralAnalytic);
		System.out.println("Deviation...............: " + String.format("%5.2e", error));
		System.out.println("sqrt(1/N)...............: " + String.format("%5.2e", Math.sqrt(1.0/numberOfSamples)));
		System.out.println("Number of Samples.......: " + String.format("%5.2e", (double)numberOfSamples));

	}

	private static double integrate(BiFunction<Double,Double,Double> integrand, double xlower, double xupper, double ylower,
			double yupper) {
		
		double sum = 0.0;
		for(int i=0; i<numberOfSamples; i++) {

			double x = Math.random() * (xupper-xlower) + xlower;
			double y = Math.random() * (yupper-ylower) + ylower;
			
			sum += integrand.apply(x, y);
		}
		return sum/numberOfSamples * (xupper-xlower) * (yupper-ylower);
	}

}
