package org.matsim.parkingProxy;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.controler.events.IterationStartsEvent;
import org.matsim.core.controler.listener.IterationStartsListener;
import org.matsim.parkingProxy.AccessEgressFinder.LegActPair;

public class ParkingCounterByPlans implements IterationStartsListener, PenaltyGenerator {
	
	public static final String CARMODE = "car";	
	
	private final MovingEntityCounter carCounter;
	private final int carWeight;
	private final AccessEgressFinder egressFinder = new AccessEgressFinder(CARMODE);
	
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
