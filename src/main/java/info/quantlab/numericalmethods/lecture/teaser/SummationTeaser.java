package info.quantlab.numericalmethods.lecture.teaser;

public class SummationTeaser {

	public static void main(String[] args) {
		
		long numberOfValues = 10000000000L;
		double value = 0.1;
		
		double sum = 0.0;
		for(long i=0; i<numberOfValues; i++) {
			sum = sum + value;
		}
		double average = sum / numberOfValues;
		
		System.out.println("sum...........: " + sum);
		System.out.println("average.......: " + average);
	}

}
