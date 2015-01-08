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
import java.io.OutputStream;
import java.util.List;

import org.envirocar.obdig.commands.AbstractCommand;
import org.envirocar.obdig.protocol.CommandExecutor;
import org.envirocar.obdig.protocol.exception.AdapterFailedException;
import org.envirocar.obdig.protocol.exception.ConnectionLostException;
import org.envirocar.obdig.protocol.exception.UnmatchedCommandResponseException;

/**
 * Interface for a OBD connector. It can provide device specific
 * command requests and initialization sequences.
 * 
 * @author matthes rieke
 *
 */
public interface OBDConnector {

	
	public enum ConnectionState {
		
		/**
		 * used to indicate a state when the connector could
		 * not understand any response received
		 */
		DISCONNECTED,
		
		/**
		 * used to indicate a state when the connector understood
		 * at least one command. Return this state only if the
		 * adapter is sure, that it can interact with the device
		 * - but the device yet did not return measurements
		 */
		CONNECTED,
		
		/**
		 * used to indicate a state where the connector received
		 * a parseable measurement
		 */
		VERIFIED
	}
	
	/**
	 * provide the required stream objects to send and retrieve
	 * commands.
	 * 
	 * An implementation shall synchronize on the inputMutex
	 * when accessing the streams.
	 * 
	 * @param inputStream
	 * @param outputStream
	 */
	public void provideStreamObjects(InputStream inputStream,
			OutputStream outputStream);

	/**
	 * An implementation shall return true if it 
	 * might support the given bluetooth device.
	 * 
	 * @param deviceName the bluetooth device name
	 * @return if it suggests support for the device
	 */
	public boolean supportsDevice(String deviceName);

	/**
	 * @return true if the implementation established a meaningful connection
	 */
	public ConnectionState connectionState();

	/**
	 * an implementation shall use this method to initialize the connection
	 * to the underlying obd adapter
	 * 
	 * @throws IOException if an exception occurred while accessing the stream objects
	 * @throws AdapterFailedException if the adapter could not establish a connection
	 */
	public void executeInitializationCommands() throws IOException,
			AdapterFailedException;

	/**
	 * an implementation shall execute the commands to retrieve
	 * the common phenomena
	 * 
	 * @return the parsed command responses
	 * @throws IOException if an exception occurred while accessing the stream objects
	 * @throws AdapterFailedException if the adapter could not establish a connection
	 * @throws UnmatchedCommandResponseException if the response did not match the requested command
	 * @throws ConnectionLostException if the maximum number of unmatched responses exceeded
	 */
	public List<AbstractCommand> executeRequestCommands() throws IOException,
			AdapterFailedException, ConnectionLostException;

	
	/**
	 * an implementation shall prepare the freeing of resources (e.g. set running flags to false)
	 */
	public void prepareShutdown();
	
	/**
	 * an implementation shall free all resources it has created (e.g. threads)
	 */
	public void shutdown();

	/**
	 * @return the number of maximum tries an adapter sends out
	 * the initial set of commands
	 */
	public int getMaximumTriesForInitialization();

	/**
	 * @return the time in ms the looper should wait between executing the command batch
	 */
	public long getPreferredRequestPeriod();

	/**
	 * an implementation shall initiate the retrieval of OBD data and
	 * continue doing so until {@link #shutdown()} has been called.
	 * @param exec the executor instance to process the OBD data
	 */
	public void startExecutions(CommandExecutor exec);


}
