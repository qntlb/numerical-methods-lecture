package info.quantlab.numericalmethods.lecture.streams;

import java.util.function.DoubleSupplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import net.finmath.randomnumbers.MersenneTwister;

public class JavaStreamsExperiments {

	public static void main(String[] args) {

		/*
		 * Working with streams
		 */
		IntStream indices = IntStream.range(1,10);
		DoubleStream values = indices.mapToDouble(i -> 10.0*i + 5);
		values.forEach(x -> System.out.println(x));

		System.out.println("_".repeat(80) + "\n");

		/*
		 * A stream of 10 random numbers
		 */
		DoubleSupplier randomNumberGenerator = new MersenneTwister();
		DoubleStream randomNumbers = DoubleStream.generate(randomNumberGenerator).limit(10);
		randomNumbers.forEach(x -> System.out.println(x));

		System.out.println("_".repeat(80) + "\n");

		for(int i=0; i<10; i++) {
			double randomNumber = Math.random();
			System.out.println(randomNumber);
		}
	}
}
