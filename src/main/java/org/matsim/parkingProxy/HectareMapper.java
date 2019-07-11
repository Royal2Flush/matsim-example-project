package org.matsim.parkingProxy;

import org.matsim.api.core.v01.Coord;

/**
 * <p>
 * Class that sorts coordinates into a grid and maps the gridcells to a key.
 * </p>
 * <p>
 * The key is composed in such a way that the x-number (Integer) of the gridcell (starting at
 * 0,0) is stored in the first half of the bits of the Long-key and the y-number (Integer) in
 * the second half.
 * </p>
 * 
 * @author tkohl / Senozon after a concept by mrieser (then also Senozon)
 *
 */
public class HectareMapper {
	private final int gridsize;
	
	public HectareMapper(int gridsize) {
		this.gridsize = gridsize;
	}
	
	public long getKey(final double x, final double y) {
		long xCell = ((long)x / this.gridsize);
		long yCell = ((long)y / this.gridsize);
		long key = ((yCell & 0x0000_00000_FFFF_FFFFL) << 32) | (xCell & 0x0000_00000_FFFF_FFFFL);
		return key;
	}
	
	public long getKey(final Coord coord) {
		return getKey(coord.getX(), coord.getY());
	}

	public Coord getCenter(long key) {
		int xCell = (int) (key & 0x0000_0000_FFFF_FFFFL);
		int yCell = (int) ((key >> 32) & 0x0000_0000_FFFF_FFFFL);
		return new Coord(xCell * this.gridsize + this.gridsize/2, yCell * this.gridsize + this.gridsize/2);
	}
}
