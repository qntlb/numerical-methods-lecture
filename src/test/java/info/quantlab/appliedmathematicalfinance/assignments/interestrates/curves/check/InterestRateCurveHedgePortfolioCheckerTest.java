package info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.check;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.InterestRateCurveHedgePortfolio;
import info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.InterestRateCurveHedgePortfolioAssignment;
import info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.ModelFactory;
import info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.ProductFactory;
import info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.check.InterestRateCurveHedgePortfolioChecker.Check;
import net.finmath.functions.LinearAlgebra;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.curves.DiscountCurve;
import net.finmath.marketdata.products.AnalyticProduct;
import net.finmath.modelling.Model;

/**
 * Exercises the public grading entry point with correct and defective submissions.
 */
public class InterestRateCurveHedgePortfolioCheckerTest {

	@Test
	void acceptsGenericFiniteDifferenceSolution() {
		assertTrue(check(new ReferenceAssignment(Behavior.CORRECT)));
	}

	@Test
	void acceptsMinimumResidualSolutionWithoutRequiringMinimumNorm() {
		final ReferenceAssignment assignment = new ReferenceAssignment(Behavior.ALTERNATIVE_MINIMUM_RESIDUAL);
		assertTrue(check(assignment));
		assertTrue(assignment.alternativeWeightsUsed, "The underdetermined scenario must exercise the alternative solution.");
	}

	@Test
	void rejectsOriginalSquareOnlyInstrumentCount() {
		final String diagnostics = getFailureDiagnostics(Behavior.SQUARE_ONLY);
		for(final String expected : List.of("3 buckets, 2 instruments", "3 rows", "2 columns", "2 weights",
				"portfolioInstruments.size()", "Caused by: java.lang.IndexOutOfBoundsException")) {
			assertTrue(diagnostics.contains(expected), "Missing diagnostic '" + expected + "':\n" + diagnostics);
		}
	}

	@Test
	void reportsNullPointerFailureWithoutDimensionHint() {
		assertFailureWithoutDimensionHint(Behavior.NULL_POINTER_EXCEPTION,
				"Caused by: java.lang.NullPointerException: Missing valuation input");
	}

	@Test
	void reportsIllegalArgumentFailureWithoutDimensionHint() {
		assertFailureWithoutDimensionHint(Behavior.ILLEGAL_ARGUMENT_EXCEPTION,
				"Caused by: java.lang.IllegalArgumentException: Unsupported valuation option");
	}

	@Test
	void rejectsIgnoringExtraRequiredInstrumentEvenWithCorrectOutputLength() {
		assertFalse(check(new ReferenceAssignment(Behavior.IGNORE_EXTRA_INSTRUMENTS)));
	}

	@Test
	void rejectsNonFiniteRectangularWeights() {
		for(final Behavior behavior : List.of(Behavior.NAN_WEIGHTS, Behavior.INFINITE_WEIGHTS)) {
			assertFalse(check(new ReferenceAssignment(behavior)), behavior.toString());
		}
	}

	@Test
	void rejectsWrongNumberOfRectangularWeights() {
		assertFalse(check(new ReferenceAssignment(Behavior.WRONG_LENGTH)));
	}

	@Test
	void rejectsFiniteButIncorrectRectangularWeights() {
		assertFalse(check(new ReferenceAssignment(Behavior.ZERO_WEIGHTS)));
	}

	@Test
	void rejectsWeightsReturnedInTheWrongInstrumentOrder() {
		assertFalse(check(new ReferenceAssignment(Behavior.REORDERED_WEIGHTS)));
	}

	private static boolean check(ReferenceAssignment assignment) {
		return InterestRateCurveHedgePortfolioChecker.check(assignment, Check.SENSITIVITIES);
	}

	private static String getFailureDiagnostics(Behavior behavior) {
		final PrintStream originalOut = System.out;
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try(PrintStream capturedOut = new PrintStream(output, true, StandardCharsets.UTF_8)) {
			System.setOut(capturedOut);
			assertFalse(check(new ReferenceAssignment(behavior)));
		}
		finally {
			System.setOut(originalOut);
		}
		return output.toString(StandardCharsets.UTF_8);
	}

	private static void assertFailureWithoutDimensionHint(Behavior behavior, String originalError) {
		final String diagnostics = getFailureDiagnostics(behavior);
		assertTrue(diagnostics.contains("3 buckets, 2 instruments"), diagnostics);
		assertTrue(diagnostics.contains(originalError), diagnostics);
		for(final String hint : List.of("portfolioInstruments.size()", "rows", "columns")) {
			assertFalse(diagnostics.contains(hint), "Unexpected dimension hint '" + hint + "':\n" + diagnostics);
		}
	}

	private enum Behavior {
		CORRECT,
		ALTERNATIVE_MINIMUM_RESIDUAL,
		SQUARE_ONLY,
		IGNORE_EXTRA_INSTRUMENTS,
		NAN_WEIGHTS,
		INFINITE_WEIGHTS,
		WRONG_LENGTH,
		ZERO_WEIGHTS,
		REORDERED_WEIGHTS,
		NULL_POINTER_EXCEPTION,
		ILLEGAL_ARGUMENT_EXCEPTION
	}

	private static final class ReferenceAssignment implements InterestRateCurveHedgePortfolioAssignment {

		private final Behavior behavior;
		private boolean alternativeWeightsUsed;

		private ReferenceAssignment(Behavior behavior) {
			this.behavior = behavior;
		}

		@Override
		public ProductFactory getSwapFactory() {
			return ReferenceSwap::new;
		}

