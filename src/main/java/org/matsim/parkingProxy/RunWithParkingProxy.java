package org.matsim.parkingProxy;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.parkingProxy.config.ParkingProxyConfigGroup;

public class RunWithParkingProxy {

	public static void main(String[] args) {
		
		ParkingProxyConfigGroup parkingConfig = new ParkingProxyConfigGroup();
		Config config = ConfigUtils.loadConfig(args[0], parkingConfig);

		//config.controler().setLastIteration(100);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.overwriteExistingFiles);
		config.plansCalcRoute().setInsertingAccessEgressWalk(true);
		config.planCalcScore().setWriteExperiencedPlans(true);
		
		Scenario scen = ScenarioUtils.loadScenario(config);
		
		Controler controler = new Controler(scen);
		
		controler.addOverridingModule(new ParkingProxyModule(scen));
		
		controler.run();
	}

}
