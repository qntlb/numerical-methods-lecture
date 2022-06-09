package info.quantlab.numericalmethods.lecture.montecarlo.integration1d.experiments;

import java.text.DecimalFormat;
import java.util.function.DoubleUnaryOperator;

import info.quantlab.numericalmethods.lecture.randomnumbers.VanDerCorputSequence;
import net.finmath.randomnumbers.MersenneTwister;

public class MonteCarloIntegrationExperiment {

	private static DecimalFormat formatter = new DecimalFormat(" 0.000E00;-0.000E00");

	public static void main(String[] args) {

		DoubleUnaryOperator function = x -> x*x*x;

		double integralAnalytic = 0.25;

		int numberOfSamplePoints = 100000;

		MersenneTwister mersenne = new MersenneTwister(3141);

		System.out.println("Integration Errors:");
		System.out.println("n" + "\t" + "mersenne" + "\t" + "equidistant" + "\t" + "v.-d.-corput");

		/*
		 * Simultaneously calculate the partial integral using Mersenne, on equidistributed and using Van-Der-Corput
		 */

		double sumMersenneTwister = 0.0;
		double sumEquidistributed = 0.0;
		double sumVanDerCorput = 0.0;

		for(int i=0; i<numberOfSamplePoints; i++) {
			sumMersenneTwister += function.applyAsDouble(mersenne.nextDouble());
			sumEquidistributed += function.applyAsDouble((double)i/numberOfSamplePoints);
			sumVanDerCorput += function.applyAsDouble(VanDerCorputSequence.getVanDerCorputNumber(i, 2));

			int currentNumberOfSamplePoints = (i+1);

			double integralMersenneTwister = sumMersenneTwister / currentNumberOfSamplePoints;
			double errorMersenneTwister = integralMersenneTwister-integralAnalytic;

			double integralEquidistributed = sumEquidistributed / currentNumberOfSamplePoints;
			double errorEquidistributed = integralEquidistributed-integralAnalytic;

			double integralVanDerCorput = sumVanDerCorput / currentNumberOfSamplePoints;
			double errorVanDerCorput = integralVanDerCorput-integralAnalytic;

			// Print every 100 points the intermediate result
			if(currentNumberOfSamplePoints % 100 == 0) {
				System.out.println((i+1) + "\t" +
						formatter.format(errorMersenneTwister) + "\t" +
						formatter.format(errorEquidistributed) + "\t" +
						formatter.format(errorVanDerCorput));
			}
		}

		/*
		 * Calculate the final result.
		 */
		double integralMersenneTwister = sumMersenneTwister / numberOfSamplePoints;
		double errorMersenneTwister = integralMersenneTwister-integralAnalytic;

		double integralEquidistributed = sumEquidistributed / numberOfSamplePoints;
		double errorEquidistributed = integralEquidistributed-integralAnalytic;

		double integralVanDerCorput = sumVanDerCorput / numberOfSamplePoints;
		double errorVanDerCorput = integralVanDerCorput-integralAnalytic;

		System.out.println();

		System.out.println("Pseudo RNG....: " + integralMersenneTwister + "\t error: " + errorMersenneTwister);
		System.out.println("Equidistri....: " + integralEquidistributed + "\t error: " + errorEquidistributed);
		System.out.println("v.d.Corput....: " + integralVanDerCorput + "\t error: " + errorVanDerCorput);

	}

}
