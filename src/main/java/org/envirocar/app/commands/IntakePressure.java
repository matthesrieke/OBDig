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

package org.envirocar.app.commands;

import org.envirocar.app.commands.PIDUtil.PID;

/**
 * Intake Manifold Pressure on PID 01 0B
 * 
 * @author jakob
 * 
 */

public class IntakePressure extends NumberResultCommand {

	public static final String NAME = "Intake Manifold Pressure";
	private int pressure = Short.MIN_VALUE;

	public IntakePressure() {
		super("01 ".concat(PID.INTAKE_MAP.toString()));
	}

	@Override
	public String getCommandName() {
		return NAME;
	}


	@Override
	public Number getNumberResult() {
		if (pressure == Short.MIN_VALUE) {
			int[] buffer = getBuffer();
			pressure = buffer[2];
		}
		return pressure;
	}

}