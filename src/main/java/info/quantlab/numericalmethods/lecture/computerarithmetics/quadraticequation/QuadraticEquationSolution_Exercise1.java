package info.quantlab.numericalmethods.lecture.computerarithmetics.quadraticequation;

public class QuadraticEquationSolution implements QuadraticEquation {

	private final double q; // Konstanten-Koeffizient
	private final double p; // Linearer Koeffizient

	public QuadraticEquationSolution(double q, double p) {
		this.q = q;
		this.p = p;
	}

	@Override
	public double[] getCoefficients() {
		return new double[] { q, p }; // Array mit den Koeffizienten q und p zurückgeben
	}

	@Override
	public boolean hasRealRoot() {
		double discriminant = p * p - 4 * q;
		return discriminant >= 0; // Rückgabe, ob eine reale Wurzel existiert
	}

	@Override
	public double getSmallestRoot() {
		double discriminant = p * p - 4 * q;

		if (discriminant < 0) {
			return Double.NaN; // Keine reale Wurzel, NaN zurückgeben
		} else if (discriminant == 0) {
			return -p / 2; // Eine reale Wurzel, die Wurzel berechnen und zurückgeben
		} else {
			double sqrtDiscriminant = Math.sqrt(discriminant);
			double r1 = (-p - sqrtDiscriminant) / 2;
			double r2 = (-p + sqrtDiscriminant) / 2;
			return Math.min(r1, r2); // Die kleinere Wurzel zurückgeben
		}
	}
}
