package org.matsim.class2019.eventHandler;

import java.util.HashMap;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;
import org.matsim.api.core.v01.population.Person;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;

public class TravelTimeEventHandler implements ActivityEndEventHandler, ActivityStartEventHandler {
	
	private final TObjectDoubleMap<Id<Person>> totalTravelTimes = new TObjectDoubleHashMap<Id<Person>>();
	private final Map<Id<Person>, ActivityEndEvent> agentsCommuting = new HashMap<Id<Person>, ActivityEndEvent>();
	
	public double getTotalTime() {
		double total = 0.;
		for (double v : totalTravelTimes.values()) {
			total += v;
		}
		return total;
	}

	@Override
	public void handleEvent(ActivityStartEvent event) {
		if (isInteraction(event.getActType())) return;
		ActivityEndEvent ev;
		double travelStartTime = 0;
		if ((ev = agentsCommuting.remove(event.getPersonId())) == null) {
			System.out.println("agent " + event.getPersonId().toString() + " was not travelling but has started an event?!");
		} else {
			travelStartTime = ev.getTime();
		}
		double travelTime = event.getTime() - travelStartTime;
		totalTravelTimes.adjustOrPutValue(event.getPersonId(), travelTime, travelTime);
	}

	@Override
	public void handleEvent(ActivityEndEvent event) {
		if (isInteraction(event.getActType())) return;
		ActivityEndEvent ev;
		if ((ev = agentsCommuting.put(event.getPersonId(), event)) != null) {
			System.out.println("agent " + ev.getPersonId().toString() + " was travelling but has ended an act?!");		
		}
	}
	
	private static boolean isInteraction(String actType) {
		return actType.contains(" interaction");
	}

}
