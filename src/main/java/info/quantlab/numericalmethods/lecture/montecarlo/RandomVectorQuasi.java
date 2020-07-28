package info.quantlab.numericalmethods.lecture.montecarlo;

import info.quantlab.numericalmethods.lecture.randomnumbers.HaltonSequence;

/**
 * Creates a sequence of vectors with uniform on [0,1) distributed components.
 *
 * The main method plots some points of a 2 dimensional vector.
 *
 * @author Christian Fries
 */
public class RandomVectorQuasi {

	// Our memory for the d-Dimensional sequence indexed as [sampleIndex][componentIndex]
	// Alternative would be : [componentIndex][sampleIndex]
	private double[][] randomVectorSequence;

	public RandomVectorQuasi(int length, int dimension) {

		if(dimension != 2) throw new IllegalArgumentException("Currently we only support dim. = 2");

		HaltonSequence sequence = new HaltonSequence(new int[] { 2, 3 } );

		randomVectorSequence = new double[length][dimension];

		for(int i=0; i<length; i++) {
			randomVectorSequence[i] = sequence.getSamplePoint(i);
		}
	}


	double[][] getSequence() {
		return randomVectorSequence;
	}

	double[] getValue(int sampleIndex) {
		return randomVectorSequence[sampleIndex];
	}

	double getValueForComponent(int sampleIndex, int componentIndex) {
		return randomVectorSequence[sampleIndex][componentIndex];
	}

	double[] getSequenceForComponent(int componentIndex) {
		double[] componentSequence = new double[randomVectorSequence.length];
		for(int i=0; i<randomVectorSequence.length; i++) {
			componentSequence[i] = randomVectorSequence[i][componentIndex];
		}
		return componentSequence;
	}
}
