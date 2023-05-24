package net.finmath.lecture.computationalfinance.assignments.dice;

import net.finmath.optimizer.SolverException;

public interface DICEModelOptimizationAssignment {

	/**
	 * Returns the vector of optimal abatement factors &mu;<sub>i</sub>,
	 * where &mu;<sub>i</sub> is the abatement factor for the time period
	 * t<sub>i</sub> to t<sub>i+1</sub>.
	 *
	 * @param interestRate
	 * @return The vector of optimal abatement factors &mu;<sub>i</sub>.
	 * @throws SolverException
	 */
	double[] getDICEModelOptimalAbatementPath(double interestRate) throws SolverException;

}
