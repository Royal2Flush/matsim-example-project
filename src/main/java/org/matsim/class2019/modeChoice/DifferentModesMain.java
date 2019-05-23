package org.matsim.class2019.modeChoice;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup.ModeRoutingParams;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultStrategy;
import org.matsim.core.scenario.ScenarioUtils;

public class DifferentModesMain {

	public static void main(String[] args) {
		String configfile = "./scenarios/equil/config.xml";
		Config config = ConfigUtils.loadConfig(configfile);
		
		config.controler().setOutputDirectory("./output");
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.overwriteExistingFiles);
		config.controler().setLastIteration(2);
		
		StrategySettings stratSets = new StrategySettings();
		stratSets.setStrategyName(DefaultStrategy.ChangeSingleTripMode);
		stratSets.setWeight(1);
		config.strategy().addStrategySettings(stratSets);
		
		ModeRoutingParams scooterOoutingParams = new ModeRoutingParams("scooter");
		scooterOoutingParams.setBeelineDistanceFactor(1.5);
		scooterOoutingParams.setTeleportedModeSpeed(6.2);
		config.plansCalcRoute().addModeRoutingParams(scooterOoutingParams );
		
		ModeParams scooterModeParams = new ModeParams("scooter");
		scooterModeParams.setConstant(4.0);
		scooterModeParams.setMarginalUtilityOfTraveling(0.);
		scooterModeParams.setMarginalUtilityOfDistance(-0.1);
		config.planCalcScore().addModeParams(scooterModeParams);
		
		String[] modes = {"car", "scooter"};
		config.changeMode().setModes(modes);
		
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);
		controler.run();
	}

}
