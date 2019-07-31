package org.matsim.parkingProxy.analysis;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.StreamingPopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

public class RunAreaAnalysis {

	public static void main(String[] args) {
		Path shapefile = Paths.get(args[0]);
		Path experiencedPlansPath = Paths.get(args[1]);
		Path networkFile = Paths.get(args[2]);

		Collection<SimpleFeature> features = ShapeFileReader.getAllFeatures(shapefile.toString());
		
		Config conf = ConfigUtils.createConfig();
		conf.network().setInputFile(networkFile.toString());
		RegionModeshareAnalyzer modeshares = new RegionModeshareAnalyzer(features);
		StreamingPopulationReader reader = new StreamingPopulationReader(ScenarioUtils.loadScenario(conf));
		reader.addAlgorithm(modeshares);
		reader.readFile(experiencedPlansPath.toString());
		modeshares.write(new File(experiencedPlansPath.getParent().toString(), "areaModeShare.csv"));
	}

}
