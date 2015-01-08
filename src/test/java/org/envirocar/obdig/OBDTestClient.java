/**
 * Copyright (C) 2014 - 2015 the enviroCar development team (envirocar.org)
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.envirocar.obdig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import org.envirocar.obdig.commands.AbstractCommand;
import org.envirocar.obdig.commands.NumberResultCommand;
import org.envirocar.obdig.protocol.CommandExecutor;
import org.envirocar.obdig.protocol.ConnectionListener;
import org.envirocar.obdig.protocol.DataListener;
import org.envirocar.obdig.protocol.OBDCommandLooper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple OBD client, requesting some data.
 */
public class OBDTestClient implements DiscoveryListener {

	private static final Logger logger = LoggerFactory.getLogger(OBDTestClient.class);
	
	private static final String DEFAULT_DEVICE = "1CAF0514A493";
	private static final int DEFAULT_CHANNEL = 4;
	
	private static Object waitMutex = new Object();
	private static String deviceURL = "btspp://%s:%s;authenticate=false;encrypt=false";

	public static void main(String[] args) throws IOException, InterruptedException {
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		logger.info("Local Device Address: "+localDevice.getBluetoothAddress());
		logger.info("Local Device Name: "+localDevice.getFriendlyName());

		String device = resolveOBDDevice();
		int channel = resolveChannel(device);
		
		//connect to the OBD device
		StreamConnection streamConnection = (StreamConnection) Connector.open(
				String.format(deviceURL, device, channel));

		OutputStream outStream = streamConnection.openOutputStream();
		final InputStream inStream = streamConnection.openInputStream();

		//initiate the looper
		OBDCommandLooper looper = new OBDCommandLooper(inStream, outStream, "1CAF0514A493", new LocalListener(), new ConnectionListener() {
			
			@Override
			public void requestConnectionRetry(IOException reason) {
				logger.warn("requestConnectionRetry: "+reason.getMessage());
			}
			
			@Override
			public void onStatusUpdate(String message) {
				logger.info("onStatusUpdate");				
			}
			
			@Override
			public void onConnectionVerified() {
				logger.info("onConnectionVerified");				
			}
			
			@Override
			public void onAllAdaptersFailed() {
				logger.warn("onAllAdaptersFailed");				
			}
		});
		
		looper.initialize(new LocalExecutor());
		
		//do it 10 seconds
		Thread.sleep(10000);
		
		looper.stopLooper();
		streamConnection.close();
	}

	private static int resolveChannel(String device) {
		return DEFAULT_CHANNEL;
	}

	private static String resolveOBDDevice() {
		return DEFAULT_DEVICE;
	}

	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		logger.info("Device discovered: "+ servRecord[0].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
		if (servRecord != null && servRecord.length > 0) {
			deviceURL = servRecord[0].getConnectionURL(ServiceRecord.AUTHENTICATE_ENCRYPT, false);
		}
		synchronized (waitMutex) {
			waitMutex.notify();
		}
	}

	public void serviceSearchCompleted(int transID, int respCode) {
		synchronized (waitMutex) {
			waitMutex.notify();
		}
	}


	public void inquiryCompleted(int discType) {
		synchronized (waitMutex ){
			waitMutex.notify();
		}

	}

	@Override
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		//not used atm
	}
	
	public static class LocalListener implements DataListener {

		@Override
		public void receiveUpdate(AbstractCommand currentJob) {
			String result;
			if (currentJob instanceof NumberResultCommand) {
				result = ((NumberResultCommand) currentJob).getNumberResult().toString();
			}
			else {
				result = new String(currentJob.getRawData());
			}
			
			logger.info(currentJob.getCommandName()+": "+result);
		}

		@Override
		public void shutdown() {
			
		}

		@Override
		public void onConnected(String deviceName) {
			logger.info("Device is ready: "+deviceName);
		}
		
	}
	
	public static class LocalExecutor implements CommandExecutor {
		
		ExecutorService exec = Executors.newFixedThreadPool(2);

		@Override
		public void postDelayed(final Runnable r, final long delayPeriod) {
			exec.submit(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(delayPeriod);
					} catch (InterruptedException e) {
						logger.warn(e.getMessage(), e);
					}
					r.run();
				}
			});
		}

		@Override
		public void removeCallbacks(Runnable r) {
			
		}

		@Override
		public void post(Runnable r) {
			exec.submit(r);
		}

		@Override
		public void shutdownExecutions() {
			exec.shutdown();
		}
		
	}

}