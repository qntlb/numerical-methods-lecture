package info.quantlab.numericalmethods.lecture.randomnumbers;

/**
 * Converts a uniform distributed sequence from a <code>RandomNumberGenerator1D</code>
 * to an exponential distributed sequence with parameter <code>lambda</code>.
 */
public class DefaultTimeExponentialDistribution {

	private final RandomNumberGenerator1D uniformSequence;
	private final double lambda;

	/**
	 * Create exponential distributed random number sequence.
	 * 
	 * @param uniformSequence The random number generator for a uniform distributed sequence.
	 * @param lambda The parameter lambda.
	 */
	public DefaultTimeExponentialDistribution(RandomNumberGenerator1D uniformSequence, double lambda) {
		super();
		this.uniformSequence = uniformSequence;
		this.lambda = lambda;
	}

	public double getNext() {
		
		double uniform = uniformSequence.getAsDouble();
		
		double time = - 1.0/lambda * Math.log(1-uniform);
		return time;
	}
}
