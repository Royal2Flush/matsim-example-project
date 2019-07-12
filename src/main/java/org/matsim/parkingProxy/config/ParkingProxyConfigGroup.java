package org.matsim.parkingProxy.config;

import java.util.Map;

import org.matsim.core.config.ReflectiveConfigGroup;

public class ParkingProxyConfigGroup extends ReflectiveConfigGroup {
	
	public static enum CalculationMethod {none, events, plans};
	
	public static final String GROUP_NAME = "parkingProxy";
	public static final String METHOD = "method";
	public static final String DELAY_PER_CAR = "delayPerCar";
	public static final String MAX_DELAY = "maxDelay";
	public static final String SCALE_FACTOR = "scenarioScaleFactor";
	public static final String TIME_BIN_SIZE = "timeBinSize";
	public static final String GRID_SIZE = "gridSize";
	public static final String CARS_PER_1000_PERSONS = "carsPer1000Persons";
	
	private CalculationMethod method = CalculationMethod.none;
	private double delayPerCar = 2.5;
	private double maxDelay = 900;
	private int scenarioScaleFactor = 100;
	private int timeBinSize = 900;
	private int gridSize = 500;
	private int carsPer1000Persons = 500;

	public ParkingProxyConfigGroup() {
		super(GROUP_NAME);
	}
	
	@Override
	public Map<String, String> getComments() {
		Map<String, String> comments = super.getComments();
		comments.put(SCALE_FACTOR, "The inverse of the scenario perentage, i.e. the number with which to multiply the" 
				+ " number of agents to get the real life population, e.g. 4 in a 25% scenario. Needs to be an Intger,"
				+ " so in case of weird percentages (e.g. 1/3) please round.");
		comments.put(DELAY_PER_CAR, "in seconds. Note that this should be scaled MANUALLY with the gridsize!");
		comments.put(MAX_DELAY, "in seconds. Note that this should be scaled MANUALLY with the gridsize!");
		comments.put(TIME_BIN_SIZE, "in seconds");
		comments.put(GRID_SIZE, "in CRS units, usually meters");
		return comments;
	}
	
	@StringGetter(METHOD)
	public CalculationMethod getCalculationMethod() {
		return this.method;
	}
	@StringSetter(METHOD)
	public void setCalculationMethod(CalculationMethod method) {
		this.method = method;
	}
	
	@StringGetter(DELAY_PER_CAR)
	public double getDelayPerCar() {
		return this.delayPerCar;
	}
	@StringSetter(DELAY_PER_CAR)
	public void setDelayPerCar(double delayPerCar) {
		this.delayPerCar = delayPerCar;
	}
	
	@StringGetter(MAX_DELAY)
	public double getMaxDelay() {
		return this.maxDelay;
	}
	@StringSetter(MAX_DELAY)
	public void setMaxDelay(double maxDelay) {
		this.maxDelay = maxDelay;
	}

	@StringGetter(GRID_SIZE)
	public int getGridSize() {
		return gridSize;
	}
	@StringSetter(GRID_SIZE)
	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	@StringGetter(TIME_BIN_SIZE)
	public int getTimeBinSize() {
		return timeBinSize;
	}
	@StringSetter(TIME_BIN_SIZE)
	public void setTimeBinSize(int timeBinSize) {
		this.timeBinSize = timeBinSize;
	}

	@StringGetter(SCALE_FACTOR)
	public int getScenarioScaleFactor() {
		return scenarioScaleFactor;
	}
	@StringSetter(SCALE_FACTOR)
	public void setScenarioScaleFactor(int scenarioScaleFactor) {
		this.scenarioScaleFactor = scenarioScaleFactor;
	}
	
	@StringGetter(CARS_PER_1000_PERSONS)
	public int getCarsPer1000Persons() {
		return carsPer1000Persons;
	}
	@StringSetter(CARS_PER_1000_PERSONS)
	public void setCarsPer1000Persons(int carsPer1000Persons) {
		this.carsPer1000Persons = carsPer1000Persons;
	}
}
