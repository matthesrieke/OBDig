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
package org.envirocar.obdig.commands;

import java.math.BigDecimal;

import org.envirocar.obdig.commands.O2LambdaProbe;
import org.envirocar.obdig.commands.O2LambdaProbeCurrent;
import org.envirocar.obdig.commands.O2LambdaProbeVoltage;
import org.envirocar.obdig.commands.PIDUtil;
import org.envirocar.obdig.commands.PIDUtil.PID;
import org.junit.Assert;
import org.junit.Test;

public class O2LambdaProbeTest {
	
	@Test
	public void testVoltageParsing() {
		O2LambdaProbeVoltage cmd = (O2LambdaProbeVoltage) PIDUtil.instantiateCommand(PID.O2_LAMBDA_PROBE_1_VOLTAGE);
		
		cmd.setRawData(createDataVoltage());
		cmd.parseRawData();
		
		BigDecimal er = BigDecimal.valueOf(cmd.getEquivalenceRatio()).setScale(2, BigDecimal.ROUND_HALF_UP);
		Assert.assertTrue("Expected equivalence ration of 1.52.", er.doubleValue() == 1.52);
		
		BigDecimal v = BigDecimal.valueOf(cmd.getVoltage()).setScale(2, BigDecimal.ROUND_HALF_UP);
		Assert.assertTrue("Expected voltage of 7.5.", v.doubleValue() == 6.08);
	}
	
	@Test
	public void testCurrentParsing() {
		O2LambdaProbeCurrent cmd = (O2LambdaProbeCurrent) O2LambdaProbe.fromPIDEnum(PID.O2_LAMBDA_PROBE_1_CURRENT);
		
		cmd.setRawData(createDataCurrent());
		cmd.parseRawData();
		
		BigDecimal er = BigDecimal.valueOf(cmd.getEquivalenceRatio()).setScale(2, BigDecimal.ROUND_HALF_UP);
		Assert.assertTrue("Expected equivalence ration of 1.52.", er.doubleValue() == 1.52);
		
		BigDecimal c = BigDecimal.valueOf(cmd.getCurrent()).setScale(2, BigDecimal.ROUND_HALF_UP);
		Assert.assertTrue("Expected current of 128.", c.doubleValue() == 2.5);
	}

	private byte[] createDataCurrent() {
		return "4134C2908280".getBytes();
	}

	private byte[] createDataVoltage() {
		return "4124C290C290".getBytes();
	}

}
