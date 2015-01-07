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

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.envirocar.obdig.commands.CommonCommand.CommonCommandState;
import org.envirocar.obdig.commands.PIDUtil.PID;
import org.envirocar.obdig.commands.raw.PIDSupported;
import org.junit.Assert;
import org.junit.Test;

public class PIDSupportedTest {

	@Test
	public void testPIDSupportedParsing() {
		PIDSupported cmd = new PIDSupported();
		
		cmd.parseRawData(createResponseMockup());
		
		Set<PID> result = cmd.getSupportedPIDs();
		
		assertResult(result);
	}
	
	@Test
	public void testPIDSupportedFail() {
		PIDSupported cmd = new PIDSupported();
		
		cmd.parseRawData(createResponseFailMockup());
		
		assertTrue(cmd.getCommandState() == CommonCommandState.EXECUTION_ERROR);
	}

	private void assertResult(Set<PID> result) {
		Set<PID> expected = new HashSet<PID>();
		expected.add(PID.CALCULATED_ENGINE_LOAD);
		expected.add(PID.FUEL_PRESSURE);
		expected.add(PID.INTAKE_AIR_TEMP);
		expected.add(PID.INTAKE_MAP);
		expected.add(PID.MAF);
		expected.add(PID.RPM);
		expected.add(PID.SPEED);
		
		Assert.assertTrue(String.format(Locale.US, "Size is different. Expected %d, Received %d.",
				expected.size(),
				result.size()),
				result.size() == expected.size());
		
		for (PID string : expected) {
			Assert.assertTrue(result.contains(string));
		}
	}

	private byte[] createResponseMockup() {
		StringBuilder sb = new StringBuilder();
		sb.append("4100");
		sb.append("107B0000");
		return sb.toString().getBytes();
	}

	private byte[] createResponseFailMockup() {
		StringBuilder sb = new StringBuilder();
		sb.append("4100");
		sb.append("107B0000");
		sb.append("4100");
		sb.append("107B0000");
		return sb.toString().getBytes();
	}
	
}

