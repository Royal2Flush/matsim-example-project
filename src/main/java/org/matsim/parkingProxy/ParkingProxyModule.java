package org.matsim.parkingProxy;

import java.util.Collection;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.parkingProxy.config.ParkingProxyConfigGroup;
import org.matsim.parkingProxy.penaltyCalculator.ExponentialPenaltyFunctionWithCap;
import org.matsim.parkingProxy.penaltyCalculator.InitialLoadGenerator;
import org.matsim.parkingProxy.penaltyCalculator.LinearPenaltyFunctionWithCap;
import org.matsim.parkingProxy.penaltyCalculator.MovingEntityCounter;
import org.matsim.parkingProxy.penaltyCalculator.ParkingVehiclesCountEventHandler;
import org.matsim.parkingProxy.penaltyCalculator.PenaltyFunction;

/**
 * <p>
 * Module to estimate additional time needed by agents due to high parking pressure in an area. For configuration options
 * see {@linkplain ParkingProxyConfigGroup}.
 * </p>
 * <p>
 * The module generates a (random) initial car distribution based on the statistical number of cars per 1000 persons. Then
 * these cars are tracked through the day and for each cell in a space-time-grid counted how many cars are in it. A time
 * penalty is calculated based on that value and added to every agent's egress walk from the car if they are arriving by
 * car in that specific space-time-gridcell.
 * <p>
 * 
 * @author tkohl / Senozon
 *
 */
public class ParkingProxyModule extends AbstractModule {
	
	private final Scenario scenario;
	
	public ParkingProxyModule(Scenario scenario) {
		this.scenario = scenario;
	}

	@Override
	public void install() {
		ParkingProxyConfigGroup parkingConfig = (ParkingProxyConfigGroup) ConfigUtils.addOrGetModule(getConfig(), ParkingProxyConfigGroup.class);
		
		InitialLoadGenerator loadGenerator = new InitialLoadGenerator(scenario.getPopulation().getPersons().values(), parkingConfig.getScenarioScaleFactor());
		Collection<Tuple<Coord, Integer>> initialLoad = loadGenerator.calculateInitialCarPositions(parkingConfig.getCarsPer1000Persons());
		MovingEntityCounter carCounter = new MovingEntityCounter(
				initialLoad, 
				parkingConfig.getTimeBinSize(), 
				(int)getConfig().qsim().getEndTime(),
				parkingConfig.getGridSize()
				);
		PenaltyFunction penaltyFunction = new LinearPenaltyFunctionWithCap(parkingConfig.getGridSize(), parkingConfig.getDelayPerCar(), parkingConfig.getMaxDelay());
		//PenaltyFunction penaltyFunction = new ExponentialPenaltyFunctionWithCap(10, parkingConfig.getGridSize(), parkingConfig.getMaxDelay(), 360);
		
		switch(parkingConfig.getCalculationMethod()) {
		case none:
			break;
		case events:
			ParkingVehiclesCountEventHandler parkingHandler = new ParkingVehiclesCountEventHandler(carCounter, scenario.getNetwork(), parkingConfig.getScenarioScaleFactor());
			super.addEventHandlerBinding().toInstance(parkingHandler);
			if (parkingConfig.getObserveOnly()) {
				super.addControlerListenerBinding().toInstance(new CarEgressWalkObserver(parkingHandler, penaltyFunction));
			} else {
				super.addControlerListenerBinding().toInstance(new CarEgressWalkChanger(parkingHandler, penaltyFunction));
			}
			break;
		case plans:
			throw new RuntimeException("Mode \"plans\" is not working yet. Use \"events\" instead.");
			/*
			ParkingCounterByPlans planCounter = new ParkingCounterByPlans(carCounter, parkingConfig.getScenarioScaleFactor());
			super.addControlerListenerBinding().toInstance(planCounter);
			if (parkingConfig.getObserveOnly()) {
				super.addControlerListenerBinding().toInstance(new CarEgressWalkObserver(planCounter, penaltyFunction));
			} else {
				super.addControlerListenerBinding().toInstance(new CarEgressWalkChanger(planCounter, penaltyFunction));
			}
			break;*/
		default:
			throw new RuntimeException("Unsupported calculation method " + parkingConfig.getCalculationMethod());	
		}
	}

}
