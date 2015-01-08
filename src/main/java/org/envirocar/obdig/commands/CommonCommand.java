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

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract command class that the other commands have to extend. Many things
 * are imported from Android OBD Reader project!
 * 
 * @author jakob
 * 
 */
public abstract class CommonCommand {
	
	/**
	 * The state of the command.
	 */
	public enum CommonCommandState {
		NEW, RUNNING, FINISHED, EXECUTION_ERROR, QUEUE_ERROR, SEARCHING, UNMATCHED_RESULT
	}

	private static Set<Character> ignoredChars;
	private static final String DEFAULT_MODE = "01";
	private static final byte[] DEFAULT_MOD_BYTES = DEFAULT_MODE.getBytes();
	private static final byte SPACE_BYTE = ' ';
	
	public static final String STATUS_OK = "41";
	public static final char COMMAND_SEND_END = '\r';
	public static final char COMMAND_RECEIVE_END = '>';
	public static final char COMMAND_RECEIVE_SPACE = ' ';
	
	static {
		ignoredChars = new HashSet<Character>();
		ignoredChars.add(COMMAND_RECEIVE_SPACE);
		ignoredChars.add(COMMAND_SEND_END);
	}
	
	private Long commandId;
	private CommonCommandState commandState = CommonCommandState.NEW;
	private long resultTime;
	
	private byte[] command;

	public abstract String getResponseTypeID();
	
	public abstract byte[] getRawData();

	public abstract void parseRawData(byte[] raw);
	
	public boolean responseAlwaysRequired() {
		return true;
	}

	/**
	 * @return the OBD command name.
	 */
	public abstract String getCommandName();

	/**
	 * @return the commandId
	 */
	public Long getCommandId() {
		return commandId;
	}

	/**
	 * @return the commandState
	 */
	public CommonCommandState getCommandState() {
		return commandState;
	}

	/**
	 * @param commandState
	 *            the commandState to set
	 */
	public void setCommandState(CommonCommandState commandState) {
		this.commandState = commandState;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Commandname: ");
		sb.append(getCommandName());
		sb.append(", Command: ");
		sb.append(new String(getOutgoingBytes()));
		sb.append(", Result Time: ");
		sb.append(getResultTime());
		return sb.toString();
	}

	public void setResultTime(long currentTimeMillis) {
		this.resultTime = currentTimeMillis;
	}

	public long getResultTime() {
		return resultTime;
	}

	public byte[] getOutgoingBytes() {
		if (this.command != null) {
			return this.command;
		}
		byte[] typeId = getResponseTypeID().getBytes();
		byte[] modeByte = getModeBytes();
		this.command = new byte[modeByte.length + typeId.length + 1];
		
		int pos = 0;
		for (int i = 0; i < modeByte.length; i++) {
			this.command[pos++] = modeByte[i];
		}
		
		this.command[pos++] = SPACE_BYTE;
		
		for (int i = 0; i < typeId.length; i++) {
			this.command[pos++] = typeId[i];
		}
		
		return this.command;
	}

	public byte[] getModeBytes() {
		return DEFAULT_MOD_BYTES;
	}

	public char getEndOfLineReceive() {
		return COMMAND_RECEIVE_END;
	}

	public char getIgnoreCharReceive() {
		return COMMAND_RECEIVE_SPACE;
	}

	public char getEndOfLineSend() {
		return COMMAND_SEND_END;
	}

	public boolean awaitsResults() {
		return true;
	}

	public Set<Character> getIgnoredChars() {
		return ignoredChars;
	}

}