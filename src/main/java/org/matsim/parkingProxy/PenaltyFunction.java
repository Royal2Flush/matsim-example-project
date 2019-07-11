package org.matsim.parkingProxy;

public interface PenaltyFunction {
	
	public double calculatePenalty(int numberOfCars);
}
