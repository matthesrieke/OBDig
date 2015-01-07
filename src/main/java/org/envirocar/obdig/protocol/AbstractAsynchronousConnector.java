/**
 * Copyright (C) 2014 - 2015 52°North Initiative for Geospatial Open Source
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
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import org.envirocar.obdig.commands.CommonCommand;
import org.envirocar.obdig.protocol.exception.AdapterFailedException;
import org.envirocar.obdig.protocol.exception.ConnectionLostException;
import org.envirocar.obdig.protocol.executor.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAsynchronousConnector implements OBDConnector {

	private static final Logger logger = LoggerFactory.getLogger(AbstractAsynchronousConnector.class);
	private InputStream inputStream;
	private OutputStream outputStream;
	private AsynchronousResponseThread responseThread;
	private CommandExecutor executor;

	protected abstract List<CommonCommand> getRequestCommands();

	protected abstract List<CommonCommand> getInitializationCommands();

	protected abstract char getRequestEndOfLine();
	
	protected abstract ResponseParser getResponseParser();
	
	protected abstract long getSleepTimeBetweenCommands();
	
	public AbstractAsynchronousConnector() {
	}
	
	@Override
	public void startExecutions(CommandExecutor exec) {
		this.executor = exec;
		startResponseThread();		
	}
	
	@Override
	public void provideStreamObjects(InputStream inputStream,
			OutputStream outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}
	
	@Override
	public void executeInitializationCommands() throws IOException,
			AdapterFailedException {
		for (CommonCommand cmd : getInitializationCommands()) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				logger.warn(e.getMessage(), e);
			}
			
			executeCommand(cmd);
		}
	}


	@Override
	public List<CommonCommand> executeRequestCommands() throws IOException,
			AdapterFailedException, ConnectionLostException {
		long sleep = getSleepTimeBetweenCommands();
		for (CommonCommand cmd : getRequestCommands()) {
			executeCommand(cmd);
			
			if (sleep > 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
		
		if (responseThread != null) {
			if (responseThread.isRunning()) {
				return responseThread.pullAvailableCommands();
			}
			else {
				throw new ConnectionLostException("ResponseThread has been shutdown");
			}
		}
		return Collections.emptyList();
	}

	private void executeCommand(CommonCommand cmd) throws IOException {
		logger.debug("Sending command: "+cmd.getCommandName());
		
		byte[] bytes = cmd.getOutgoingBytes();
		if (bytes != null && bytes.length > 0) {
			outputStream.write(bytes);
		}
		outputStream.write(getRequestEndOfLine());
		outputStream.flush();		
	}

	@Override
	public void prepareShutdown() {
		if (responseThread != null) {
			responseThread.shutdown();			
		}
	}
	
	@Override
	public void shutdown() {
		if (responseThread != null) {
			responseThread.shutdown();
		}		
	}
	
	protected void startResponseThread() {
		if (responseThread == null || !responseThread.isRunning()) {
			responseThread = new AsynchronousResponseThread(inputStream, getResponseParser(), this.executor);
			responseThread.start();
		}
	}


}
