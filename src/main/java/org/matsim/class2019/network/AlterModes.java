package org.matsim.class2019.network;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

public class AlterModes {

	public static final int numExpectedArgs = 2;
	public static final String epsg = "EPSG:31468";
	
	private final Collection<Geometry> geometries = new LinkedList<>();
	private final Network network;
	
	private Collection<Link> linksInsideShape;
	
	public static void main(String[] args) {
		if (args.length != numExpectedArgs) {
			throw new IllegalArgumentException("expected exactly " + numExpectedArgs + " arguments but got " + args.length);
		}
		Path inputNetworkFile = Paths.get(args[0]);
		Path inputSHPFile = Paths.get(args[1]);
		Path outFile = Paths.get(inputNetworkFile.toString().replace(".xml.gz", "_modes_altered.xml.gz"));
		
		/*Collection<String> modesToRemove = new ArrayList<String>(2);
		modesToRemove.add("car");
		modesToRemove.add("freight");
		modesToRemove.add("ride");*/
		
		AlterModes alter = new AlterModes(inputNetworkFile, inputSHPFile);
		//alter.remove(modesToRemove);
		alter.changeFreespeedTo(1);
		alter.write(outFile);
	}
	
	public AlterModes(Path inputNetworkFile, Path inputSHPFile) {
		Config conf = ConfigUtils.createConfig();
		conf.network().setInputFile(inputNetworkFile.toString());
		//conf.network().setInputCRS(epsg);
		this.network = ScenarioUtils.loadScenario(conf).getNetwork();
		
		for (SimpleFeature feature : ShapeFileReader.getAllFeatures(inputSHPFile.toString())) {
			geometries.add((Geometry) feature.getDefaultGeometry());
		}
	}
	
	public void changeFreespeedTo(double freespeed) {
		for (Link link : lazySelectLinks()) {
			if(!link.getId().toString().contains("pt_")) {
				link.setFreespeed(freespeed);
			}
		}
	}

	public void remove(Collection<String> modesToRemove) {
		for (Link link : lazySelectLinks()) {
			Set<String> modes = new HashSet<String>(link.getAllowedModes());
			modes.removeAll(modesToRemove);
			link.setAllowedModes(modes);
		}
	}
	
	public void add(Collection<String> modesToAdd) {
		for (Link link : lazySelectLinks()) {
			Set<String> modes = new HashSet<String>(link.getAllowedModes());
			modes.addAll(modesToAdd);
			link.setAllowedModes(modes);
		}
	}
	
	public void write(Path outputFile) {
		new NetworkWriter(network).write(outputFile.toString());
	}
	
	public Collection<Link> lazySelectLinks() {
		if (this.linksInsideShape == null) {
			this.linksInsideShape = new ArrayList<Link>();
			for (Link link : network.getLinks().values()) {
				if (featureContainsCoord(link.getFromNode().getCoord()) && featureContainsCoord(link.getToNode().getCoord())) {
					this.linksInsideShape.add(link);
				}
			}
		}
		
		return this.linksInsideShape;
	}
	
	public Collection<Geometry> getAOI() {
		return this.geometries;
	}
	
	private boolean featureContainsCoord(Coord coord) {
		return geometries.stream().anyMatch(geom -> geom.contains(MGC.coord2Point(coord)));
	}

}
