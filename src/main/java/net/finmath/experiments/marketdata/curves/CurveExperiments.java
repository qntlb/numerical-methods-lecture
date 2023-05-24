package net.finmath.experiments.marketdata.curves;

import java.time.LocalDate;
import java.util.function.DoubleUnaryOperator;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.AnalyticModelFromCurvesAndVols;
import net.finmath.marketdata.model.curves.Curve;
import net.finmath.marketdata.model.curves.CurveInterpolation;
import net.finmath.marketdata.model.curves.CurveInterpolation.ExtrapolationMethod;
import net.finmath.marketdata.model.curves.CurveInterpolation.InterpolationEntity;
import net.finmath.marketdata.model.curves.CurveInterpolation.InterpolationMethod;
import net.finmath.marketdata.model.curves.DiscountCurve;
import net.finmath.marketdata.model.curves.DiscountCurveInterpolation;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.marketdata.model.curves.ForwardCurveFromDiscountCurve;
import net.finmath.marketdata.products.AnalyticProduct;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.plots.Plot2D;
import net.finmath.time.RegularSchedule;
import net.finmath.time.Schedule;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class CurveExperiments {

	public static void main(String[] args) {

//		testCurve();

		testSwapLeg();

	}

	private static void testSwapLeg() {

		LocalDate referenceDate = LocalDate.of(2022, 11, 23);

		TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(0.0, 10, 0.5);	// 0.0, 0.5, 1.0, ..., 5.0;
		Schedule schedule = new RegularSchedule(timeDiscretization);
		double maturity = schedule.getPayment(schedule.getNumberOfPeriods()-1);


		DiscountCurve discountCurve = DiscountCurveInterpolation.createDiscountCurveFromZeroRates("EURSTR", referenceDate,
				new double[] { 1.0, 2.0, maturity, 5.5, 6.0, 6.5 },
				new double[] { 0.05, 0.05, 0.05,   0.01, 0.05, 0.05 },
				InterpolationMethod.CUBIC_SPLINE, ExtrapolationMethod.LINEAR, InterpolationEntity.LOG_OF_VALUE);

		ForwardCurve forwardCurve = new ForwardCurveFromDiscountCurve(discountCurve.getName(), referenceDate, null);

		AnalyticModel model = new AnalyticModelFromCurvesAndVols(new Curve[] { discountCurve, forwardCurve});


		AnalyticProduct swapLegFloat = new SwapLeg(schedule, forwardCurve.getName(), 0.0, discountCurve.getName());

		double value = swapLegFloat.getValue(0.0, model);
		double valueCheck = discountCurve.getValue(0.0) - discountCurve.getValue(maturity);

		System.out.println("Float Leg");
		System.out.println("value = " + value);
		System.out.println("value = " + valueCheck);

		AnalyticProduct swapLegFix = new SwapLeg(schedule, null, 0.05, discountCurve.getName());
		double valueFix = swapLegFix.getValue(0.0, model);
		System.out.println("Fixed Leg");
		System.out.println("value = " + valueFix);
	}

	private static void testCurve() {
		Curve curve = new CurveInterpolation("curve", null,
				InterpolationMethod.LINEAR,
				ExtrapolationMethod.LINEAR,
				InterpolationEntity.LOG_OF_VALUE,
				new double[] { 0.0, 1.0, 2.0, 5.0, 10.0 },
				new double[] { 1.0, 0.9, 0.8, 0.6, 0.4 });

		plotCurve(curve);

	}

	private static void plotCurve(Curve curve) {
		DoubleUnaryOperator interpolation = x -> curve.getValue(x);

		(new Plot2D(0.0, 15.0, interpolation)).show();
	}

}
