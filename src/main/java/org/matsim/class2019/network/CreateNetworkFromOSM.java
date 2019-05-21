package org.matsim.class2019.network;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Creates a detailed network inside  aspecified shapefile and a rough network outside of it.
 * 
 * @author tkohl after Janekdererste
 *
 */
public class CreateNetworkFromOSM {

	public static final int numExpectedArgs = 2;
	public static final String epsg = "EPSG:25832";
	
	private final Path OSMFile, SHPFile;
	
	public static void main(String[] args) {
		if (args.length != numExpectedArgs) {
			throw new IllegalArgumentException("expected exactly " + numExpectedArgs + " arguments but got " + args.length);
		}
		Path inputOSMFile = Paths.get(args[0]);
		Path inputSHPFile = Paths.get(args[1]);
		Path outFile = Paths.get(inputSHPFile.toString().replace(".shp", "_filtered.xml.gz"));
		new CreateNetworkFromOSM(inputOSMFile, inputSHPFile).create(outFile);
	}
	
	public CreateNetworkFromOSM(Path inputOSMFile, Path inputSHPFile) {
		this.OSMFile = inputOSMFile;
		this.SHPFile = inputSHPFile;
	}

	public void create(Path outputFile) {
		Network network = NetworkUtils.createNetwork();
		CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation(
					TransformationFactory.WGS84, epsg);
		
		OsmNetworkReader reader = new OsmNetworkReader(network, transformation, true, true);
		reader.addOsmFilter(new NetworkFilter(this.SHPFile));
		reader.parse(this.OSMFile.toString());
		
		new NetworkCleaner().run(network);
		new NetworkWriter(network).write(outputFile.toString());
	}
}