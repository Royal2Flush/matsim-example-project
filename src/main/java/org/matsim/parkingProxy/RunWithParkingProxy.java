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

public class RunWithParkingProxy {

	public static void main(String[] args) {
		
		Config config = ConfigUtils.loadConfig(args[0]);

		config.controler().setLastIteration(100);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.overwriteExistingFiles);
		config.plansCalcRoute().setInsertingAccessEgressWalk(true);
		config.planCalcScore().setWriteExperiencedPlans(true);
		
		Scenario scen = ScenarioUtils.loadScenario(config);
		
		Controler controler = new Controler(scen);
		
		Collection<Tuple<Coord, Integer>> initialCarPositions = new LinkedList<Tuple<Coord, Integer>>();
		for (Person p : scen.getPopulation().getPersons().values()) {
			initialCarPositions.add(new Tuple<>(((Activity)p.getSelectedPlan().getPlanElements().get(0)).getCoord(), 100));
		}
		MovingEntityCounter carCounter = new MovingEntityCounter(initialCarPositions, 900, 81000, 500);
		/*ParkingVehiclesCountEventHandler parkingHandler = new ParkingVehiclesCountEventHandler(carCounter, scen.getNetwork(), 100);
		controler.getEvents().addHandler(parkingHandler);
		
		controler.addControlerListener(new CarEgressWalkChanger(parkingHandler));*/
		ParkingCounterByPlans planCounter = new ParkingCounterByPlans(carCounter, 100);
		controler.addControlerListener(planCounter);
		
		controler.addControlerListener(new CarEgressWalkChanger(planCounter));
		
		controler.run();
	}

}
