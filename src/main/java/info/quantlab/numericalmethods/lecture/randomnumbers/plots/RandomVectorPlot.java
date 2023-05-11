package info.quantlab.numericalmethods.lecture.randomnumbers.plots;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.finmath.plots.Plots;

/**
 * Plot 1000 (pseudo-)random vectors (x,y) from [0,1)^2.
 * 
 * @author Christian Fries
 */
public class RandomVectorPlot {

	public static void main(String[] args) {

		int numberOfSamplePoints = 1000;
		
		// Initialize the random number generator with a seed
		Random random = new Random(3141);

		List<Double> xValues = new ArrayList<Double>();
		List<Double> yValues = new ArrayList<Double>();
		for(int i=0; i<numberOfSamplePoints; i++) {
			
			// Pick two elements from the sequence to create one element of the vector
			double x = random.nextDouble();
			double y = random.nextDouble();
			
			xValues.add(x);
			yValues.add(y);
				
		}
		
		Plots.createScatter(xValues, yValues, 0, 1, 3)
		.setTitle("Sample of a two dimensional random sequence with i.i.d. uniform components")
		.show();;
	}

}
