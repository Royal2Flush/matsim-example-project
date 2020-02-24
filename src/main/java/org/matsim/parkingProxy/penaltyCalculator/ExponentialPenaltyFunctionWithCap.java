package org.matsim.parkingProxy.penaltyCalculator;

/**
 * Uses n exponential raise in penalty time per car but stays between 0 and a maximum value. In mathematical terms this means: </br>
 * {@code P = [ 0 if c<0; m*b^(c/c_m - 1); m if c>c_m},</br>
 * where P is the penalty in seconds, c is the number of cars, r is the additional penalty per car, m is the maximum 
 * penalty and c_m is the number of cars at which r*c=m.
 * 
 * @author tkohl / Senozon
 *
 */
public class ExponentialPenaltyFunctionWithCap implements PenaltyFunction {
	
	private final double basis;
	private final double maxPenalty;
	private final int carsForMax;
	private final double areaFactor;
	
	public ExponentialPenaltyFunctionWithCap(double basis, double gridSize, double maxPenalty, int carsForMax) {
		this.basis = basis;
		this.maxPenalty = maxPenalty;
		this.carsForMax = carsForMax;
		this.areaFactor = gridSize * gridSize / 25000.;
	}

	@Override
	public double calculatePenalty(int numberOfCars) {
		return Math.max(Math.min(maxPenalty / basis * (Math.pow(basis, (double)numberOfCars / (double)carsForMax)) / areaFactor, maxPenalty), 0);
	}

}
