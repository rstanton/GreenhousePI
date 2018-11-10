package com.stanton.i2c.sensor;

import java.io.Serializable;

public class SensorReading implements Serializable{
	private double temp;

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}
}
