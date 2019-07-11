package org.matsim.parkingProxy.penaltyCalculator;

import java.util.HashSet;
import java.util.Set;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.TransitDriverStartsEvent;
import org.matsim.api.core.v01.events.VehicleEntersTrafficEvent;
import org.matsim.api.core.v01.events.VehicleLeavesTrafficEvent;
import org.matsim.api.core.v01.events.handler.TransitDriverStartsEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleEntersTrafficEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleLeavesTrafficEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.vehicles.Vehicle;

/**
 * Tracks how many vehicles are in a given area at any time based on {@linkplain VehicleEntersTrafficEvent}s and
 * {@linkplain VehicleLeavesTrafficEvent}s. PT vehicles are excluded by tracking the involved vehicles of all
 * {@linkplain TransitDriverStartsEvent}s. </br>
 * 
 * The functionality of the {@linkplain PenaltyGenerator} interface are delegated to the {@linkplain MovingEntityCounter}
 * received in the constructor.
 * 
 * @author tkohl / Senozon
 *
 */
public class ParkingVehiclesCountEventHandler
		implements VehicleEntersTrafficEventHandler, VehicleLeavesTrafficEventHandler, TransitDriverStartsEventHandler,
		PenaltyGenerator {

	private final Network network;
	private final MovingEntityCounter carCounter;
	private final int carWeight;
	
	private Set<Id<Vehicle>> knownPtVehicles;
	
	/**
	 * Sets up the EventHandler and calls {@linkplain #reset()}.
	 * 
	 * @param carCounter The central part of this class responsible for defining spatial and temporal resolution
	 * @param network The used network. Necessary to look up link-ids
	 * @param carWeight the weight of a single car; usually the inverse of the scenario percentage, i.e. 100 in case of 1pct scenario
	 */
	public ParkingVehiclesCountEventHandler(MovingEntityCounter carCounter, Network network, int carWeight) {
		this.carCounter = carCounter;
		this.network = network;
		this.carWeight = carWeight;
		reset();
	}

	@Override
	public void handleEvent(VehicleLeavesTrafficEvent event) {
		if (!this.knownPtVehicles.contains(event.getVehicleId())) {
			Link link = network.getLinks().get(event.getLinkId());
			Coord coord = getCoordOnLink(link, event.getRelativePositionOnLink());
			carCounter.handleArrival((int) event.getTime(), coord.getX(), coord.getY(), carWeight);
		}
	}

	@Override
	public void handleEvent(VehicleEntersTrafficEvent event) {
		if (!this.knownPtVehicles.contains(event.getVehicleId())) {
			Link link = network.getLinks().get(event.getLinkId());
			Coord coord = getCoordOnLink(link, event.getRelativePositionOnLink());
			carCounter.handleDeparture((int) event.getTime(), coord.getX(), coord.getY(), carWeight);
		}
	}
	
	@Override
	public void handleEvent(TransitDriverStartsEvent event) {
		knownPtVehicles.add(event.getVehicleId());
	}
	
	@Override
	public PenaltyCalculator generatePenaltyCalculator() {
		return this.carCounter.generatePenaltyCalculator();
	}
	
	@Override
	public void reset() {
		this.knownPtVehicles = new HashSet<Id<Vehicle>>();
		carCounter.reset();
	}
	
	/**
	 * Gets the precise coordinate of a point at a certain percentage of the link's extent. Note: This method
	 * currently (07/2019) is used to calculate the position of events, which ALWAYS happen at the end-node of
	 * a link and therefore isn't very useful. However, I left it in in case this changes in the future.
	 * 
	 * @param link
	 * @param lengthPercentage
	 * @return The coordinate at the lengthPercentage on the link.
	 */
	private static Coord getCoordOnLink(Link link, double lengthPercentage) {
		Coord distanceFromTo = CoordUtils.minus(link.getToNode().getCoord(), link.getFromNode().getCoord());
		Coord relativePosition = CoordUtils.scalarMult(lengthPercentage, distanceFromTo);
		return CoordUtils.plus(link.getFromNode().getCoord(), relativePosition);
	}
	
	
}
