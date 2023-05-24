package info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.check;

import java.time.LocalDate;

import info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.ProductFactory;
import net.finmath.marketdata.products.AnalyticProduct;
import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.time.RegularSchedule;
import net.finmath.time.Schedule;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class ProductFactoryImplementation implements ProductFactory {

	private final LocalDate referenceDate;
	private final Double periodLength;

	public ProductFactoryImplementation(LocalDate referenceDate, Double periodLength) {
		super();
		this.referenceDate = referenceDate;
		this.periodLength = periodLength;
	}

	public ProductFactoryImplementation() {
		this(LocalDate.now(), 0.5);
	}

	@Override
	public AnalyticProduct getSwap(double maturity, double rateFix, String forwardCurveName, String discountCurveName) {

		int numberOfPeriods = (int) Math.round(maturity/periodLength);

		TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(0.0, numberOfPeriods, periodLength);
		Schedule legSchedule = new RegularSchedule(timeDiscretization);

		AnalyticProduct swapLegFloat = new SwapLeg(legSchedule, forwardCurveName, 0.0, discountCurveName);
		AnalyticProduct swapLegFix = new SwapLeg(legSchedule, null, rateFix, discountCurveName);

		AnalyticProduct swap = new Swap(swapLegFloat, swapLegFix);

		return swap;
	}
}
