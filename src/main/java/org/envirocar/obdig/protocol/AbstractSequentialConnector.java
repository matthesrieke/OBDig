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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.envirocar.obdig.FeatureFlags;
import org.envirocar.obdig.commands.CommonCommand;
import org.envirocar.obdig.commands.PIDUtil;
import org.envirocar.obdig.commands.CommonCommand.CommonCommandState;
import org.envirocar.obdig.commands.PIDUtil.PID;
import org.envirocar.obdig.commands.numeric.EngineLoad;
import org.envirocar.obdig.commands.numeric.IntakePressure;
import org.envirocar.obdig.commands.numeric.IntakeTemperature;
import org.envirocar.obdig.commands.numeric.LongTermTrimBank1;
import org.envirocar.obdig.commands.numeric.MAF;
import org.envirocar.obdig.commands.numeric.O2LambdaProbe;
import org.envirocar.obdig.commands.numeric.RPM;
import org.envirocar.obdig.commands.numeric.ShortTermTrimBank1;
import org.envirocar.obdig.commands.numeric.Speed;
import org.envirocar.obdig.commands.numeric.TPS;
import org.envirocar.obdig.commands.raw.FuelSystemStatus;
import org.envirocar.obdig.commands.raw.PIDSupported;
import org.envirocar.obdig.protocol.exception.AdapterFailedException;
import org.envirocar.obdig.protocol.exception.ConnectionLostException;
import org.envirocar.obdig.protocol.exception.UnmatchedCommandResponseException;
import org.envirocar.obdig.protocol.executor.CommandExecutor;
import org.envirocar.obdig.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class acts as the basis for adapters which work
 * in a request/response fashion (in particular, they do not
 * send out data without an explicit request)
 * 
 * @author matthes rieke
 */
