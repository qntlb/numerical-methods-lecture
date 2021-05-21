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

public class RandomNumberGenerator2DExperiment {

	public static void main(String[] args) throws Exception {
		RandomNumberGenerator generatorPseudo = new RandomNumberGeneratorFrom1D(new MersenneTwister(3141), 2);
		RandomNumberGenerator generatorQuasi1 = new RandomNumberGeneratorFrom1D(new VanDerCorputSequence(2), 2);
		RandomNumberGenerator generatorQuasi2 = new HaltonSequence(new int[] { 2, 3 });
		
		plotSamplePoints(generatorPseudo);
		plotSamplePoints(generatorQuasi1);
		plotSamplePoints(generatorQuasi2);
	}

	private static void plotSamplePoints(RandomNumberGenerator generator) throws Exception {

		List<double[]> points = new ArrayList<double[]>();
		
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
