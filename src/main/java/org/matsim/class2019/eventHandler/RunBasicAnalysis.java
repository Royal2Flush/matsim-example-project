package org.matsim.class2019.eventHandler;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.class2019.network.AlterModes;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class RunBasicAnalysis {

	public static void main(String[] args) {


		// get the paths for the network and the events
		Path networkpath = Paths.get(args[0]);
		Path shapefile = Paths.get(args[1]);
		Path baseCaseEventsPath = Paths.get(args[2]);
		Path policyCaseEventsPath = Paths.get(args[3]);

		// read in the simulation network
		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile(networkpath.toString());

		Set<Id<Link>> linksToWatch = new AlterModes(networkpath, shapefile).lazySelectLinks()
				.stream().map(l -> l.getId())
				.collect(Collectors.toSet());
		
		// start preparing the events manager
		AgentTravelledOnLinkEventHandler agentTravelledOnLinkEventHandler = new AgentTravelledOnLinkEventHandler(linksToWatch);
		TravelDistanceEventHandler travelDistanceEventHandler = new TravelDistanceEventHandler(network);
		TravelTimeEventHandler travelTimeEventHandler = new TravelTimeEventHandler();

		EventsManager baseCaseManager = EventsUtils.createEventsManager();
		baseCaseManager.addHandler(agentTravelledOnLinkEventHandler);
		baseCaseManager.addHandler(travelDistanceEventHandler);
		baseCaseManager.addHandler(travelTimeEventHandler);

		// read the actual events file
		new MatsimEventsReader(baseCaseManager).readFile(baseCaseEventsPath.toString());

		// start preparing the events manager for the policy case
		TravelDistanceEventHandler travelDistanceEventHandlerPolicy = new TravelDistanceEventHandler(network);
		TravelTimeEventHandler travelTimeEventHandlerPolicy = new TravelTimeEventHandler();

		EventsManager policyCaseManager = EventsUtils.createEventsManager();
		policyCaseManager.addHandler(travelDistanceEventHandlerPolicy);
		policyCaseManager.addHandler(travelTimeEventHandlerPolicy);

		new MatsimEventsReader(policyCaseManager).readFile(policyCaseEventsPath.toString());

		System.out.println("Total travel time base case: " + travelTimeEventHandler.calculateOverallTravelTime() / 60 / 60 + " hours");
		System.out.println("Total travel time policy case: " + travelTimeEventHandlerPolicy.calculateOverallTravelTime() / 60 / 60 + " hours");

		System.out.println("Total travel distance base case: " + travelDistanceEventHandler.getTotalTravelDistance() / 1000 + "km");
		System.out.println(" policy case: " + travelDistanceEventHandlerPolicy.getTotalTravelDistance() / 1000 + "km");

		// calculate travel time for people who used the street
		double baseCaseTravelTime = travelTimeEventHandler.getTravelTimesByPerson().entrySet().stream()
				.filter(entry -> agentTravelledOnLinkEventHandler.getPersonOnWatchedLinks().contains(entry.getKey()))
				.mapToDouble(entry -> entry.getValue())
				.sum();

		double policyCaseTravelTime = travelTimeEventHandlerPolicy.getTravelTimesByPerson().entrySet().stream()
				.filter(entry -> agentTravelledOnLinkEventHandler.getPersonOnWatchedLinks().contains(entry.getKey()))
				.mapToDouble(entry -> entry.getValue())
				.sum();

		System.out.println("Difference in travel time for people who travelled on link. Base case: "
				+ (policyCaseTravelTime - baseCaseTravelTime));

		// calculate travel distances for people who used the street
		double baseCaseDistance = travelDistanceEventHandler.getTravelDistancesByPerson().entrySet().stream()
				.filter(entry -> agentTravelledOnLinkEventHandler.getPersonOnWatchedLinks().contains(entry.getKey()))
				.mapToDouble(entry -> entry.getValue())
				.sum();

		double policyCaseDistance = travelDistanceEventHandlerPolicy.getTravelDistancesByPerson().entrySet().stream()
				.filter(entry -> agentTravelledOnLinkEventHandler.getPersonOnWatchedLinks().contains(entry.getKey()))
				.mapToDouble(entry -> entry.getValue())
				.sum();

		System.out.println("Difference in travel distances for people who travelled on link: " + (policyCaseDistance - baseCaseDistance));
	}
}