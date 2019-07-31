package org.matsim.parkingProxy.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.population.algorithms.AbstractPersonAlgorithm;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.opengis.feature.simple.SimpleFeature;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;

public class RegionModeshareAnalyzer extends AbstractPersonAlgorithm {
	
	private final Collection<SimpleFeature> areas;
	private final Map<String, TObjectIntMap<String>> inModes;
	private final Map<String, TObjectIntMap<String>> outModes;
	
	public RegionModeshareAnalyzer(Collection<SimpleFeature> areas) {
		this.areas = areas;
		inModes = new HashMap<>();
		outModes = new HashMap<>();
		for (SimpleFeature f : areas) {
			inModes.put(f.getID(), new TObjectIntHashMap<String>());
			outModes.put(f.getID(), new TObjectIntHashMap<String>());
		}
	}

	@Override
	public void run(Person person) {
		List<PlanElement> elements = person.getSelectedPlan().getPlanElements();
		for (int i = 0; i < elements.size(); i++) {
			PlanElement e = elements.get(i);
			if (e instanceof Leg) {
				Leg leg = (Leg) e;
				for (SimpleFeature f : areas) {
					Activity prevAct = (Activity) elements.get(i-1);
					Activity nextAct = (Activity) elements.get(i+1);
					if (((Geometry)f.getDefaultGeometry()).contains(MGC.coord2Point(prevAct.getCoord()))) {
						outModes.get(f.getID()).adjustOrPutValue(leg.getMode(), 1, 1);
					}
					if (((Geometry)f.getDefaultGeometry()).contains(MGC.coord2Point(nextAct.getCoord()))) {
						inModes.get(f.getID()).adjustOrPutValue(leg.getMode(), 1, 1);
					}
				}
			}
		}
	}
	
	public void write(File outfile) {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(outfile))) {
			writer.write("area;mode;in;out");
			writer.newLine();
			for (SimpleFeature f : areas) {
				inModes.get(f.getID()).forEachEntry(new TObjectIntProcedure<String>() {
					@Override
					public boolean execute(String a, int b) {
						int c = outModes.get(f.getID()).get(a);
						try {
							writer.write(f.getAttribute("desc")+";"+a+";"+b+";"+c);
							writer.newLine();
						} catch (IndexOutOfBoundsException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return true;
					}
				});
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}