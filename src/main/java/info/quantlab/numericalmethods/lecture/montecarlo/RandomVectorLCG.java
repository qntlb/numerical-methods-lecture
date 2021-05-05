package info.quantlab.numericalmethods.lecture.montecarlo;

import java.util.Random;

/**
 * Creates a sequence of vectors with uniform on [0,1) distributed components.
 *
 * The main method plots some points of a 2 dimensional vector.
 *
 * @author Christian Fries
 */
public class RandomVectorLCG {

	// Create a 1-D random number generator x_{k}
	private Random random = new Random(3141);

	// Our memory for the d-Dimensional sequence indexed as [sampleIndex][componentIndex]
	// Alternative would be : [componentIndex][sampleIndex]
	private double[][] randomVectorSequence;

	public RandomVectorLCG(int length, int dimension) {
		randomVectorSequence = new double[length][dimension];

		for(int i=0; i<length; i++) {
			for(int j=0; j<dimension; j++) {
				// Populate the vector y_{i,j} = x_{i*d+j}
				randomVectorSequence[i][j] = random.nextDouble();
			}
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
