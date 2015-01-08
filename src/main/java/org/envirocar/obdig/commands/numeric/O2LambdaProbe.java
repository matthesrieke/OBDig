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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class O2LambdaProbe extends NumberResultCommand {

	private static final Logger logger = LoggerFactory.getLogger(O2LambdaProbe.class);
	private String cylinderPosition;
	private double equivalenceRation = Double.NaN;
	private String pid;

	public static O2LambdaProbe fromPIDEnum(PID pid) {
		switch (pid) {
		case O2_LAMBDA_PROBE_1_VOLTAGE:
			return new O2LambdaProbeVoltage(pid.toString()); 
		case O2_LAMBDA_PROBE_2_VOLTAGE:
			return new O2LambdaProbeVoltage(pid.toString());
		case O2_LAMBDA_PROBE_3_VOLTAGE:
			return new O2LambdaProbeVoltage(pid.toString());
		case O2_LAMBDA_PROBE_4_VOLTAGE:
			return new O2LambdaProbeVoltage(pid.toString());
		case O2_LAMBDA_PROBE_5_VOLTAGE:
			return new O2LambdaProbeVoltage(pid.toString());
		case O2_LAMBDA_PROBE_6_VOLTAGE:
			return new O2LambdaProbeVoltage(pid.toString());
		case O2_LAMBDA_PROBE_7_VOLTAGE:
			return new O2LambdaProbeVoltage(pid.toString());
		case O2_LAMBDA_PROBE_8_VOLTAGE:
			return new O2LambdaProbeVoltage(pid.toString());
		case O2_LAMBDA_PROBE_1_CURRENT:
			return new O2LambdaProbeCurrent(pid.toString());
		case O2_LAMBDA_PROBE_2_CURRENT:
			return new O2LambdaProbeCurrent(pid.toString());
		case O2_LAMBDA_PROBE_3_CURRENT:
			return new O2LambdaProbeCurrent(pid.toString());
		case O2_LAMBDA_PROBE_4_CURRENT:
			return new O2LambdaProbeCurrent(pid.toString());
		case O2_LAMBDA_PROBE_5_CURRENT:
			return new O2LambdaProbeCurrent(pid.toString());
		case O2_LAMBDA_PROBE_6_CURRENT:
			return new O2LambdaProbeCurrent(pid.toString());
		case O2_LAMBDA_PROBE_7_CURRENT:
			return new O2LambdaProbeCurrent(pid.toString());
		case O2_LAMBDA_PROBE_8_CURRENT:
			return new O2LambdaProbeCurrent(pid.toString());
			
		default:
			break;
		}
		
		return null;
	}
	
	public O2LambdaProbe(String cylinderPosition) {
		this.cylinderPosition = cylinderPosition;
		this.pid = cylinderPosition;
	}
	
	@Override
	public Number getNumberResult() {
		//command provides two results
		return null;
	}
	
	@Override
	public void parseRawData(byte[] data) {
		super.parseRawData(data);
		if (getBuffer() == null || getBuffer().length < 6) {
			setCommandState(CommonCommandState.EXECUTION_ERROR);
			logger.warn("The response did not contain the correct expected count: "+
					(getBuffer() == null ? "null" : getBuffer().length));
		}
	}
	
	public double getEquivalenceRatio() {
		if (Double.isNaN(this.equivalenceRation)) {
			int[] data = getBuffer();
			
			this.equivalenceRation = ((data[2]*256d)+data[3])/32768d;
		}
		
		return this.equivalenceRation;
	}
	

	@Override
	public String getCommandName() {
		return "O2 Lambda Probe "+cylinderPosition;
	}

	public String getPID() {
		return pid;
	}
	
	public String lambdaString() {
		return getClass().getSimpleName() +" ("+pid+"): "+getEquivalenceRatio() +"; "+getNumberResult();
	}
	
	@Override
	public String getResponseTypeID() {
		return this.cylinderPosition;
	}
	
}
