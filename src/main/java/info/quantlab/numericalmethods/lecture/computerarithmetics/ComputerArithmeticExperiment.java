package info.quantlab.numericalmethods.lecture.computerarithmetics;

public class ComputerArithmeticExperiment {

	public static void main(String[] args) {
		
		/*
		 * Explore Integer
		 */

		int i = Integer.MAX_VALUE;
		int j = Integer.MIN_VALUE;
		
		int iPlusOne = i+1;
		
		System.out.println("i     = " + i);
		System.out.println("i+1   = " + iPlusOne);
		
		System.out.println("i > i+1 is " + (i > i+1));

		System.out.println();

		/*
		 * Explore Double Part 1
		 */

		double tiny = 1.0;
		
		while(tiny/2 > 0) {
			tiny = tiny/2.0;
		}
		
		System.out.println("tiny       = " + tiny);
		System.out.println("tiny/2     = " + tiny/2);
		System.out.println("tiny/2*2   = " + (tiny/2)*2);
		
		System.out.println();

		/*
		 * Explore Double Part 2
		 */
		
		double eps = 1.0;
		while(1 + eps > 1) {
			eps = eps / 2.0;
		}
		System.out.println("eps ....... = " + eps);
		System.out.println("1+eps ..... = " + (1+eps));
		System.out.println("1+eps==1...is " + ((1+eps)==1));
		System.out.println("1+2*eps ... = " + (1+2*eps));
		
	}

}
