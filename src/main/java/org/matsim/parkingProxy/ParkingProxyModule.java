package org.matsim.parkingProxy;

import java.util.Collection;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.parkingProxy.config.ParkingProxyConfigGroup;
import org.matsim.parkingProxy.penaltyCalculator.InitialLoadGenerator;
import org.matsim.parkingProxy.penaltyCalculator.LinearPenaltyFunctionWithCap;
import org.matsim.parkingProxy.penaltyCalculator.MovingEntityCounter;
import org.matsim.parkingProxy.penaltyCalculator.ParkingCounterByPlans;
import org.matsim.parkingProxy.penaltyCalculator.ParkingVehiclesCountEventHandler;
import org.matsim.parkingProxy.penaltyCalculator.PenaltyFunction;

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
		PenaltyFunction penaltyFunction = new LinearPenaltyFunctionWithCap(parkingConfig.getDelayPerCar(), parkingConfig.getMaxDelay());
		
		switch(parkingConfig.getCalculationMethod()) {
		case none:
			break;
		case events:
			ParkingVehiclesCountEventHandler parkingHandler = new ParkingVehiclesCountEventHandler(carCounter, scenario.getNetwork(), parkingConfig.getScenarioScaleFactor());
			super.addEventHandlerBinding().toInstance(parkingHandler);
			super.addControlerListenerBinding().toInstance(new CarEgressWalkChanger(parkingHandler, penaltyFunction));
			//controler.getEvents().addHandler(parkingHandler);
			//controler.addControlerListener(new CarEgressWalkChanger(parkingHandler, penaltyFunction));
			break;
		case plans:
			ParkingCounterByPlans planCounter = new ParkingCounterByPlans(carCounter, parkingConfig.getScenarioScaleFactor());
			super.addControlerListenerBinding().toInstance(planCounter);
			super.addControlerListenerBinding().toInstance(new CarEgressWalkChanger(planCounter, penaltyFunction));
			//controler.addControlerListener(planCounter);
			//controler.addControlerListener(new CarEgressWalkChanger(planCounter, penaltyFunction));
			break;
		default:
			throw new RuntimeException("Unsupported calculation method " + parkingConfig.getCalculationMethod());	
		}
	}

}
