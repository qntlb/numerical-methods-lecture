package info.quantlab.numericalmethods.lecture.randomnumbers;

public class RandomNumberGeneratorFrom1D implements RandomNumberGenerator {

	private final RandomNumberGenerator1D randomNumberGenerator;
	private final int dimension;


	public RandomNumberGeneratorFrom1D(
			RandomNumberGenerator1D randomNumberGenerator,
			int dimension) {
		super();
		this.randomNumberGenerator = randomNumberGenerator;
		this.dimension = dimension;
	}

	@Override
	public double[] getNext() {
		double[] value = new double[dimension];
		for(int i=0; i<dimension; i++) {
			value[i] = randomNumberGenerator.getAsDouble();
		}
		return value;
	}

	@Override
	public int getDimension() {
		return dimension;
	}

	@Override
	public String toString() {
		return "RandomNumberGeneratorFrom1D [randomNumberGenerator=" + randomNumberGenerator + ", dimension="
				+ dimension + "]";
	}

}
