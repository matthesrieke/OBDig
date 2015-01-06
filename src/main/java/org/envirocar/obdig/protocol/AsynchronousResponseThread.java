/**
 * Copyright (C) ${inceptionYear}-${latestYearOfContribution} 52°North Initiative for Geospatial Open Source
 * Software GmbH
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
package org.envirocar.obdig.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.envirocar.obdig.commands.CommonCommand;
import org.envirocar.obdig.protocol.exception.LooperStoppedException;
import org.envirocar.obdig.protocol.executor.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AsynchronousResponseThread {
	
	private static final Logger logger = LoggerFactory.getLogger(AsynchronousResponseThread.class);
	private static final int MAX_BUFFER_SIZE = 32;
	private CommandExecutor handler;
	private InputStream inputStream;
	
	private Runnable readInputStreamRunnable;
	
	private List<CommonCommand> buffer = new ArrayList<CommonCommand>();
	
	protected boolean running = true;
	private byte[] globalBuffer = new byte[64];
	private int globalIndex;
	private ResponseParser responseParser;

	public AsynchronousResponseThread(final InputStream in, ResponseParser responseParser) {
//		super("AsynchronousResponseThread");
		this.inputStream = in;
		
		this.responseParser = responseParser;
		
		this.readInputStreamRunnable = new Runnable() {
			
			@Override
			public void run() {
				while (running) {
					
					CommonCommand cmd;
					try {
						cmd = readResponse();	
						
						if (cmd != null) {
							synchronized (AsynchronousResponseThread.this) {
								buffer.add(cmd);	
							}	
						}
						
					} catch (IOException e) {
						logger.warn(e.getMessage(), e);
						break;
					}
				}
				
				throw new LooperStoppedException();
			}
		};
	}
	
	private CommonCommand readResponse() throws IOException {
		byte byteIn;
		int intIn;
		while (running) {
			intIn = inputStream.read();
			
			if (intIn < 0) {
				break;
			}

			byteIn = (byte) intIn;
			
			if (byteIn == (byte) responseParser.getEndOfLine()) {
				boolean isReplete = false;
				synchronized (this) {
					/*
					 * are we fed?
					 */
					isReplete = buffer.size() > MAX_BUFFER_SIZE;
				}
				
				CommonCommand result = null;
				if (!isReplete) {
					result = responseParser.processResponse(globalBuffer,
							0, globalIndex);	
				}
				
				globalIndex = 0;
				return result;
			} else {
				globalBuffer[globalIndex++] = byteIn;
			}
		}
		
		return null;
	}
	

//	@Override
//	public void run() {
//		Looper.prepare();
//		handler = new Handler();
//		handler.post(readInputStreamRunnable);
//		try {
//			Looper.loop();
//		} catch (LooperStoppedException e) {
//			logger.info("AsynchronousResponseThread stopped.");
//		}
//	}

	public List<CommonCommand> pullAvailableCommands() {
		List<CommonCommand> result;
		synchronized (this) {
			result = new ArrayList<CommonCommand>(buffer.size());
			result.addAll(buffer);
			buffer.clear();
		}
		return result;
	}

	public void shutdown() {
		logger.info("SHUTDOWN!");
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

	public void join() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}
	
	
}