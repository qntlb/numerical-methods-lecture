/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 21 May 2018
 */
package info.quantlab.numericalmethods.lecture.randomnumbers;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implements a multi-dimensional Halton sequence (quasi random numbers) with the given bases.
 *
 * @author Christian Fries
 * @version 1.0
 */
public class HaltonSequence implements RandomNumberGenerator {

	private static final long serialVersionUID = -4799340450248196350L;

	private final int[] base;

	private final AtomicLong currentIndex = new AtomicLong();

	/**
	 * Constructs a Halton sequence with the given bases.
	 *
	 * The bases should be integers without common divisor greater than 1, for example, prime numbers.
	 *
	 * @param base The array of base integers. The length of the array defines the dimension of the sequence.
	 */
	public HaltonSequence(final int[] base) {
		for(int i=0; i<base.length; i++) {
			if(base[i] <= 1) {
				throw new IllegalArgumentException("base needs to be larger than 1");
			}
		}

		this.base = base;
	}

	@Override
	public double[] getNext() {
		return getHaltonNumber(currentIndex.getAndIncrement());
	}

	@Override
	public int getDimension() {
		return base.length;
	}

	public double[] getHaltonNumber(final long index) {
		final double[] x = new double[base.length];
		for(int dimension = 0; dimension<base.length; dimension++) {
			x[dimension] = VanDerCorputSequence.getVanDerCorputNumber(index, base[dimension]);
		}
		return x;
	}

	public static double getHaltonNumberForGivenBase(long index, int base) {
		return VanDerCorputSequence.getVanDerCorputNumber(index, base);
	}

	@Override
	public String toString() {
		return "HaltonSequence [base=" + Arrays.toString(base) + ", currentIndex=" + currentIndex + "]";
	}
}
