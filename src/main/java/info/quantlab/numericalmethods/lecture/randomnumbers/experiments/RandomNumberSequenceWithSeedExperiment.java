package info.quantlab.numericalmethods.lecture.randomnumbers.experiments;

import java.util.Random;

public class RandomNumberSequenceWithSeedExperiment {

	public static void main(String[] args) {
		
		long seed = 3141;
		int numberOfSamplePoints = 30;
		
		System.out.println("First " + numberOfSamplePoints + " elements of the sequence with seed " + seed + ":");
		System.out.println("_".repeat(79));

		// Create random number generator
		Random random = new Random(seed);
		
		// Print elements of the sequence
		for(int i=0; i<numberOfSamplePoints ; i++) {
			float randomNumber = random.nextFloat();
			System.out.println("\t" + i + "\t" + randomNumber);
		}
	}
}
