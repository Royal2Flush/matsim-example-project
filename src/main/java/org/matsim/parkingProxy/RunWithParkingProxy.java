package org.matsim.parkingProxy;

import java.util.Collection;
import java.util.LinkedList;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.parkingProxy.config.ParkingProxyConfigGroup;
import org.matsim.parkingProxy.penaltyCalculator.LinearPenaltyFunctionWithCap;
import org.matsim.parkingProxy.penaltyCalculator.MovingEntityCounter;
import org.matsim.parkingProxy.penaltyCalculator.ParkingCounterByPlans;
import org.matsim.parkingProxy.penaltyCalculator.ParkingVehiclesCountEventHandler;
import org.matsim.parkingProxy.penaltyCalculator.PenaltyFunction;

public class RunWithParkingProxy {

	public static void main(String[] args) {
		
		ParkingProxyConfigGroup parkingConfig = new ParkingProxyConfigGroup();
		Config config = ConfigUtils.loadConfig(args[0], parkingConfig);

		config.controler().setLastIteration(100);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.overwriteExistingFiles);
		config.plansCalcRoute().setInsertingAccessEgressWalk(true);
		config.planCalcScore().setWriteExperiencedPlans(true);
		
		Scenario scen = ScenarioUtils.loadScenario(config);
		
		Controler controler = new Controler(scen);
		
		Collection<Tuple<Coord, Integer>> initialCarPositions = new LinkedList<Tuple<Coord, Integer>>();
		for (Person p : scen.getPopulation().getPersons().values()) {
			initialCarPositions.add(new Tuple<>(((Activity)p.getSelectedPlan().getPlanElements().get(0)).getCoord(), parkingConfig.getScenarioScaleFactor()));
		}
		MovingEntityCounter carCounter = new MovingEntityCounter(
				initialCarPositions, 
				parkingConfig.getTimeBinSize(), 
				(int)config.qsim().getEndTime(),
				parkingConfig.getGridSize()
				);
		PenaltyFunction penaltyFunction = new LinearPenaltyFunctionWithCap(parkingConfig.getDelayPerCar(), parkingConfig.getMaxDelay());
		
		switch(parkingConfig.getCalculationMethod()) {
		case none:
			break;
		case events:
			ParkingVehiclesCountEventHandler parkingHandler = new ParkingVehiclesCountEventHandler(carCounter, scen.getNetwork(), parkingConfig.getScenarioScaleFactor());
			controler.getEvents().addHandler(parkingHandler);
			controler.addControlerListener(new CarEgressWalkChanger(parkingHandler, penaltyFunction));
			break;
		case plans:
			ParkingCounterByPlans planCounter = new ParkingCounterByPlans(carCounter, parkingConfig.getScenarioScaleFactor());
			controler.addControlerListener(planCounter);
			controler.addControlerListener(new CarEgressWalkChanger(planCounter, penaltyFunction));
			break;
		default:
			throw new RuntimeException("Unsupported calculation method " + parkingConfig.getCalculationMethod());	
		}
		
		controler.run();
	}

}
