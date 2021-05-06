package info.quantlab.numericalmethods.lecture.randomnumbers;

import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import net.finmath.plots.Plot2D;
import net.finmath.plots.PlotableFunction2D;
import net.finmath.plots.PlotablePoints2D;
import net.finmath.plots.Point2D;
import net.finmath.randomnumbers.MersenneTwister;

public class DiscrepancyExperiment {

	public static void main(String[] args) {
		
		
		final List<Double> samples = DoubleStream.generate(new MersenneTwister(3216)).limit(5).boxed().collect(Collectors.toList());

		DoubleUnaryOperator lambda = x -> {
		
			double d = x - (double)samples.stream().filter(y -> y < x).count()/samples.size();
			
			return d;
		};
		
		Plot2D plot = new Plot2D(List.of(
				new PlotableFunction2D(0, 1, 100, lambda),
				new PlotablePoints2D("Samples", samples.stream().map(x -> new Point2D(x,0)).collect(Collectors.toList()), null)
				));
		plot.setYRange(-0.5, 0.5);
		plot.show();
		
	}

}
