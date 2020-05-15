package info.quantlab.numericalmethods.lecture.randomnumbers;

public class VanDerCorputSequence {

	public static void main(String[] args) {
		for(int i=0; i<30; i++) {
			double x = getVanDerCorputNumber(i, 2);
			System.out.println(i + "\t" + x);
		}
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
