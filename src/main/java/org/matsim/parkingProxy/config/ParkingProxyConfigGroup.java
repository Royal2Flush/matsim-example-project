package org.matsim.parkingProxy.config;

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
	
	private CalculationMethod method = CalculationMethod.none;
	private double delayPerCar = 2.5;
	private double maxDelay = 900;
	private int scenarioScaleFactor = 100;
	private int timeBinSize = 900;
	private int gridSize = 500;

	public ParkingProxyConfigGroup() {
		super(GROUP_NAME);
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
}
