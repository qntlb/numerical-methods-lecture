package info.quantlab.numericalmethods.lecture.computerarithmetics.quadraticequation;

public interface QuadraticEquationFactory {

	/**
	 * Factory method creating an instance of an object
	 * implementing QuadraticEquation with coefficients p
	 * and q, representing the equation x^2 + p x + q.
	 *
	 * @param q The constant coefficient q in x^2 + p x + q.
	 * @param p The linear coefficient p in x^2 + p x + q.
	 * @return The object implementing QuadraticEquation.
	 */
	QuadraticEquation createQuadraticEquation(double q, double p);
}
