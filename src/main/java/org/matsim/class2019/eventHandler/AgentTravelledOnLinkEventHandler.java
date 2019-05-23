package org.matsim.class2019.eventHandler;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.utils.io.IOUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class AgentTravelledOnLinkEventHandler implements LinkLeaveEventHandler {

	private final Set<Id<Link>> linksToWatch;
	private final Set<Id<Person>> personOnWatchedLinks = new HashSet<>();

	AgentTravelledOnLinkEventHandler(Set<Id<Link>> linksToWatch) {
		this.linksToWatch = linksToWatch;
	}

	Set<Id<Person>> getPersonOnWatchedLinks() {
		return personOnWatchedLinks;
	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {
		if (linksToWatch.contains(event.getLinkId())) {
			Id<Person> id = Id.createPersonId(event.getVehicleId());
			personOnWatchedLinks.add(id);
		}
	}
	
	public void write(Path outfile) {
		try (BufferedWriter writer = IOUtils.getBufferedWriter(outfile.toString())) {
			for (Id<Person> id : personOnWatchedLinks) {
				writer.write(id.toString()+"\n");
			}
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}