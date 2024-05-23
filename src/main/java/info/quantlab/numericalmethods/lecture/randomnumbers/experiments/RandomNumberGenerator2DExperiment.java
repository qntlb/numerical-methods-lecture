package info.quantlab.numericalmethods.lecture.randomnumbers.experiments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import info.quantlab.numericalmethods.lecture.randomnumbers.HaltonSequence;
import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGeneratorFrom1D;
import info.quantlab.numericalmethods.lecture.randomnumbers.VanDerCorputSequence;
import net.finmath.plots.Plot;
import net.finmath.plots.Plots;

/**
 * Plot three different sampling of a 2-D vector in [0,1]^2 - where one method is wrong!
 *
 * 1) sample a 2D vector by alternating on a pseudo random number generator (correct).
 * 2) sample a 2D vector by alternating on a quasi random number generator (incorrect).
 * 3) sample a 2D vector using a 2D quasi random number generator (correct).
 *
 * @author Christian Fries
 */
public class RandomNumberGenerator2DExperiment {

	public static void main(String[] args) throws Exception {
		RandomNumberGenerator generatorPseudo = new RandomNumberGeneratorFrom1D(new MersenneTwister(3141), 2);
		plotSamplePoints(generatorPseudo);

		RandomNumberGenerator generatorQuasi1 = new RandomNumberGeneratorFrom1D(new VanDerCorputSequence(2), 2);
		plotSamplePoints(generatorQuasi1);

		RandomNumberGenerator generatorQuasi2 = new HaltonSequence(new int[] { 2, 3 });
		plotSamplePoints(generatorQuasi2);
	}

	private static void plotSamplePoints(RandomNumberGenerator generator) throws Exception {

		List<double[]> points = new ArrayList<>();

		for(int i=0; i<1000; i++) {
			points.add(generator.getNext());
		}

		String title = generator.toString();

		Plot plot = Plots.createScatter(points, 0.0, 1.0, 0.0, 1.0, 3)
				.setTitle(title);
		plot.saveAsPDF(new File("Plot-RandomNumberGenerator2DExperiment-" + generator.toString() + ".pdf"), 1400, 800);
		plot.show();
	}
}
