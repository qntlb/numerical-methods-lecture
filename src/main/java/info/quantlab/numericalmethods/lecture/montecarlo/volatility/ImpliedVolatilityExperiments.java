package info.quantlab.numericalmethods.lecture.montecarlo.volatility;

import java.io.File;
import java.io.IOException;
import java.util.function.DoubleUnaryOperator;

import net.finmath.functions.AnalyticFormulas;
import net.finmath.plots.Plot2D;

/**
 * Small experiment to illustrate the implied volatility smile.
 *
 * @author Christian Fries
 */
public class ImpliedVolatilityExperiments {

	// Model properties
	private final double	initialValue   = 1.0;
	private final double	riskFreeRate   = 0.00;
	private final double	volatility     = 0.30;

	// Product properties
	private final double	optionMaturity = 5.0;

	/**
	 * Run the experiment.
	 *
	 * @param args Not used.
	 * @throws IOException Thrown if the image could not be stored.
	 */
	public static void main(String[] args) throws IOException {

		new ImpliedVolatilityExperiments().plotImpliedVols();
	}

	public void plotImpliedVols() throws IOException {

		final double forward = initialValue * Math.exp(riskFreeRate * optionMaturity);
		final DoubleUnaryOperator values = strike -> {
			final double value = AnalyticFormulas.bachelierOptionValue(forward, volatility, optionMaturity, strike, Math.exp(-riskFreeRate * optionMaturity));
			return value;
		};

		final DoubleUnaryOperator volatilitySmile = strike -> {
			final double value = values.applyAsDouble(strike);
			final double impliedVolatility = AnalyticFormulas.blackScholesOptionImpliedVolatility(forward, optionMaturity, strike, Math.exp(-riskFreeRate * optionMaturity), value);
			return impliedVolatility;
		};

		new Plot2D(1/4.0, 4.0, volatilitySmile)
		.setTitle("Implied Black-Scholes Volatility of Bachelier Values")
		.setXAxisLabel("Strike")
		.setYAxisLabel("Implied Volatility")
		.setYRange(0.0, 0.6)
		.saveAsPDF(new File("images/ImpliedVolBS.pdf"), 960, 600)
		.show();
	}
}
