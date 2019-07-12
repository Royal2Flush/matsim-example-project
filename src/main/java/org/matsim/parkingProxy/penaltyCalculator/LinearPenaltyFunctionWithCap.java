package org.matsim.parkingProxy.penaltyCalculator;

public class LinearPenaltyFunctionWithCap implements PenaltyFunction {
	
	double penaltyPerCar;
	double maxPenalty;
	
	public LinearPenaltyFunctionWithCap(double penaltyPerCar, double maxPenalty) {
		this.penaltyPerCar = penaltyPerCar;
		this.maxPenalty = maxPenalty;
	}

	@Override
	public double calculatePenalty(int numberOfCars) {
		return Math.min(numberOfCars * penaltyPerCar, maxPenalty);
	}

}
