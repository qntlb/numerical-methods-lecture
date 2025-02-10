package net.finmath.lecture.computationalfinance.assignments.dice;

import net.finmath.lecture.computationalfinance.assignments.dice.DICEModelOptimizationAssignment.AbatementParameterConstraint;
import net.finmath.optimizer.SolverException;

public interface DICEModelOptimizationAssignment {

	public enum AbatementParameterConstraint {
		NONE					/* none */,					
		BOUNDED					/* mu is bounded between 0.0 and 1.0 */,
		MONOTONE_AND_BOUNDED 	/* mu bounded and is monotone increasing */
	}

	/**
	 * Returns the vector of optimal abatement factors &mu;<sub>i</sub>,
	 * where &mu;<sub>i</sub> is the abatement factor for the time period
	 * t<sub>i</sub> to t<sub>i+1</sub>.
	 *
	 * @param abatementParameterConstraint A constraint on the abatement model.
	 * @param interestRate The discount rate.
	 * @return The vector of optimal abatement factors &mu;<sub>i</sub>.
	 */
	double[] getDICEModelOptimalAbatementPath(AbatementParameterConstraint abatementParameterConstraint, double interestRate);
}
