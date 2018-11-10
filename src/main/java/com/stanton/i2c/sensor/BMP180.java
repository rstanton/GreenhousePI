package com.stanton.i2c.sensor;

import java.util.logging.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class BMP180 {
	private IQueue<SensorReading> queue;
	
	public BMP180(Config config) {
		HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);
        IQueue<SensorReading> queue = hz.getQueue( "greenhouse" );
	}
	
	public void read() {
		try {
			I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
			I2CDevice device = bus.getDevice(0x77);
			
			
			// Calibration Cofficients stored in EEPROM of the device
			// Read 22 bytes of data from address 0xAA(170)
			byte[] data = new byte[22];
			device.read(0xAA, data, 0, 22);
	 
			// Convert the data
			//int AC1 = data[0] * 256 + data[1];
			int AC1 = data[0] * 256 + data[1];
			int AC2 = data[2] * 256 + data[3];
			int AC3 = data[4] * 256 + data[5];
			int AC4 = ((data[6] & 0xFF) * 256) + (data[7] & 0xFF);
			int AC5 = ((data[8] & 0xFF) * 256) + (data[9] & 0xFF);
			int AC6 = ((data[10] & 0xFF) * 256) + (data[11] & 0xFF);
			int B1 = data[12] * 256 + data[13];
			int B2 = data[14] * 256 + data[15];
			int MB = data[16] * 256 + data[17];
			int MC = data[18] * 256 + data[19];
			int MD = data[20] * 256 + data[21];
			
			Thread.sleep(500);
	 
			// Select measurement control register
			// Enable temperature measurement
			device.write(0xF4, (byte)0x2E);
			Thread.sleep(100);
	 
			// Read 2 bytes of data from address 0xF6(246)
			// temp msb, temp lsb
			device.read(0xF6, data, 0, 2);
	 
			// Convert the data
			int temp = ((data[0] & 0xFF) * 256 + (data[1] & 0xFF));
			
			// Select measurement control register
			// Enable pressure measurement, OSS = 1
			device.write(0xF4, (byte)0x74);
			Thread.sleep(100);
	 
			// Read 3 bytes of data from address 0xF6(246)
			// pres msb1, pres msb, pres lsb
			device.read(0xF6, data, 0, 3);
	 
			// Convert the data
			double pres = (((data[0] & 0xFF) * 65536) + ((data[1] & 0xFF) * 256) + (data[2] & 0xFF)) / 128;
	 
			// Callibration for Temperature
			double X1 = (temp - AC6) * AC5 / 32768.0;
			double X2 = (MC * 2048.0) / (X1 + MD);
			double B5 = X1 + X2;
			
			// temperature formula from spec = ((B5+8)/2^4)/10
			double cTemp = ((B5 + 8.0) / 16.0) / 10.0;
			double fTemp = cTemp * 1.8 + 32;
	 
			// Calibration for Pressure
			double B6 = B5 - 4000;
			X1 = (B2 * (B6 * B6 / 4096.0)) / 2048.0;
			X2 = AC2 * B6 / 2048.0;
			double X3 = X1 + X2;
			double B3 = (((AC1 * 4 + X3) * 2) + 2) / 4.0;
			X1 = AC3 * B6 / 8192.0;
			X2 = (B1 * (B6 * B6 / 2048.0)) / 65536.0;
			X3 = ((X1 + X2) + 2) / 4.0;
			double B4 = AC4 * (X3 + 32768) / 32768.0;
			double B7 = ((pres - B3) * (25000.0));
			double pressure = 0.0;
			if(B7 < 2147483648L){
				pressure = (B7 * 2) / B4;
			}
			else
			{
				pressure = (B7 / B4) * 2;
			}
			X1 = (pressure / 256.0) * (pressure / 256.0);
			X1 = (X1 * 3038.0) / 65536.0;
			X2 = ((-7357) * pressure) / 65536.0;
			pressure = (pressure + (X1 + X2 + 3791) / 16.0) / 100;
	 
			// Calculate Altitude
			double altitude = 44330 * (1 - Math.pow((pressure / 1013.25), 0.1903));
	 
			// Output data to screen
			System.out.printf("Temperature in Celsius : %.2f C %n", cTemp);
			
			SensorReading reading = new SensorReading();
			reading.setTemp(cTemp);
			
	        if(queue!=null)
	        	queue.put(reading);
	        else
	        	Logger.getLogger("test").warning("Queue is null");
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
