package org.matsim.class2019.personAlgorithms;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.population.algorithms.PersonAlgorithm;
import org.matsim.core.utils.geometry.geotools.MGC;

public class PtInteractionCollector implements PersonAlgorithm {
	private final Set<Id<Link>> linksToWatch;
	private final Set<Id<Person>> ptPersonsTotal;
	private final Set<Id<Person>> ptPersonsUdl;
	private final Set<Id<Person>> ptPersonsUdlEnhanced;
	private final Collection<Geometry> aoi;
	
	public PtInteractionCollector(Set<Id<Link>> linksToWatch, Collection<Geometry> aoi) {
		ptPersonsTotal = new HashSet<>();
		ptPersonsUdl = new HashSet<>();
		ptPersonsUdlEnhanced = new HashSet<>();
		this.linksToWatch = linksToWatch;
		this.aoi = aoi;
	}

	@Override
	public void run(Person person) {
		for (PlanElement element : person.getSelectedPlan().getPlanElements()) {
			if (element instanceof Activity) {
				Activity act = (Activity) element;
				if (act.getType().equals("pt interaction")) {
					ptPersonsTotal.add(person.getId());
					if (linksToWatch.contains(act.getLinkId())) {
						ptPersonsUdl.add(person.getId());
						ptPersonsUdlEnhanced.add(person.getId());
					} else {
						Geometry interactionPoint = MGC.coord2Point(act.getCoord());
						boolean inaoi = false;
						for (Geometry geom : aoi) {
							if (geom.contains(interactionPoint)) {
								inaoi = true;
								break;
							}
						}
						if (inaoi) {
							ptPersonsUdlEnhanced.add(person.getId());
						}
					}
				}
			}
		}
	}
	
	public Set<Id<Person>> getAllPtUsers() {
		return this.ptPersonsTotal;
	}
	
	public Set<Id<Person>> getUdlPtUsersBus() {
		return this.ptPersonsUdl;
	}
	
	public Set<Id<Person>> getUdlPtUsers() {
		return this.ptPersonsUdlEnhanced;
	}

}
