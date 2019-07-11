package org.matsim.parkingProxy;

import java.util.Collection;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.trafficmonitoring.TimeBinUtils;
import org.matsim.core.utils.collections.Tuple;

import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TLongIntHashMap;

/**
 * Counts departures and arrivals of not strictly defined entities that move through spacetime (persons,
 * vehicles, ...) based on a spatial and temporal grid. 
 * 
 * @author tkohl / Senozon
 *
 */
public class MovingEntityCounter implements PenaltyGenerator{

	private final TLongIntMap[] carArrivals;
	private final TLongIntMap initialLoad;
	private final HectareMapper hectareMapper;
	private final int timeBinSize;
	private final int numberOfTimeBins;
	
	public MovingEntityCounter(Collection<Tuple<Coord, Integer>> initialCarPositions, int timeBinSize, int endTime, int spatialGridSize) {
		this.timeBinSize = timeBinSize;
		this.hectareMapper = new HectareMapper(spatialGridSize);
		this.numberOfTimeBins = TimeBinUtils.getTimeBinCount(endTime, timeBinSize);
		this.carArrivals = new TLongIntMap[numberOfTimeBins];
		
		this.initialLoad = new TLongIntHashMap();
		for (Tuple<Coord, Integer> carTuple : initialCarPositions) {
			this.initialLoad.adjustOrPutValue(this.hectareMapper.getKey(carTuple.getFirst()), carTuple.getSecond(), carTuple.getSecond());
		}
		
		reset();
	}
	
	public void reset() {
		for (int i = 0; i < this.carArrivals.length; i++) {
			this.carArrivals[i] = new TLongIntHashMap();
		}
	}
	
	public int handleArrival(int time, double x, double y, int weight) {
		return this.carArrivals[getTimeBin(time)].adjustOrPutValue(this.hectareMapper.getKey(x, y), weight, weight);
	}
	
	public int handleDeparture(int time, double x, double y, int weight) {
		int newValue = this.carArrivals[getTimeBin(time)].adjustOrPutValue(this.hectareMapper.getKey(x, y), -weight, -weight);
		return newValue;
	}
	
	public PenaltyCalculator generatePenaltyCalculator() {
		TLongIntMap[] cars = new TLongIntMap[this.carArrivals.length];
		cars[0] = new TLongIntHashMap(this.initialLoad);
		for (int i = 1; i < this.carArrivals.length; i++) {
			cars[i] = new TLongIntHashMap(cars[i-1]);
			TLongIntIterator iter = this.carArrivals[i-1].iterator();
			while (iter.hasNext()) {
				iter.advance();
				cars[i].adjustOrPutValue(iter.key(), iter.value(), iter.value());
			}
		}
		
		return new PenaltyCalculator(cars, this.timeBinSize, this.hectareMapper);
	}
	
	private int getTimeBin(int time) {
		return TimeBinUtils.getTimeBinIndex(time, this.timeBinSize, this.numberOfTimeBins);
	}
}
