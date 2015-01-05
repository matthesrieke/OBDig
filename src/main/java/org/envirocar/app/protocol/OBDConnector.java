/* 
 * enviroCar 2013
 * Copyright (C) 2013  
 * Martin Dueren, Jakob Moellers, Gerald Pape, Christopher Stephan
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * 
 */
package org.envirocar.app.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.envirocar.app.commands.CommonCommand;
import org.envirocar.app.protocol.exception.AdapterFailedException;
import org.envirocar.app.protocol.exception.ConnectionLostException;
import org.envirocar.app.protocol.exception.UnmatchedCommandResponseException;

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
	public List<CommonCommand> executeRequestCommands() throws IOException,
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


}
