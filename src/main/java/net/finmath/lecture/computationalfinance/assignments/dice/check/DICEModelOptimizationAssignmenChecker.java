package net.finmath.lecture.computationalfinance.assignments.dice.check;

import net.finmath.lecture.computationalfinance.assignments.dice.DICEModelOptimizationAssignment;

public class DICEModelOptimizationAssignmenChecker
{
	public static boolean check(DICEModelOptimizationAssignment solution) {

		System.out.println("Note: This is just a very rudimentary test.");
		System.out.println("If this test passes, it is not a guarantee that the calibration found the optimal solution (sorry).");

		try {
			for(double discountRate : new double[] { 0.005, 0.01, 0.015 }) {
				double[] abatement = solution.getDICEModelOptimalAbatementPath(discountRate);

				/*
				 * Rudimentary check: check that abatement vector is monotone
				 */
				for(int i=1; i<abatement.length; i++) {
					if(abatement[i] < abatement[i-1]) {
						System.out.println("\tAbatement vector is not monotone increasing.");
						return false;
					}
				}

				/*
				 * Rudimentary check: check that abatement vector is >= 0
				 */
				for (double element : abatement) {
					if(element < 0) {
						System.out.println("\tAbatement vector is negative.");
						return false;
					}
				}

				/*
				 * Rudimentary check: check that abatement is not all zero
				 */
				boolean isAbatementAllZero = true;
				for (double element : abatement) {
					isAbatementAllZero &= (element == 0);
				}
				if(isAbatementAllZero) {
					System.out.println("\tAbatement vector is all zero, this is likly not the optimal soluation.");
					return false;
				}
			}
		}
		catch(Exception e) {
			System.out.println("\tThe calibration throwed an Exception:");
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
