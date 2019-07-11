package org.matsim.parkingProxy.penaltyCalculator;

/**
 * A PenaltyGenerator acts as a Factory class for {@linkplain PenaltyCalculator}s. It does so
 * by taking any input either all at once or by collecting it over a longer time and on demand
 * builds a new instance of {@linkplain PenaltyCalculator} with the collected information.
 * 
 * @author tkohl / Senozon
 *
 */
public interface PenaltyGenerator {

	/**
	 * Generates an (almost) immutable {@linkplain PenaltyCalculator} based on the current state
	 * of this class. Further data collected by this class will not change the results of already
	 * generated {@linkplain PenaltyCalculators}. However, you may later plug in your own
	 * {@linkplain PenaltyFunction}.
	 * 
	 * @return the generated immutable calculator instance
	 */
	public PenaltyCalculator generatePenaltyCalculator();
	
	/**
	 * Resets the state of this class.
	 */
	public void reset();
}
