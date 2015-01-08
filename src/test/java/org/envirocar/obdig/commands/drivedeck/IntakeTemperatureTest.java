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
package org.envirocar.obdig.commands.drivedeck;

import org.envirocar.obdig.commands.AbstractCommand;
import org.envirocar.obdig.commands.numeric.IntakeTemperature;
import org.envirocar.obdig.protocol.adapter.ResponseParser;
import org.junit.Assert;
import org.junit.Test;

public class IntakeTemperatureTest extends CommandTest {
	
	@Test
	public void testParsing() {
		ResponseParser parser = getResponseParser();
		
		byte[] bytes = createBytes();
		AbstractCommand resp = parser.processResponse(bytes, 0, bytes.length);
		
		Assert.assertTrue(resp != null && resp instanceof IntakeTemperature);
		
		double temp = ((IntakeTemperature) resp).getNumberResult().doubleValue();
		Assert.assertTrue(temp == 23.0);
	}

	private byte[] createBytes() {
		return "B52<?0".getBytes();
	}


}
