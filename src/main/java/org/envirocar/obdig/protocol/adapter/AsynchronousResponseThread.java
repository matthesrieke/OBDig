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
package org.envirocar.obdig.protocol.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.envirocar.obdig.commands.AbstractCommand;
import org.envirocar.obdig.protocol.CommandExecutor;
import org.envirocar.obdig.protocol.exception.LooperStoppedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AsynchronousResponseThread {
	
	private static final Logger logger = LoggerFactory.getLogger(AsynchronousResponseThread.class);
	private static final int MAX_BUFFER_SIZE = 32;
	private CommandExecutor handler;
	private InputStream inputStream;
	
	private Runnable readInputStreamRunnable;
	
	private List<AbstractCommand> buffer = new ArrayList<AbstractCommand>();
	
	protected boolean running = true;
	private byte[] globalBuffer = new byte[64];
	private int globalIndex;
	private ResponseParser responseParser;

	public AsynchronousResponseThread(final InputStream in, ResponseParser responseParser, CommandExecutor executor) {
//		super("AsynchronousResponseThread");
		this.handler = executor;
		this.inputStream = in;
		
		this.responseParser = responseParser;
		
		this.readInputStreamRunnable = new Runnable() {
			
			@Override
			public void run() {
				while (running) {
					
					AbstractCommand cmd;
					try {
						cmd = readResponse();	
						
						if (cmd != null) {
							synchronized (AsynchronousResponseThread.this) {
								buffer.add(cmd);	
							}	
						}
						
					} catch (IOException e) {
						logger.warn(e.getMessage(), e);
						running = false;
						break;
					}
				}
				
				throw new LooperStoppedException();
			}
		};
	}
	
	private AbstractCommand readResponse() throws IOException {
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
				
				AbstractCommand result = null;
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

	public List<AbstractCommand> pullAvailableCommands() {
		List<AbstractCommand> result;
		synchronized (this) {
			result = new ArrayList<AbstractCommand>(buffer.size());
			result.addAll(buffer);
			buffer.clear();
		}
		return result;
	}

	public void shutdown() {
		logger.info("SHUTDOWN!");
		running = false;
		this.handler.removeCallbacks(readInputStreamRunnable);
	}

	public boolean isRunning() {
		return running;
	}

	public void start() {
		this.handler.post(readInputStreamRunnable);
	}

	
}
