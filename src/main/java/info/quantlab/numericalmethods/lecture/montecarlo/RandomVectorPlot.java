package info.quantlab.numericalmethods.lecture.montecarlo;


import java.io.File;

import net.finmath.plots.Plot;
import net.finmath.plots.Plots;

/**
 * Creates a sequence of vectors with uniform on [0,1) distributed components.
 *
 * The main method plots some points of a 2 dimensional vector.
 *
 * @author Christian Fries
 */
public class RandomVectorPlot {

	public static void main(String[] args) throws Exception {

		int numberOfPoints = 1000;

		RandomVector sequence = new RandomVector(numberOfPoints, 2);

		Plot plot = Plots.createScatter(
				sequence.getSequenceForComponent(0),
				sequence.getSequenceForComponent(1),
				0.0, 1.0, 3);

		plot.show();
		plot.saveAsPDF(new File("RandomNumbers2D-" + numberOfPoints + ".pdf"), 960, 600);
	}
}
