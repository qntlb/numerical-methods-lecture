package info.quantlab.numericalmethods.lecture.randomnumbers;

public class HaltonSequence {

	private int[] base;

	public HaltonSequence(int[] base) {
		super();
		this.base = base;
	}

	public double[] getSamplePoint(int index) {
		double[] x = new double[base.length];
		for(int i = 0; i<x.length; i++) {
			x[i] = VanDerCorputSequence.getVanDerCorputNumber(index, base[i]);
		}
		return x;
	}
}
