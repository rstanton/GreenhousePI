package com.stanton.i2c.sensor;

import java.io.Serializable;

public class SensorReading implements Serializable{
	private double temp;
	private double pressure;
	private double voltage;

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public double getPressure() {
		return pressure;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	public double getVoltage() {
		return voltage;
	}

	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}
}