public abstract class AbstractSequentialConnector implements OBDConnector {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractSequentialConnector.class.getName());
	private static final int SLEEP_TIME = 25;
	private static final int MAX_SLEEP_TIME = 5000;
	private static final int MAX_INVALID_RESPONSE_COUNT = 5;
	private static final int MIN_BACKLIST_COUNT = 5;
	private static final int MAX_SEARCHING_COUNT_IN_A_ROW = 10;
	private static Set<String> whitelistedCommandNames = new HashSet<String>();
	
	private InputStream inputStream;
	private OutputStream outputStream;
	private boolean connectionEstablished;
	private boolean staleConnection;
	private int invalidResponseCount;
	private Map<String, AtomicInteger> blacklistCandidates = new HashMap<String, AtomicInteger>();
	private Set<String> blacklistedCommandNames = new HashSet<String>();
	
	private int searchingCountInARow;
	private Set<PID> supportedPIDs;
	private int cycle = 0;
	private String preferredLambdaProbe;
	private ExecutorService initializationExecutor = Executors.newSingleThreadExecutor();
	
	static {
//		whitelistedCommandNames.add(new FuelSystemStatus().getCommandName());
//		whitelistedCommandNames.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_1_VOLTAGE).getCommandName());
	}
	
	/**
	 * @return the list of initialization commands for the adapter
	 */
	protected abstract List<CommonCommand> getInitializationCommands();

	/**
	 * a sub-class shall process the given command and determine
	 * the connection state when a specific set of command results
	 * have been rececived.
	 * 
	 * @param cmd the executed command
	 */
	protected abstract void processInitializationCommand(CommonCommand cmd);
	
	@Override
	public abstract boolean supportsDevice(String deviceName);

	@Override
	public abstract ConnectionState connectionState();

	
	@Override
	public void provideStreamObjects(InputStream inputStream,
			OutputStream outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}
	
	protected List<CommonCommand> getRequestCommands() {
		List<CommonCommand> requestCommands;
		if (supportedPIDs != null && supportedPIDs.size() != 0) {
			requestCommands = new ArrayList<CommonCommand>();
			for (PID pid : supportedPIDs) {
				CommonCommand cmd = PIDUtil.instantiateCommand(pid);
				if (cmd != null) {
					requestCommands.add(cmd);
				}
			}
			
			logger.info("PID supported result: "+requestCommands);
		} else {
			requestCommands = new ArrayList<CommonCommand>();
			requestCommands.add(new Speed());
			requestCommands.add(new MAF());
			requestCommands.add(new RPM());
			requestCommands.add(new IntakePressure());
			requestCommands.add(new IntakeTemperature());
			requestCommands.add(new EngineLoad());
			requestCommands.add(new TPS());
		}
		
		/*
		 * XXX: Tryout for Lambda probes. better: do via PIDSupported
		 * 
		 */
		if (this.preferredLambdaProbe == null || this.preferredLambdaProbe.isEmpty()) {
			if (cycle % 8 == 0) {
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_1_VOLTAGE));
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_1_CURRENT));	
			}
			else if (cycle % 8 == 1) {
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_2_VOLTAGE));
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_2_CURRENT));
			}
			else if (cycle % 8 == 2) {
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_3_VOLTAGE));
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_3_CURRENT));
			}
			else if (cycle % 8 == 3) {
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_4_VOLTAGE));
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_4_CURRENT));
			}
			else if (cycle % 8 == 4) {
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_5_VOLTAGE));
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_5_CURRENT));
			}
			else if (cycle % 8 == 5) {
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_6_VOLTAGE));
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_6_CURRENT));
			}
			else if (cycle % 8 == 6) {
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_7_VOLTAGE));
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_7_CURRENT));
			}
			else if (cycle % 8 == 7) {
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_8_VOLTAGE));
				requestCommands.add(O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_8_CURRENT));
			}
		}
		else {
			/*
			 * we got one positive response, use that
			 */
			requestCommands.add(O2LambdaProbe.fromPIDEnum(PIDUtil.fromString(preferredLambdaProbe)));
		}
		
		
		requestCommands.add(new FuelSystemStatus());
		requestCommands.add(new ShortTermTrimBank1());
		requestCommands.add(new LongTermTrimBank1());
		
		cycle++;
		
		return requestCommands;
	}

	private void onInitializationCommand(CommonCommand cmd) {
		if (cmd instanceof PIDSupported && FeatureFlags.usePIDSupported()) {
			if (!(cmd.getCommandState() == CommonCommandState.EXECUTION_ERROR)) {
				this.supportedPIDs = ((PIDSupported) cmd).getSupportedPIDs();
			}
		}
		processInitializationCommand(cmd);
	}
	
	private void onBlacklistCandidate(CommonCommand cmd) {
		String name = cmd.getCommandName();
		
		if (blacklistedCommandNames.contains(name)) return;
		
		/*
		 * whiteliste, basically for testing via user study
		 */
		if (whitelistedCommandNames.contains(name)) return;
		
		AtomicInteger candidate = blacklistCandidates.get(name);
		
		if (candidate != null) {
			int count = candidate.incrementAndGet();
			if (count > MIN_BACKLIST_COUNT) {
				logger.info("Blacklisting command: "+name);
				blacklistedCommandNames.add(name);
			}
		}
		else {
			blacklistCandidates.put(name, new AtomicInteger(0));
		}
	}
	

	/**
	 * "Run" the provided command. This includes sending (writing bytes to stream)
	 * and reading and parsing the response stream.
	 * 
	 * @param cmd
	 * @throws IOException
	 */
	private void runCommand(CommonCommand cmd)
			throws IOException {
		logger.debug("Sending command " +cmd.getCommandName()+ " / "+ new String(cmd.getOutgoingBytes()));
		
		try {
			sendCommand(cmd);	
		} catch (RuntimeException e) {
			logger.warn("Error while sending command '" + cmd.toString() + "': "+e.getMessage(), e);
			cmd.setCommandState(CommonCommandState.EXECUTION_ERROR);
			return;
		}
		
		if (!cmd.awaitsResults()) return; 
		
		// waiting with InputStream#available() does not work on all devices (and cars?!)
//		waitForResult(cmd);
		
		try {
			readResult(cmd);	
		} catch (RuntimeException e) {
			logger.warn("Error while sending command '" + cmd.toString() + "': "+e.getMessage(), e);
			cmd.setCommandState(CommonCommandState.EXECUTION_ERROR);
		}
	}
	
	
	/**
	 * Sends the OBD-II request.
	 * 
	 * This method may be overriden in subclasses, such as ObMultiCommand or
	 * TroubleCodesObdCommand.
	 * 
	 * @param cmd
	 *            The command to send.
	 */
	private void sendCommand(CommonCommand cmd) throws IOException {
		// write to OutputStream, or in this case a BluetoothSocket
		outputStream.write(cmd.getOutgoingBytes());
		outputStream.write(cmd.getEndOfLineSend());
		outputStream.flush();
	}
	
	/**
	 * @deprecated some devices (and cars?!) do not implement #available()
	 * reliably
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void waitForResult(CommonCommand cmd) throws IOException {
//		try {
//			Thread.sleep(SLEEP_TIME);
//		} catch (InterruptedException e) {
//			logger.warn(e.getMessage(), e);
//		}
		
		if (!cmd.awaitsResults()) return; 
		try {
			int tries = 0;
			while (inputStream.available() <= 0) {
				if (tries++ * getSleepTime() > getMaxTimeout()) {
					if (cmd.responseAlwaysRequired()) {
						throw new IOException("OBD-II Request Timeout of "+ getMaxTimeout() +" ms exceeded.");
					}
					else {
						return;
					}
				}
				
				Thread.sleep(getSleepTime());
			}
			
		} catch (InterruptedException e) {
			logger.warn(e.getMessage(), e);
		}	
	}
	
	/**
	 * Reads the OBD-II response and parse it afterwards.
	 * @param cmd 
	 */
	private void readResult(CommonCommand cmd) throws IOException {
		byte[] rawData = readResponseLine(cmd);
		cmd.setResultTime(System.currentTimeMillis());

		// read string each two chars
		cmd.parseRawData(rawData);
	}

	private byte[] readResponseLine(CommonCommand cmd) throws IOException {
		byte b = 0;

		Set<Character> ignored = cmd.getIgnoredChars();
		
		byte[] buffer = new byte[272];
		int index = 0;
		// read until '>' arrives
		while (index < buffer.length && (char) (b = (byte) inputStream.read()) != cmd.getEndOfLineReceive()) {
			if (!ignored.contains((char) b)){
				buffer[index++] = b;
			}
		}
		
		if (index > 0) {
			logger.debug("Response read. Data (base64): "+
					Base64.encodeBytes(buffer, 0, index));
		}

		return Arrays.copyOf(buffer, index);
	}


	public int getMaxTimeout() {
		return MAX_SLEEP_TIME;
	}

	public int getSleepTime() {
		return SLEEP_TIME;
	}
	
	@Override
	public void executeInitializationCommands() throws IOException, AdapterFailedException {
		final List<CommonCommand> cmds = this.getInitializationCommands();
		
		if (initializationExecutor.isShutdown() || initializationExecutor.isTerminated()) {
			throw new AdapterFailedException(getClass().getSimpleName());
		}
		
		Future<Boolean> future;
		try {
			future = initializationExecutor.submit(new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					try {
						executeCommands(cmds);
						executeCommand(new PIDSupported());
						return true;
					} catch (UnmatchedCommandResponseException e) {
						logger.warn("This should never happen!", e);
					} catch (ConnectionLostException e) {
						logger.warn("This should never happen!", e);
					}
					return false;
				}
			});			
		}
		catch (RejectedExecutionException e) {
			throw new AdapterFailedException(getClass().getSimpleName(), e);
		}
		
		try {
			Boolean resp = future.get(10, TimeUnit.SECONDS);
			
			if (!resp.booleanValue()) {
				throw new AdapterFailedException("Init commands took too long.");
			}
			
		} catch (InterruptedException e) {
			throw new AdapterFailedException(e.getMessage());
		} catch (ExecutionException e) {
			throw new AdapterFailedException(e.getMessage());
		} catch (TimeoutException e) {
			throw new AdapterFailedException(e.getMessage());
		}
		
	}
	
	@Override
	public List<CommonCommand> executeRequestCommands() throws IOException, AdapterFailedException, ConnectionLostException {
		List<CommonCommand> list = getRequestCommands();
		
		for (CommonCommand cmd : list) {
			if (blacklistedCommandNames.contains(cmd.getCommandName())) {
				/*
				 * we have received enough failed responses for this command
				 */
				continue;
			}
			
			try {
				executeCommand(cmd);
			} catch (UnmatchedCommandResponseException e) {
				logger.warn("Unmatched Response detected! trying to read another line.");
				readResponseLine(cmd);
			}
			
			/*
			 * check if we got a positive response from a Lambda probe request
			 */
			if (cmd.getCommandState() == CommonCommandState.FINISHED) {
				evaluateSupportedLambdaCommand(cmd);
			}
		}
		
		return list;
	}
	
	private void evaluateSupportedLambdaCommand(CommonCommand cmd) {
		if (this.preferredLambdaProbe != null && !this.preferredLambdaProbe.isEmpty()) {
			/*
			 * no action required, we already got what we want
			 */
			return;
		}
		
		if (cmd instanceof O2LambdaProbe) {
			this.preferredLambdaProbe = ((O2LambdaProbe) cmd).getPID();
		}
	}

	/**
	 * Execute a list of commands
	 * 
	 * @throws AdapterFailedException if the adapter could not establish a connection
	 * @throws IOException if an exception occurred while accessing the stream objects
	 * @throws UnmatchedCommandResponseException if the response did not match the requested command
	 * @throws ConnectionLostException if the maximum number of unmatched responses exceeded
	 */
	private void executeCommands(List<CommonCommand> cmds) throws AdapterFailedException, IOException, UnmatchedCommandResponseException, ConnectionLostException {
		for (CommonCommand c : cmds) {
			executeCommand(c);
		}
	}

	/**
	 * Execute one command using the given stream objects
	 * 
	 * @throws AdapterFailedException if the adapter could not establish a connection
	 * @throws IOException if an exception occurred while accessing the stream objects
	 * @throws UnmatchedCommandResponseException if the response did not match the requested command
	 * @throws ConnectionLostException if the maximum number of unmatched responses exceeded
	 */
	private void executeCommand(CommonCommand cmd) throws AdapterFailedException, IOException, UnmatchedCommandResponseException, ConnectionLostException {
		try {
			if (cmd.getCommandState().equals(CommonCommandState.NEW)) {

				/*
				 * a command with state NEW shall be run (= bytes written to the stream).
				 */
				cmd.setCommandState(CommonCommandState.RUNNING);
				runCommand(cmd);
			}
		} catch (IOException e) {
			if (!connectionEstablished) {
				/*
				 * lets first try a different adapter before we fail!
				 */
				logger.warn(e.getMessage(), e);
				throw new AdapterFailedException(getClass().getName());
			} else {
				throw e;
			}
		}

		if (cmd != null) {
			if (!cmd.awaitsResults()) return;
			
			switch (cmd.getCommandState()) {
			case FINISHED:
				if (!connectionEstablished) {
					onInitializationCommand(cmd);
					if (connectionState() == ConnectionState.CONNECTED) {
						connectionEstablished = true;
					}
				}
				else {
					if (cmd instanceof PIDSupported) {
						onInitializationCommand(cmd);
					}
					if (staleConnection) {
						staleConnection = false;
						invalidResponseCount = 0;
						searchingCountInARow = 0;
					}
				}
				break;
			case EXECUTION_ERROR:
				String raw = cmd.getRawData() == null ? "null" : new String(cmd.getRawData());
				logger.debug("Execution Error for " +cmd.getCommandName() +": "+raw);
				this.onBlacklistCandidate(cmd);
				break;
				
			case SEARCHING:
				logger.info("Adapter searching. Continuing. Response for " +cmd.getCommandName() +": "+new String(cmd.getRawData()));
				staleConnection = true;
				
				if (searchingCountInARow++ > MAX_SEARCHING_COUNT_IN_A_ROW) {
					throw new ConnectionLostException("Adapter is SEARCHING mode for too long.");
				}
				
				break;
			case UNMATCHED_RESULT:
				logger.warn("Did not receive the expected result! Expected: "+cmd.getResponseTypeID());
				
				if (staleConnection && invalidResponseCount++ > MAX_INVALID_RESPONSE_COUNT) {
					throw new ConnectionLostException("Received too many unmatched responses.");
				}
				else {
					staleConnection = true;
					throw new UnmatchedCommandResponseException();	
				}
			default:
				break;
			}
			
		}
		
	}
	
	@Override
	public void startExecutions(CommandExecutor exec) {
	}
	
	@Override
	public void shutdown() {
		if (initializationExecutor != null) {
			initializationExecutor.shutdown();
		}
	}

	
}
