package info.quantlab.numericalmethods.lecture.finitedifference;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import net.finmath.plots.Named;
import net.finmath.plots.Plot2D;

/**
 * An experiment illustrating issues in the finite difference approximation:
 * <ul>
 * 	<li>For large shifts we get a bias from the higher order terms.</li>
 * 	<li>For small shifts we get a numerical errors from IEEE754 rounding error.</li>
 * <li>
 * </ul>
 */
public class FiniteDifferenceExperiments {

	/**
	 * Calculate d/dx exp(x) at x = 0
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		double x = 0.0;

		printForwardFiniteDifferenceApproximationOfExp(x, 0.01 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 0.001 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 0.0001 /* shift */);

		System.out.println();

		printForwardFiniteDifferenceApproximationOfExp(x, Double.MIN_NORMAL /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1E-16 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1E-15 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1E-14 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1E-12 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1E-10 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1E-8 /* shift */);

		System.out.println();

		printForwardFiniteDifferenceApproximationOfExp(x, 0.49 * Math.pow(2, -52) /* shift h */);
		printForwardFiniteDifferenceApproximationOfExp(x, 0.51*Math.pow(2, -52) /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1.00*Math.pow(2, -52) /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1.49*Math.pow(2, -52) /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1.50*Math.pow(2, -52) /* shift */);

		// Forward Finite Difference for First Order Derivative of exp
		plotForwardFiniteDifferenceApproximationErrorOfExp(x, -16.5, -14);
		plotForwardFiniteDifferenceApproximationErrorOfExp(x, -20, -1);
		plotForwardFiniteDifferenceApproximationErrorOfExp(x, -10, -6);

		// Central Finite Difference for First Order Derivative of exp
		plotCentralFiniteDifferenceApproximationErrorOfExp(x, -16.5, -14);
		plotCentralFiniteDifferenceApproximationErrorOfExp(x, -20, -1);
		plotCentralFiniteDifferenceApproximationErrorOfExp(x, -7, -4);

		// Finite Difference for Second Order Derivative of cos
		plotSecondOrderFiniteDifferenceApproximationErrorOfCos(x, -9, 1);

		// Finite Difference for Third Order Derivative of exp
		plotThirdOrderFiniteDifferenceApproximationErrorOfExp(x, -4.5, -1);
	}

	private static void printForwardFiniteDifferenceApproximationOfExp(double x, double shift) {

		// Finite difference approximation
		double valueUpShift = Math.exp(x + shift);
		double value = Math.exp(x);

		double derivativeFiniteDifferenceApproximation = (valueUpShift-value)/shift;

		// Analytic solution (known in this case)
		double derivativeAnalytic = Math.exp(x);

		// Absolute error
		double error = derivativeFiniteDifferenceApproximation - derivativeAnalytic;

		System.out.println("h = " + String.format("%-10.3e", shift) + "  \t  finite difference approx \u2202f/\u2202x \u2248 \u0394f/\u0394h = " + String.format("%5.3f", derivativeFiniteDifferenceApproximation) + "\t error: " + error);
		
	}

	private static void plotForwardFiniteDifferenceApproximationErrorOfExp(double x, double scaleMin, double scaleMax) throws IOException {

		DoubleUnaryOperator finiteDifferenceApproxError = scale -> {

			// Shift as a function of the scale
			double shift = Math.pow(10, scale);

			// Finite difference approximation
			double valueUpShift = Math.exp(x + shift);
			double value = Math.exp(x);

			double derivativeFiniteDifferenceApproximation = (valueUpShift-value)/shift;

			// Analytic solution (known in this case)
			double derivativeAnalytic = Math.exp(x);

			// Absolute error
			double error = derivativeFiniteDifferenceApproximation - derivativeAnalytic;

			return error;
		};

		Plot2D plot = new Plot2D(scaleMin, scaleMax, 1024, List.of(
				new Named<DoubleUnaryOperator>("Finite Difference Appoximation", finiteDifferenceApproxError),
				new Named<DoubleUnaryOperator>("Analytic", t -> 0.0)));
		plot.setTitle("(One Sided Finite Difference) Derivative of exp(x) at x = " + x)
		.setXAxisLabel("scale = log\u2081\u2080(h)  (h = 10^{scale})")
		.setYAxisLabel("error")
		.setYAxisNumberFormat(new DecimalFormat("0.0E00"))
		.show();
		plot.saveAsPDF(new File("images/exp-x-forward-fd-("+(int)(scaleMin*10)+","+(int)(scaleMax*10)+").pdf"), 800, 500);
//		.saveAsPNG(new File("images/exp-x-forward-fd-("+(int)(scaleMin*10)+","+(int)(scaleMax*10)+").png"), 800, 500)
	}

	private static void plotCentralFiniteDifferenceApproximationErrorOfExp(double x, double scaleMin, double scaleMax) {

		DoubleUnaryOperator finiteDifferenceApproxError = scale -> {

			double shift = Math.pow(10, scale);

			double valueUpShift = Math.exp(x + shift);
			double valueDnShift = Math.exp(x - shift);
			double finiteDifferenceApproximation = (valueUpShift-valueDnShift)/shift/2.0;

			double derivativeAnalytic = Math.exp(x);

			double error = finiteDifferenceApproximation - derivativeAnalytic;

			return error;
		};

		Plot2D plot = new Plot2D(scaleMin, scaleMax, 1024, List.of(
				new Named<>("Finite Difference Appoximation", finiteDifferenceApproxError),
				new Named<DoubleUnaryOperator>("Analytic", t -> 0.0)));
		plot.setYAxisNumberFormat(new DecimalFormat("0.0"))
		.setTitle("(Central Finite Difference) Derivative of exp(x) at x = " + x)
		.setXAxisLabel("scale = log\u2081\u2080(h)  (h = 10^{scale})")
		.setYAxisLabel("error")
		.setYAxisNumberFormat(new DecimalFormat("0.0E00"))
//		.saveAsPNG(new File("images/exp-x-centered-fd-("+(int)(scaleMin*10)+","+(int)(scaleMax*10)+").png"), 800, 500)
		.show();
	}

	private static void plotSecondOrderFiniteDifferenceApproximationErrorOfCos(double x, double scaleMin, double scaleMax) throws IOException {

		DoubleUnaryOperator finiteDifferenceApproxError = scale -> {

			// Shift as a function of the scale
			double shift = Math.pow(10, scale);

			// Finite difference approximation of second derivative
			double valueUpShift = Math.cos(x + shift);
			double value = Math.cos(x);
			double valueDnShift = Math.cos(x - shift);

			double derivativeFiniteDifferenceApproximation = (valueUpShift-2*value+valueDnShift)/shift/shift;

			// Analytic solution (known in this case)
			double derivativeAnalytic = -Math.cos(x);

			// Absolute error
			double error = derivativeFiniteDifferenceApproximation - derivativeAnalytic;

			return error;
		};

		Plot2D plot = new Plot2D(scaleMin, scaleMax, 1024, List.of(
				new Named<>("Finite Difference Appoximation", finiteDifferenceApproxError),
				new Named<DoubleUnaryOperator>("Analytic", t -> 0.0)));
		plot.setTitle("Second Order (Finite Difference) Derivative of cos(x) at x = " + x)
		.setXAxisLabel("scale = log\u2081\u2080(h)  (h = 10^{scale})")
		.setYAxisLabel("error")
		.setYAxisNumberFormat(new DecimalFormat("0.0E00"))
		.show();
		plot.saveAsPDF(new File("images/exp-x-forward-fd-("+(int)(scaleMin*10)+","+(int)(scaleMax*10)+").pdf"), 800, 500);
//		.saveAsPNG(new File("images/exp-x-forward-fd-("+(int)(scaleMin*10)+","+(int)(scaleMax*10)+").png"), 800, 500)
	}

	private static void plotThirdOrderFiniteDifferenceApproximationErrorOfExp(double x, double scaleMin, double scaleMax) {

		DoubleUnaryOperator finiteDifferenceApproxError = scale -> {

			double shift = Math.pow(10, scale);

			double value2UShift = Math.exp(x + 2* shift);
			double value2DShift = Math.exp(x - 2* shift);
			double value1UShift = Math.exp(x + shift);
			double value1DShift = Math.exp(x - shift);
			double finiteDifferenceApproximation = (value2UShift - 2*value1UShift + 2*value1DShift - value2DShift)/shift/shift/shift;

			double derivativeAnalytic = Math.exp(x);

			double error = finiteDifferenceApproximation - derivativeAnalytic - 1;

			return error;
		};

		Plot2D plot = new Plot2D(scaleMin, scaleMax, 1024, List.of(
				new Named<>("Finite Difference Appoximation", finiteDifferenceApproxError),
				new Named<DoubleUnaryOperator>("Analytic", t -> 0.0)));
		plot.setYAxisNumberFormat(new DecimalFormat("0.0"))
		.setTitle("(Central Finite Difference) 3rd Order Derivative of exp(x) at x = " + x)
		.setXAxisLabel("scale = log\u2081\u2080(h)  (h = 10^{scale})")
		.setYAxisLabel("error")
		.setYAxisNumberFormat(new DecimalFormat("0.0E00"))
//		.saveAsPNG(new File("images/exp-x-centered-fd-("+(int)(scaleMin*10)+","+(int)(scaleMax*10)+").png"), 800, 500)
		.show();
	}
}
