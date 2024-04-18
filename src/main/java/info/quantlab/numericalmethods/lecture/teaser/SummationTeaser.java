package info.quantlab.numericalmethods.lecture.teaser;

public class SummationTeaser {

	public static void main(String[] args) {
		
		int numberOfValues = 10000000;
		double value = 0.1;
		
		double sum = 0.0;
		for(int i=0; i<numberOfValues; i++) {
			sum = sum + value;
		}
		double average = sum / numberOfValues;
		
		System.out.println("average.......: " + average);
	}

}
