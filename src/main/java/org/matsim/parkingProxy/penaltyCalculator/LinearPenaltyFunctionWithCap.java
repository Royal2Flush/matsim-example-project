package org.matsim.parkingProxy.penaltyCalculator;

/**
 * Uses a linear raise in penalty time per car but stays between 0 and a maximum value. In mathematical terms this means: </br>
 * {@code P = [ 0 if c<0; r*c if 0<=c<=c_m; m if c>c_m},</br>
 * where P is the penalty in seconds, c is the number of cars, r is the additional penalty per car, m is the maximum 
 * penalty and c_m is the number of cars at which r*c=m.
 * 
 * @author tkohl / Senozon
 *
 */
public class LinearPenaltyFunctionWithCap implements PenaltyFunction {
	
	private final double penaltyPerCar;
	private final double maxPenalty;
	private final double areaFactor;
	
	public LinearPenaltyFunctionWithCap(double gridSize, double penaltyPerCar, double maxPenalty) {
		this.penaltyPerCar = penaltyPerCar;
		this.maxPenalty = maxPenalty;
		this.areaFactor = gridSize * gridSize / 2500.;
	}

	@Override
	public double calculatePenalty(int numberOfCars) {
		return Math.max(Math.min(numberOfCars * penaltyPerCar / areaFactor, maxPenalty), 0);
	}

}
