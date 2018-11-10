package com.stanton.i2c.sensor;

import java.io.Serializable;

public class SensorReading implements Serializable{
	private double temp;
	private double pressure;
	
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

}
