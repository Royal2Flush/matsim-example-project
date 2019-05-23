package org.matsim.class2019.personAlgorithms;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.population.algorithms.PersonAlgorithm;
import org.matsim.core.utils.io.IOUtils;

public class HomeCoordinateCollector implements PersonAlgorithm {
	
	private final Collection<Id<Person>> personsToCollect;
	private final Collection<Coord> homeCoords = new LinkedList<Coord>();
	
	public HomeCoordinateCollector(Collection<Id<Person>> personsToCollect) {
		this.personsToCollect = personsToCollect;
	}

	@Override
	public void run(Person person) {
		if (personsToCollect.contains(person.getId())) {
			for (PlanElement planElement : person.getSelectedPlan().getPlanElements()) {
				if (planElement instanceof Activity) {
					Activity act = (Activity) planElement;
					if (act.getType().contains("home")) {
						homeCoords.add(act.getCoord());
						continue;
					}
				}
			}
		}
	}
	
	public Collection<Coord> getCoords() {
		return this.homeCoords;
	}
	
	public void write(Path outfile) {
		try (BufferedWriter writer = IOUtils.getBufferedWriter(outfile.toString())) {
			writer.write("x;y\n");
			for (Coord c : homeCoords) {
				writer.write(c.getX()+";"+c.getY()+"\n");
			}
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