		@Override
		public InterestRateCurveHedgePortfolio getInterestRateCurveHedgePortfolio(ModelFactory modelFactory, ProductFactory swapFactory) {
			return new InterestRateCurveHedgePortfolio() {

				@Override
				public double getParRate(double periodLength, double maturity, String discountCurveName, AnalyticModel model) {
					final double valueAtZero = swapFactory.getSwap(periodLength, maturity, 0.0, true, discountCurveName).getValue(0.0, model);
					final double valueAtOne = swapFactory.getSwap(periodLength, maturity, 1.0, true, discountCurveName).getValue(0.0, model);
					return valueAtZero / (valueAtZero - valueAtOne);
				}

				@Override
				public double[] getReplicationPortfolio(List<AnalyticProduct> instruments, double[] maturities,
						double[] zeroRates, String discountCurveName, AnalyticProduct productToHedge) {
					final int numberOfBuckets = maturities.length;
					final int numberOfInstruments = instruments.size();
					final boolean rectangular = numberOfBuckets != numberOfInstruments;
					if(rectangular && behavior == Behavior.NULL_POINTER_EXCEPTION) {
						throw new NullPointerException("Missing valuation input");
					}
					if(rectangular && behavior == Behavior.ILLEGAL_ARGUMENT_EXCEPTION) {
						throw new IllegalArgumentException("Unsupported valuation option");
					}
					int instrumentsToUse = numberOfInstruments;
					if(behavior == Behavior.SQUARE_ONLY
							|| behavior == Behavior.IGNORE_EXTRA_INSTRUMENTS && numberOfBuckets < numberOfInstruments) {
						instrumentsToUse = numberOfBuckets;
					}

					final double shift = 0.001;
					final AnalyticModel model = modelFactory.getModel(maturities, zeroRates, discountCurveName);
					final double productValue = productToHedge.getValue(0.0, model);
					final double[] instrumentValues = new double[instrumentsToUse];
					for(int j=0; j<instrumentsToUse; j++) {
						instrumentValues[j] = instruments.get(j).getValue(0.0, model);
					}
					final double[][] sensitivities = new double[numberOfBuckets][instrumentsToUse];
					final double[] targetSensitivities = new double[numberOfBuckets];
					for(int i=0; i<numberOfBuckets; i++) {
						final AnalyticModel shiftedModel = modelFactory.getModelWithShift(maturities, zeroRates, discountCurveName, i, shift);
						targetSensitivities[i] = (productToHedge.getValue(0.0, shiftedModel) - productValue) / shift;
						for(int j=0; j<instrumentsToUse; j++) {
							sensitivities[i][j] = (instruments.get(j).getValue(0.0, shiftedModel) - instrumentValues[j]) / shift;
						}
					}
					final double[] weights = LinearAlgebra.solveLinearEquationSVD(sensitivities, targetSensitivities);

					if(behavior == Behavior.ALTERNATIVE_MINIMUM_RESIDUAL && numberOfBuckets < numberOfInstruments) {
						// The checker supplies two identical 1Y swaps before its required 2Y swap.
						// This preserves every sensitivity while increasing the coefficient norm.
						weights[0] += 0.25;
						weights[1] -= 0.25;
						alternativeWeightsUsed = true;
					}
					if(behavior == Behavior.IGNORE_EXTRA_INSTRUMENTS) {
						return Arrays.copyOf(weights, numberOfInstruments);
					}
					if(rectangular && behavior == Behavior.NAN_WEIGHTS) {
						weights[0] = Double.NaN;
					}
					if(rectangular && behavior == Behavior.INFINITE_WEIGHTS) {
						weights[0] = Double.POSITIVE_INFINITY;
					}
					if(rectangular && behavior == Behavior.WRONG_LENGTH) {
						return Arrays.copyOf(weights, numberOfInstruments + 1);
					}
					if(rectangular && behavior == Behavior.ZERO_WEIGHTS) {
						return new double[numberOfInstruments];
					}
					if(rectangular && behavior == Behavior.REORDERED_WEIGHTS) {
						final double firstWeight = weights[0];
						weights[0] = weights[weights.length - 1];
						weights[weights.length - 1] = firstWeight;
					}
					return weights;
				}
			};
		}
	}

	private static final class ReferenceSwap implements AnalyticProduct {

		private final double periodLength;
		private final int numberOfPeriods;
		private final double fixedRate;
		private final boolean isPayer;
		private final String discountCurveName;

		private ReferenceSwap(double periodLength, double maturity, double fixedRate, boolean isPayer, String discountCurveName) {
			this.periodLength = periodLength;
			this.numberOfPeriods = (int)Math.round(maturity / periodLength);
			this.fixedRate = fixedRate;
			this.isPayer = isPayer;
			this.discountCurveName = discountCurveName;
		}

		@Override
		public double getValue(double evaluationTime, AnalyticModel model) {
			final DiscountCurve curve = model.getDiscountCurve(discountCurveName);
			double value = 0.0;
			for(int i=0; i<numberOfPeriods; i++) {
				final double startDiscount = curve.getDiscountFactor(i * periodLength);
				final double endDiscount = curve.getDiscountFactor((i + 1) * periodLength);
				value += startDiscount - endDiscount - fixedRate * periodLength * endDiscount;
			}
			return isPayer ? value : -value;
		}

		@Override
		public Object getValue(double evaluationTime, Model model) {
			return getValue(evaluationTime, (AnalyticModel)model);
		}
	}
}
