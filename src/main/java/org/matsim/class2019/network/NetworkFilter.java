package org.matsim.class2019.network;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.matsim.core.utils.io.OsmNetworkReader.OsmFilter;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Keeps everything inside the feature boundaries defined in a shapefile
 * and only hierarchies 1-3 for osm objects outside of it.
 * 
 * @author tkohl after Janekdererste
 *
 */
public class NetworkFilter implements OsmFilter {

	private final Collection<Geometry> geometries = new ArrayList<>();

	public NetworkFilter(Path shapeFile) {
		for (SimpleFeature feature : ShapeFileReader.getAllFeatures(shapeFile.toString())) {
			geometries.add((Geometry) feature.getDefaultGeometry());
		}
	}

	@Override
	public boolean coordInFilter(Coord coord, int hierarchyLevel) {
		if (hierarchyLevel <= 4) {
			// hierachy levels 1 - 3 are motorways and primary roads, as well as their trunks. Always include.
			return true;
		} else {
			// else only include if inside shape
			return hierarchyLevel <= 8 && containsCoord(coord);
		}
	}

	private boolean containsCoord(Coord coord) {
		return geometries.stream().anyMatch(geom -> geom.contains(MGC.coord2Point(coord)));
	}
}