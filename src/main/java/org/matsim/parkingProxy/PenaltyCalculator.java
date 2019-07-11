package org.matsim.parkingProxy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.trafficmonitoring.TimeBinUtils;

import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TLongIntHashMap;

public class PenaltyCalculator {
	
	private final TLongIntMap[] numberOfCars;
	private final int numberOfTimeBins;
	private final int timeBinSize;
	private final HectareMapper hectareMapper;
	
	private PenaltyFunction penaltyFunction;
	
	/*package*/ PenaltyCalculator(TLongIntMap[] numberOfCars, int timeBinSize, HectareMapper hectareMapper) {
		this.numberOfCars = numberOfCars;
		this.numberOfTimeBins = numberOfCars.length;
		this.timeBinSize = timeBinSize;
		this.hectareMapper = hectareMapper;
		this.setPenaltyFunction(new DefaultPenaltyFunction());
	}
	
	public void setPenaltyFunction(PenaltyFunction function) {
		this.penaltyFunction = function;
	}
	
	public double getPenalty(double time, double x, double y) {
		int cars = this.numberOfCars[calculateTimeBin((int) time)].get(this.hectareMapper.getKey(x, y));
		return this.penaltyFunction.calculatePenalty(cars);
	}
	
	public double getPenalty(double time, Coord coord) {
		return getPenalty(time, coord.getX(), coord.getY());
	}
	
	private int calculateTimeBin(int time) {
		return TimeBinUtils.getTimeBinIndex(time, this.timeBinSize, this.numberOfTimeBins);
	}
	
	public void dump(File outputfile) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputfile))) {
			writer.write("x;y;t;penalty");
			writer.newLine();
			for (int tbin = 0; tbin < numberOfCars.length; tbin++) {
				TLongIntIterator iter = this.numberOfCars[tbin].iterator();
				while (iter.hasNext()) {
					iter.advance();
					Coord coord = hectareMapper.getCenter(iter.key());
					String entry = (int) coord.getX() + ";";
					entry += (int)coord.getY() + ";";
					entry += tbin * timeBinSize + ";";
					entry += penaltyFunction.calculatePenalty(iter.value());
					writer.write(entry);
					writer.newLine();
				}
			}
			writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public static PenaltyCalculator getDummyCalculator() {
		TLongIntMap[] cars = new TLongIntMap[1];
		cars[0] = new TLongIntHashMap();
		PenaltyCalculator penaltyCalculator = new PenaltyCalculator(cars, 1, new HectareMapper(1));
		penaltyCalculator.setPenaltyFunction(new DummyPenaltyFunction());
		return penaltyCalculator;
	}
	
	public static class DefaultPenaltyFunction implements PenaltyFunction {
		@Override
		public double calculatePenalty(int numberOfCars) {
			// 2.5 seconds per car but not more than 15 minutes
			return Math.min(numberOfCars*2.5, 900);
		}
	}
	
	public static class DummyPenaltyFunction implements PenaltyFunction {
		@Override
		public double calculatePenalty(int numberOfCars) {
			return 3600;
		}
	}
}
