package info.quantlab.numericalmethods.lecture.montecarlo;

import java.util.Random;

import info.quantlab.numericalmethods.lecture.randomnumbers.VanDerCorputSequence;

/**
 * Creates a sequence of vectors with uniform on [0,1) distributed components.
 * 
 * The main method plots some points of a 2 dimensional vector.
 * 
 * @author Christian Fries
 */
public class RandomVectorQuasiWrong {

	// Our memory for the d-Dimensional sequence indexed as [sampleIndex][componentIndex]
	// Alternative would be : [componentIndex][sampleIndex]
	private double[][] randomVectorSequence;
	
	public RandomVectorQuasiWrong(int length, int dimension) {
		
		randomVectorSequence = new double[length][dimension];
		
		for(int i=0; i<length; i++) {
			for(int j=0; j<dimension; j++) {
				// Populate the vector y_{i,j} = x_{i*d+j}
				int index = i*dimension+j;
				randomVectorSequence[i][j] = VanDerCorputSequence.getVanDerCorputNumber(index, 2);
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
