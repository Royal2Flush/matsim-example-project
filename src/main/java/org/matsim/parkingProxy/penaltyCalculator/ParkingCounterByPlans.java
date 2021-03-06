package org.matsim.parkingProxy.penaltyCalculator;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.controler.events.IterationStartsEvent;
import org.matsim.core.controler.listener.IterationStartsListener;
import org.matsim.parkingProxy.utils.AccessEgressFinder;
import org.matsim.parkingProxy.utils.AccessEgressFinder.LegActPair;

/**
 * <b>For some reason not working at the moment</b></br>
 * 
 * Tracks how many vehicles are in a given area at any time based on the selected Plans of the Persons
 * in the Population.</br>
 * 
 * The functionality of the {@linkplain PenaltyGenerator} interface are delegated to the {@linkplain MovingEntityCounter}
 * received in the constructor.
 * 
 * @author tkohl / Senozon
 *
 */
@Deprecated
public class ParkingCounterByPlans implements IterationStartsListener, PenaltyGenerator {
	
	public static final String CARMODE = "car";	
	
	private final MovingEntityCounter carCounter;
	private final int carWeight;
	private final AccessEgressFinder egressFinder = new AccessEgressFinder(CARMODE);
	
	/**
	 * 
	 * @param carCounter
	 * @param carWeight
	 */
	public ParkingCounterByPlans(MovingEntityCounter carCounter, int carWeight) {
		this.carCounter = carCounter;
		this.carWeight = carWeight;
	}

	@Override
	public PenaltyCalculator generatePenaltyCalculator() {
		return carCounter.generatePenaltyCalculator();
	}

	@Override
	public void reset() {
		carCounter.reset();
	}

	/**
	 * Iterates over all plans and calls the {@linkplain MovingEntityCounter#handleArrival(int, double, double, int)}
	 * and {@linkplain MovingEntityCounter#handleDeparture(int, double, double, int)} functions whenever an
	 * arrival or departure is detected. More precisely, the functions are called whenever an egress leg
	 * after a car leg starts and whenever an access leg followed by a car leg ends.
	 */
	@Override
	public void notifyIterationStarts(IterationStartsEvent event) {
		for (Person p : event.getServices().getScenario().getPopulation().getPersons().values()) {
			for (LegActPair walkActPair : this.egressFinder.findEgressWalks(p.getSelectedPlan())) {
				carCounter.handleArrival(
						(int) walkActPair.leg.getDepartureTime(),
						walkActPair.act.getCoord().getX(),
						walkActPair.act.getCoord().getY(),
						carWeight
						);
			}
			for (LegActPair walkActPair : this.egressFinder.findAccessWalks(p.getSelectedPlan())) {
				carCounter.handleDeparture(
						(int) (walkActPair.leg.getDepartureTime() + walkActPair.leg.getTravelTime()),
						walkActPair.act.getCoord().getX(),
						walkActPair.act.getCoord().getY(),
						carWeight
						);
			}
		}
	}

}
