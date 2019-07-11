package org.matsim.parkingProxy.penaltyCalculator;

public interface PenaltyFunction {
	
	public double calculatePenalty(int numberOfCars);
}
