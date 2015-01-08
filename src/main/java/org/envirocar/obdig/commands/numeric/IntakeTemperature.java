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
package org.envirocar.obdig.commands.numeric;

import org.envirocar.obdig.commands.NumberResultCommand;
import org.envirocar.obdig.commands.PIDUtil.PID;

/**
 * Intake temperature on PID 01 0F
 * 
 * @author jakob
 * 
 */
public class IntakeTemperature extends NumberResultCommand {

	public static final String NAME = "Air Intake Temperature";
	private int temperature = Short.MIN_VALUE;

	@Override
	public String getCommandName() {
		return NAME;
	}

	@Override
	public Number getNumberResult() {
		if (temperature == Short.MIN_VALUE) {
			int[] buffer = getBuffer();
			temperature = buffer[2] - 40;
		}
		return temperature;
	}

	@Override
	public String getResponseTypeID() {
		return PID.INTAKE_AIR_TEMP.toString();
	}

}