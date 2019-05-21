package org.matsim.class2019.eventHandler;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;

public class TravelTimeMain {

	public static void main(String[] args) {
		Path events = Paths.get(args[0]);
		
		TravelTimeEventHandler handler = new TravelTimeEventHandler();
		EventsManager eventManager = EventsUtils.createEventsManager();
		eventManager.addHandler(handler);
		
		new MatsimEventsReader(eventManager).readFile(events.toString());
		
		System.out.println(handler.getTotalTime());
		
	}

}
