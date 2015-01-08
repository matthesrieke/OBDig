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
package org.envirocar.obdig.protocol.adapter.sequential;

import java.util.ArrayList;
import java.util.List;

import org.envirocar.obdig.commands.CommonCommand;
import org.envirocar.obdig.commands.StringResultCommand;
import org.envirocar.obdig.commands.control.EchoOff;
import org.envirocar.obdig.commands.control.LineFeedOff;
import org.envirocar.obdig.commands.control.ObdReset;
import org.envirocar.obdig.commands.control.SelectAutoProtocol;
import org.envirocar.obdig.commands.control.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AposW3Connector extends ELM327Connector {
	
	private static final Logger logger = LoggerFactory.getLogger(AposW3Connector.class);

	@Override
	public List<CommonCommand> getInitializationCommands() {
		List<CommonCommand> result = new ArrayList<CommonCommand>();
		result.add(new ObdReset());
		result.add(new AposEchoOff());
		result.add(new AposEchoOff());
		result.add(new LineFeedOff());
		result.add(new Timeout(62));
		result.add(new SelectAutoProtocol());
		return result;
	}

	@Override
	public boolean supportsDevice(String deviceName) {
		return deviceName.contains("APOS") && deviceName.contains("OBD_W3");
	}

	@Override
	public void processInitializationCommand(CommonCommand cmd) {

		if (cmd instanceof AposEchoOff) {
			if (cmd instanceof StringResultCommand) {
				String content = ((StringResultCommand) cmd).getStringResult();
				if (content.contains("OK")) {
					succesfulCount++;
				}
			}
		} else {
			super.processInitializationCommand(cmd);
		}

	}

	@Override
	public ConnectionState connectionState() {
		if (succesfulCount >= 4) {
			return ConnectionState.CONNECTED;
		}
		return ConnectionState.DISCONNECTED;
	}

	private static class AposEchoOff extends EchoOff {
		
		@Override
		public byte[] getOutgoingBytes() {
			try {
				/*
				 * hack for too fast init requests,
				 * issue observed with Galaxy Nexus (4.3) and VW Tiguan 2013
				 */
				Thread.sleep(250);
			} catch (InterruptedException e) {
				logger.warn(e.getMessage(), e);
			}
			return super.getOutgoingBytes();
		}

		@Override
		public boolean responseAlwaysRequired() {
			return false;
		}
		
	}
}
