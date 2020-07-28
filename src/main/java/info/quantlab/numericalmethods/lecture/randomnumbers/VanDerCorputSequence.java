package info.quantlab.numericalmethods.lecture.randomnumbers;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.DoubleSupplier;

public class VanDerCorputSequence implements DoubleSupplier {

	private final AtomicInteger index;
	private final int base;

	public static void main(String[] args) {
		for(int i=0; i<30; i++) {
			double x = getVanDerCorputNumber(i, 2);
			System.out.println(i + "\t" + x);
		}
	}

	public VanDerCorputSequence(int startIndex, int base) {
		super();
		this.index = new AtomicInteger(startIndex);
		this.base = base;
	}


	public VanDerCorputSequence(int base) {
		this(0, base);
	}

	@Override
	public double getAsDouble() {
		return getVanDerCorputNumber(index.getAndIncrement(), base);
	}

	/**
	 * @param index The index of the sequence starting with 0
	 * @param base The base.
	 * @return The van der Corput number
	 */
	public static double getVanDerCorputNumber(long index, int base) {

		index = index + 1;

		double x = 0.0;
		double refinementFactor = 1.0 / base;

		while(index > 0) {
			x += (index % base) * refinementFactor;
			index = index / base;
			refinementFactor = refinementFactor / base;
		}

		return x;
	}
}
