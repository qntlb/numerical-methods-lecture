package info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.check;

import java.time.LocalDate;
import java.util.Arrays;

import info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.ModelFactory;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.AnalyticModelFromCurvesAndVols;
import net.finmath.marketdata.model.curves.Curve;
import net.finmath.marketdata.model.curves.DiscountCurve;
import net.finmath.marketdata.model.curves.DiscountCurveInterpolation;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.marketdata.model.curves.ForwardCurveFromDiscountCurve;
import net.finmath.marketdata.model.curves.CurveInterpolation.ExtrapolationMethod;
import net.finmath.marketdata.model.curves.CurveInterpolation.InterpolationEntity;
import net.finmath.marketdata.model.curves.CurveInterpolation.InterpolationMethod;

public class ModelFactoryImplementation implements ModelFactory {

	private final LocalDate referenceDate;

	private final InterpolationMethod interpolationMethod;
	private final ExtrapolationMethod extrapolationMethod;
	private final InterpolationEntity interpolationEntity;

	public ModelFactoryImplementation(InterpolationMethod interpolationMethod, ExtrapolationMethod extrapolationMethod,
			InterpolationEntity interpolationEntity) {
		super();
		this.referenceDate = LocalDate.now();
		this.interpolationMethod = interpolationMethod;
		this.extrapolationMethod = extrapolationMethod;
		this.interpolationEntity = interpolationEntity;
	}
	
	public ModelFactoryImplementation() {
		this(InterpolationMethod.LINEAR, ExtrapolationMethod.LINEAR, InterpolationEntity.LOG_OF_VALUE);
	}

	/**
	 * Create a model with a discount curve and a forward curve using the given zero rates in the discount curve.
	 * 
	 * @param maturities The maturities.
	 * @param zeroRates The zero rates.
	 * @param discountCurveName The discount curve name.
	 * @param forwardCurveName The forward curve name.
	 * @return A model providing the two curves with the given zero rates.
	 */
	@Override
	public AnalyticModel getModel(double[] maturities, double[] zeroRates, String discountCurveName, String forwardCurveName) {
		return getModelWithShift(maturities, zeroRates, discountCurveName, forwardCurveName, 0, 0.0);
	}

	/**
	 * Create a model with a discount curve and a forward curve using a flat zero rate curve and a shift in one bucket.
	 * 
	 * @param maturities The maturities.
	 * @param zeroRates The zero rates.
	 * @param discountCurveName The discount curve name.
	 * @param forwardCurveName The forward curve name.
	 * @param bucket The bucket to shift.
	 * @param shift A shift to be applied to the zero rate.
	 * @return A model providing the two curves with a zero rate of zeroRate for all but the shifted bucket.
	 */
	@Override
	public AnalyticModel getModelWithShift(double[] maturities, double[] zeroRates, String discountCurveName, String forwardCurveName, int bucket, double shift) {

		// Shift bucket in zero rates
		double[] zeroRatesShifted = Arrays.copyOf(zeroRates, zeroRates.length);
		zeroRatesShifted[bucket] += shift;

		// Get discount factors
		double[] discountFactors = getDiscountFactorsFromForwardZeroRates(maturities, zeroRatesShifted);

		// Create discount curve
		DiscountCurve discountCurve = DiscountCurveInterpolation.createDiscountCurveFromDiscountFactors(discountCurveName, referenceDate,
				maturities,
				discountFactors,
				null,
				interpolationMethod, extrapolationMethod, interpolationEntity);

		ForwardCurve forwardCurve = new ForwardCurveFromDiscountCurve(forwardCurveName, discountCurve.getName(), referenceDate, null);

		AnalyticModel model = new AnalyticModelFromCurvesAndVols(new Curve[] { discountCurve, forwardCurve });
		
		return model;
	}

	private double[] getDiscountFactorsFromForwardZeroRates(double[] maturities, double[] zeroRatesShifted) {

		double[] discountFactors = new double[maturities.length];

		double discountFactor = 1.0;
		double maturityPrevious = 0.0;
		for(int i=0; i<maturities.length; i++) {
			double maturity = maturities[i];
			discountFactor *= Math.exp(- zeroRatesShifted[i] * (maturity-maturityPrevious));
			discountFactors[i] = discountFactor;
			maturityPrevious = maturity;
		}

		return discountFactors;
	}
}
