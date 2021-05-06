package info.quantlab.numericalmethods.lecture.montecarlo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

import net.finmath.plots.Plot2D;

public class RunningAverageOfIndicator {

	public static void main(String[] args) throws Exception {

		int numberOfPoints = 100;

		List<Double> averageValues1 = getRunningAveragesOfIndicator(3 /* omega */, numberOfPoints, 3105 /* seed */);
		List<Double> averageValues2 = getRunningAveragesOfIndicator(3 /* omega */, numberOfPoints, 1 /* seed */);

		DoubleUnaryOperator averageFunction1	= x -> averageValues1.get((int)x);
		DoubleUnaryOperator averageFunction2	= x -> averageValues2.get((int)x);
		DoubleUnaryOperator limitValueFunction	= x -> 1.0/6.0;

		Plot2D plot = new Plot2D(0.0, numberOfPoints-1, numberOfPoints, new DoubleUnaryOperator[] {
				averageFunction1,
				averageFunction2,
				limitValueFunction
		});

		plot.setTitle("Plot of running average of indicator function (x[i] == \u03C9[j]))");
		plot.setXAxisLabel("n");
		plot.setYAxisLabel("S(n)");
//		plot.saveAsPNG(new File("RunningAverageOfIndicator.png"), 960, 600);
		plot.show();
	}

	private static List<Double> getRunningAveragesOfIndicator(int omega, int numberOfSamples, int seed) {

		Random random = new Random(seed);

		List<Double> averages = new ArrayList<>();

		System.out.println("_".repeat(79));
		System.out.println("i" + "\t" + "x[i]" + "\t" + "indctr" + "\t" + "sum" + "\t" + "avg");
		System.out.println("_".repeat(79));

		int sum = 0;
		for(int i=0; i<numberOfSamples; i++) {

			int drawing = random.nextInt(6)+1;					// integer in { 1,2,3,4,5,6 }

			int indicator = (drawing == omega) ? 1 : 0;			// indicator function

			sum += indicator;									// sum up

			double average = ((double)sum)/(i+1);				// calculation the average

			averages.add(average);								// append the value to the list

			System.out.println((i+1) + "\t" + drawing + "\t" + indicator + "\t" + sum + "\t" + average);
		}
		System.out.println();

		return averages;
	}

}
