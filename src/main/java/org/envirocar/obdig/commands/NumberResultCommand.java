/**
 * Copyright (C) ${inceptionYear}-${latestYearOfContribution} 52°North Initiative for Geospatial Open Source
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

public abstract class NumberResultCommand extends CommonCommand {

	private static final CharSequence SEARCHING = "SEARCHING";
	private static final CharSequence STOPPED = "STOPPED";
	private static final CharSequence NODATA = "NODATA";
	
	static final String STATUS_OK = "41";
	
	private int[] buffr;
	
	/**
	 * @param command the command to send. This will be the raw data send to the OBD device
	 * (if a sub-class does not override {@link #getOutgoingBytes()}).
	 */
	public NumberResultCommand(String command) {
		super(command);
	}

	@Override
	public void parseRawData() {
		
		int index = 0;
		int length = 2;
		byte[] data = getRawData();
		
		String dataString = new String(data);

		if (isSearching(dataString)) {
			setCommandState(CommonCommandState.SEARCHING);
			return;
		}
		else if (isNoDataCommand(dataString)) {
			setCommandState(CommonCommandState.EXECUTION_ERROR);
			return;
		}
		
		buffr = new int[data.length / 2];
		while (index + length <= data.length) {
			String tmp = new String(data, index, length);
			
			if (index == 0) {
				// this is the status
				if (!tmp.equals(STATUS_OK)) {
					setCommandState(CommonCommandState.EXECUTION_ERROR);
					return;
				}
			}
			else if (index == 2) {
				// this is the ID byte
				if (!tmp.equals(this.getResponseTypeID())) {
					setCommandState(CommonCommandState.UNMATCHED_RESULT);
					return;
				}
			}
			
			/*
			 * this is a hex number
			 */
			buffr[index/2] = Integer.parseInt(tmp, 16);
			if (buffr[index/2] < 0){
				setCommandState(CommonCommandState.EXECUTION_ERROR);
				return;
			}
			index += length;
		}
		
		setCommandState(CommonCommandState.FINISHED);
	}
	
	public abstract Number getNumberResult();

	public int[] getBuffer() {
		return buffr;
	}
	
	private boolean isSearching(String dataString) {
		return dataString.contains(SEARCHING) || dataString.contains(STOPPED);
	}
	
	private boolean isNoDataCommand(String dataString) {
		return dataString == null || dataString.contains(NODATA);
	}
	
}
