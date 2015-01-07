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
package org.envirocar.obdig.protocol.sequential;

import java.util.ArrayList;
import java.util.List;

import org.envirocar.obdig.commands.CommonCommand;
import org.envirocar.obdig.commands.StringResultCommand;
import org.envirocar.obdig.commands.control.EchoOff;
import org.envirocar.obdig.commands.control.LineFeedOff;
import org.envirocar.obdig.commands.control.ObdReset;
import org.envirocar.obdig.commands.control.SelectAutoProtocol;
import org.envirocar.obdig.commands.control.Timeout;
import org.envirocar.obdig.protocol.AbstractSequentialConnector;

public class ELM327Connector extends AbstractSequentialConnector {
	
	protected int succesfulCount;

	/*
	 * This is what Torque does:
	 */

	// addCommandToWaitingList(new Defaults());
	// addCommandToWaitingList(new Defaults());
	// addCommandToWaitingList(new ObdReset());
	// addCommandToWaitingList(new ObdReset());
	// addCommandToWaitingList(new EchoOff());
	// addCommandToWaitingList(new EchoOff());
	// addCommandToWaitingList(new EchoOff());
	// addCommandToWaitingList(new MemoryOff());
	// addCommandToWaitingList(new MemoryOff());
	// addCommandToWaitingList(new MemoryOff());
	// addCommandToWaitingList(new MemoryOff());
	// addCommandToWaitingList(new MemoryOff());
	// addCommandToWaitingList(new LineFeedOff());
	// addCommandToWaitingList(new SpacesOff());
	// addCommandToWaitingList(new HeadersOff());
	// addCommandToWaitingList(new Defaults());
	// addCommandToWaitingList(new ObdReset());
	// addCommandToWaitingList(new ObdReset());
	// addCommandToWaitingList(new EchoOff());
	// addCommandToWaitingList(new EchoOff());
	// addCommandToWaitingList(new EchoOff());
	// addCommandToWaitingList(new MemoryOff());
	// addCommandToWaitingList(new MemoryOff());
	// addCommandToWaitingList(new MemoryOff());
	// addCommandToWaitingList(new MemoryOff());
	// addCommandToWaitingList(new MemoryOff());
	// addCommandToWaitingList(new LineFeedOff());
	// addCommandToWaitingList(new SpacesOff());
	// addCommandToWaitingList(new HeadersOff());
	// addCommandToWaitingList(new SelectAutoProtocol());
	// addCommandToWaitingList(new PIDSupported());
	// addCommandToWaitingList(new EnableHeaders());
	// addCommandToWaitingList(new PIDSupported());
	// addCommandToWaitingList(new HeadersOff());

	/*
	 * End Torque
	 */

	@Override
	public List<CommonCommand> getInitializationCommands() {
		List<CommonCommand> result = new ArrayList<CommonCommand>();
		result.add(new ObdReset());
		result.add(new EchoOff());
		result.add(new EchoOff());
		result.add(new LineFeedOff());
		result.add(new Timeout(62));
		result.add(new SelectAutoProtocol());
		return result;
	}

	@Override
	public boolean supportsDevice(String deviceName) {
		return deviceName.contains("OBDII") || deviceName.contains("ELM327");
	}

	@Override
	public void processInitializationCommand(CommonCommand cmd) {
		if (cmd instanceof StringResultCommand) {
			String content = ((StringResultCommand) cmd).getStringResult();
			
			if (cmd instanceof EchoOff) {
				if (content.contains("ELM327v1.")) {
					succesfulCount++;
				}
				else if (content.contains("ATE0") && content.contains("OK")) {
					succesfulCount++;
				}
			}
			
			else if (cmd instanceof LineFeedOff) {
				if (content.contains("OK")) {
					succesfulCount++;
				}
			}
			
			else if (cmd instanceof Timeout) {
				if (content.contains("OK")) {
					succesfulCount++;
				}
			}
			
			else if (cmd instanceof SelectAutoProtocol) {
				if (content.contains("OK")) {
					succesfulCount++;
				}
			}
		}
		
	}

	@Override
	public ConnectionState connectionState() {
		if (succesfulCount >= 5) {
			return ConnectionState.CONNECTED;
		}
		return ConnectionState.DISCONNECTED;
	}

	@Override
	public void shutdown() {
		super.shutdown();
	}

	@Override
	public int getMaximumTriesForInitialization() {
		return 1;
	}

	@Override
	public void prepareShutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getPreferredRequestPeriod() {
		return 100;
	}



}
