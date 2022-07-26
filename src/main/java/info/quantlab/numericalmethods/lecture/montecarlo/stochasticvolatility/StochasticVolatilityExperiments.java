package info.quantlab.numericalmethods.lecture.montecarlo.stochasticvolatility;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.IntStream;

import net.finmath.exception.CalculationException;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.HestonModel;
import net.finmath.montecarlo.assetderivativevaluation.models.HestonModel.Scheme;
import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;
import net.finmath.montecarlo.model.ProcessModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.montecarlo.process.MonteCarloProcessFromProcessModel;
import net.finmath.plots.DoubleToRandomVariableFunction;
import net.finmath.plots.Plot2D;
import net.finmath.plots.PlotProcess2D;
import net.finmath.plots.Plots;
import net.finmath.stochastic.RandomVariable;
import net.finmath.stochastic.Scalar;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

/**
 * 
 * 
 * @author Christian Fries
 */
public class StochasticVolatilityExperiments {

	// Model properties
	private final double	initialValue   = 1.0;
	private final double	riskFreeRate   = 0.05;
	private final double	volatility     = 0.30;

	private final double theta = volatility*volatility;
	private final double kappa = 0.0;
	private final double xi;				// The parameter will be set differently
	private final double rho = 0.0;

	private final Scheme scheme = Scheme.FULL_TRUNCATION;

	// Process discretization properties
	private final int		numberOfPaths		= 100000;
	private final int		numberOfTimeSteps	= 500;
	private final double	deltaT				= 0.01;

	private final int		seed				= 31415;

	// Product properties
	private final int		assetIndex = 0;
	private final double	optionMaturity = 5.0;
	private final double	optionStrike = 1.10;

	private static DecimalFormat formatReal3 = new DecimalFormat("####0.000", new DecimalFormatSymbols(Locale.ENGLISH));

	public StochasticVolatilityExperiments(double xi) {
		this.xi = xi;
	}


	public static void main(String[] args) throws CalculationException, IOException {

		// Model corresponds to a Black-Scholes model
		new StochasticVolatilityExperiments(0.00).analyse();

		// Model has a stochastic volatility
		new StochasticVolatilityExperiments(0.15).analyse();
	}


	public void analyse() throws CalculationException, IOException {
		// Create a time discretization
		final TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(0.0 /* initial */, numberOfTimeSteps, deltaT);

		final BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretization, 2 /* numberOfFactors */, numberOfPaths, seed);

		final ProcessModel model = new HestonModel(initialValue, riskFreeRate, volatility, theta, kappa, xi, rho, scheme);

		// Create a corresponding MC process
		final MonteCarloProcessFromProcessModel process = new EulerSchemeFromProcessModel(model, brownianMotion);

		final MonteCarloAssetModel simulation = new MonteCarloAssetModel(process);

		/*
		 * Generate some interesting objects (underlying, paths, quadratcVariation)
		 */
		RandomVariable underlying = simulation.getAssetValue(optionMaturity, 0);

		DoubleToRandomVariableFunction paths = t -> simulation.getAssetValue(t, assetIndex).log();

		RandomVariable quadratcVariation = IntStream.range(0, timeDiscretization.getNumberOfTimeSteps())
				.mapToObj(i -> {
					try {
						return simulation.getAssetValue(i+1, assetIndex).log()
								.sub(
										simulation.getAssetValue(i, assetIndex).log())
								.squared()
								.div(optionMaturity);
					} catch (CalculationException e) {
						return new Scalar(Double.NaN);
					}
				})
				.reduce((x1,x2)-> x2.add(x1)).get();

		/*
		 * Plots: Sample paths
		 */
		int numberOfPathsToPlot = 1000;
		Color[] colors = new Color[numberOfPathsToPlot];
		double x1 = 0.06;//quadratcVariation.getAverage()+0*quadratcVariation.getStandardDeviation();
		double x2 = 0.12;//quadratcVariation.getAverage()+1*quadratcVariation.getStandardDeviation();
		double x3 = 0.20;//quadratcVariation.getAverage()+3*quadratcVariation.getStandardDeviation();
		for(int i=0; i<numberOfPathsToPlot; i++) {
			double v = quadratcVariation.get(i); 
			float green = (float) Math.min(Math.max(x2-v,0)/x2,1);
			float blue = (float) Math.max(1-(Math.abs(((v-x1)-(x2-x1)/2))/((x2-x1)/2)),0);
			float red = (float) Math.min(Math.max(v/x3,0),1);
			colors[i] = new Color(red, green, blue); 
		}

		new PlotProcess2D(timeDiscretization, paths, numberOfPathsToPlot)
			.setColors(colors)
			.setTitle("Simulation path (colored by quatratic variation) (\u03C3=" + volatility + ", \u03be=" + xi + ")")
			.setXAxisLabel("t")
			.setYAxisLabel("log(S(t))")
			.saveAsPDF(new File("images/StochasticVolatilityExperiments-paths-xi" + (int)(xi * 100) + ".pdf"), 960, 600)
			.show();

		/*
		 * Plots: Density of underlying
		 */
		Plots.createDensity(underlying.log(), 200, 6.0)
			.setTitle("Density (\u03C3=" + volatility + ", \u03be=" + xi + ")")
			.setXAxisLabel("S(T)")
			.setYRange(0, 0.8)
			.saveAsPDF(new File("images/StochasticVolatilityExperiments-density-xi" + (int)(xi * 100) + ".pdf"), 960, 600)
			.show();

		/*
		 * Plots: Density of quadratcVariation
		 */
		Plots.createDensity(quadratcVariation, 200, 6.0)
			.setTitle("Quadratc Variation (\u03C3=" + volatility + ", \u03be=" + xi + ")")
			.setXAxisLabel("Quadratc Variation")
			.saveAsPDF(new File("images/StochasticVolatilityExperiments-density-quadratic-variation-xi" + (int)(xi * 100) + ".pdf"), 960, 600)
			.show();
		
		/*
		 * Plots: Implied volatility
		 */
		DoubleUnaryOperator volatilitySmile = strike -> {
			try {
				double value = new EuropeanOption(optionMaturity, strike, assetIndex).getValue(simulation);
				double impliedVolatility = AnalyticFormulas.blackScholesOptionImpliedVolatility(
						initialValue * Math.exp(riskFreeRate * optionMaturity),
						optionMaturity, strike, Math.exp(-riskFreeRate * optionMaturity), value);				
				return impliedVolatility;
			} catch (CalculationException e) {
				return Double.NaN;
			}
		};
		new Plot2D(1/4.0, 4.0, volatilitySmile)
			.setTitle("Implied Black-Schholes Volatility (\u03C3=" + volatility + ", \u03be=" + xi + ")")
			.setXAxisLabel("Strike")
			.setYAxisLabel("Implied Volatility")
			.setYRange(0.0, 0.6)
			.saveAsPDF(new File("images/StochasticVolatilityExperiments-impliedvol-xi" + (int)(xi * 100) + ".pdf"), 960, 600)
			.show();

		new Plot2D(-1.0, 1.0, logStrike -> volatilitySmile.applyAsDouble(Math.exp(logStrike)))
			.setTitle("Implied Black-Schholes Volatility (\u03C3=" + volatility + ", \u03be=" + xi + ")")
			.setXAxisLabel("log(Strike)")
			.setYAxisLabel("Implied Volatility")
			.setYRange(0.0, 0.6)
			.saveAsPDF(new File("images/StochasticVolatilityExperiments-impliedvol-log-xi" + (int)(xi * 100) + ".pdf"), 960, 600)
			.show();
	}
}
