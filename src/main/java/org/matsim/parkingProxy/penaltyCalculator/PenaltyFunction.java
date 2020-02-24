package org.matsim.parkingProxy.penaltyCalculator;

/**
 * Interface to calculate the penalty a car driver receives depending of the number of cars already there.
 * 
 * @author tkohl / Senozon
 *
 */
public interface PenaltyFunction {
	
	public double calculatePenalty(int numberOfCars);
}
