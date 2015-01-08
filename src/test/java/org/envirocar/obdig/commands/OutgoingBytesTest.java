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
package org.envirocar.obdig.commands;

import org.envirocar.obdig.commands.PIDUtil.PID;
import org.envirocar.obdig.commands.numeric.EngineLoad;
import org.envirocar.obdig.commands.numeric.IntakePressure;
import org.envirocar.obdig.commands.numeric.IntakeTemperature;
import org.envirocar.obdig.commands.numeric.MAF;
import org.envirocar.obdig.commands.numeric.RPM;
import org.envirocar.obdig.commands.numeric.Speed;
import org.envirocar.obdig.commands.numeric.TPS;
import org.junit.Assert;
import org.junit.Test;

public class OutgoingBytesTest {

	@Test
	public void testEngineLoad() {
		EngineLoad s = new EngineLoad();
		byte[] b = s.getOutgoingBytes();
		Assert.assertTrue(new String(b).equals("01 ".concat(PID.CALCULATED_ENGINE_LOAD.toString())));
	}
	
	@Test
	public void testIntakePressure() {
		IntakePressure s = new IntakePressure();
		byte[] b = s.getOutgoingBytes();
		Assert.assertTrue(new String(b).equals("01 ".concat(PID.INTAKE_MAP.toString())));
	}
	
	@Test
	public void testIntakeTemp() {
		IntakeTemperature s = new IntakeTemperature();
		byte[] b = s.getOutgoingBytes();
		Assert.assertTrue(new String(b).equals("01 ".concat(PID.INTAKE_AIR_TEMP.toString())));
	}
	
	@Test
	public void testMAF() {
		MAF s = new MAF();
		byte[] b = s.getOutgoingBytes();
		Assert.assertTrue(new String(b).equals("01 ".concat(PID.MAF.toString())));
	}
	
	@Test
	public void testRPM() {
		RPM s = new RPM();
		byte[] b = s.getOutgoingBytes();
		Assert.assertTrue(new String(b).equals("01 ".concat(PID.RPM.toString())));
	}
	
	@Test
	public void testSpeed() {
		Speed s = new Speed();
		byte[] b = s.getOutgoingBytes();
		Assert.assertTrue(new String(b).equals("01 ".concat(PID.SPEED.toString())));
	}
	
	@Test
	public void testTPS() {
		TPS s = new TPS();
		byte[] b = s.getOutgoingBytes();
		Assert.assertTrue(new String(b).equals("01 ".concat(PID.TPS.toString())));
	}
	
}
