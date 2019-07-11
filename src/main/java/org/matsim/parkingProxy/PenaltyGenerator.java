package org.matsim.parkingProxy;

/**
 * A PenaltyGenerator acts as a Factory class for a {@linkplain PenaltyCalculator}. It does so
 * by taking any input either all at once or collecting it over a longer time and on demand
 * builds with the collected information a new instance of {@linkplain PenaltyCalculator}.
 * 
 * @author tkohl / Senozon
 *
 */
public interface PenaltyGenerator {

	/**
	 * Generates an immutable {@linkplain PenaltyCalculator} based on the current state of 
	 * this class. Further data collected by this class will not change the results of already
	 * generated {@linkplain PenaltyCalculators}.
	 * 
	 * @return the generated immutable calculator instance
	 */
	public PenaltyCalculator generatePenaltyCalculator();
	
	/**
	 * Resets the state of this class.
	 */
	public void reset();
}
