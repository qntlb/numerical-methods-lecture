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

	public static void main(String[] args) throws IOException {

		double x = 0.0;

		printForwardFiniteDifferenceApproximationOfExp(x, 0.01 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 0.001 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 0.0001 /* shift */);

		System.out.println();

		printForwardFiniteDifferenceApproximationOfExp(x, 1E-16 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1E-15 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1E-12 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 0.49 * Math.pow(2, -52) /* shift h */);
		printForwardFiniteDifferenceApproximationOfExp(x, 0.51*Math.pow(2, -52) /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1.00*Math.pow(2, -52) /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1.50*Math.pow(2, -52) /* shift */);

		plotForwardFiniteDifferenceApproximationErrorOfExp(x, -16.5, -14);
		
		plotForwardFiniteDifferenceApproximationErrorOfExp(x, -20, -1);
		plotForwardFiniteDifferenceApproximationErrorOfExp(x, -10, -6);

		plotCenteredFiniteDifferenceApproximationErrorOfExp(x, -16.5, -14);
		plotCenteredFiniteDifferenceApproximationErrorOfExp(x, -20, -1);
		plotCenteredFiniteDifferenceApproximationErrorOfExp(x, -7, -4);

		plotThirdOrderFiniteDifferenceApproximationErrorOfExp(x, -4.5, -1);
	}

	private static void printForwardFiniteDifferenceApproximationOfExp(double x, double shift) {

		double valueUpShift = Math.exp(x + shift);
		double value = Math.exp(x);
		
		double finiteDifferenceApproximation = (valueUpShift-value)/shift;
		
		double error = finiteDifferenceApproximation - Math.exp(x);
		
		System.out.println("h = " + String.format("%2.3e", shift) + "  \t  finite difference approx \u0394f / \u0394h = " + String.format("%5.3f", finiteDifferenceApproximation) + "\t error: " + error);
	}

	private static void plotForwardFiniteDifferenceApproximationErrorOfExp(double x, double scaleMin, double scaleMax) throws IOException {

		DoubleUnaryOperator finiteDifferenceApproxError = scale -> {

			double shift = Math.pow(10, scale);

			double valueUpShift = Math.exp(x + shift);
			double value = Math.exp(x);
			double finiteDifferenceApproximation = (valueUpShift-value)/shift;

			double derivativeAnalytic = Math.exp(x);

			double error = finiteDifferenceApproximation - derivativeAnalytic;

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

	private static void plotCenteredFiniteDifferenceApproximationErrorOfExp(double x, double scaleMin, double scaleMax) {

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
				new Named<DoubleUnaryOperator>("Finite Difference Appoximation", finiteDifferenceApproxError),
				new Named<DoubleUnaryOperator>("Analytic", t -> 0.0)));
		plot.setYAxisNumberFormat(new DecimalFormat("0.0"))
		.setTitle("(Centered Finite Difference) Derivative of exp(x) at x = " + x)
		.setXAxisLabel("scale = log\u2081\u2080(h)  (h = 10^{scale})")
		.setYAxisLabel("error")
		.setYAxisNumberFormat(new DecimalFormat("0.0E00"))
//		.saveAsPNG(new File("images/exp-x-centered-fd-("+(int)(scaleMin*10)+","+(int)(scaleMax*10)+").png"), 800, 500)
		.show();
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
				new Named<DoubleUnaryOperator>("Finite Difference Appoximation", finiteDifferenceApproxError),
				new Named<DoubleUnaryOperator>("Analytic", t -> 0.0)));
		plot.setYAxisNumberFormat(new DecimalFormat("0.0"))
		.setTitle("(Centered Finite Difference) 3rd Order Derivative of exp(x) at x = " + x)
		.setXAxisLabel("scale = log\u2081\u2080(h)  (h = 10^{scale})")
		.setYAxisLabel("error")
		.setYAxisNumberFormat(new DecimalFormat("0.0E00"))
//		.saveAsPNG(new File("images/exp-x-centered-fd-("+(int)(scaleMin*10)+","+(int)(scaleMax*10)+").png"), 800, 500)
		.show();
	}
}
