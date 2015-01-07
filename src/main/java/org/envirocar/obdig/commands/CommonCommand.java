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

	private static Set<Character> ignoredChars;
	public static final char COMMAND_SEND_END = '\r';
	public static final char COMMAND_RECEIVE_END = '>';
	public static final char COMMAND_RECEIVE_SPACE = ' ';
	
	public static final String STATUS_OK = "41";
	
	static {
		ignoredChars = new HashSet<Character>();
		ignoredChars.add(COMMAND_RECEIVE_SPACE);
		ignoredChars.add(COMMAND_SEND_END);
	}
	
	private String command = null;
	private Long commandId;
	private CommonCommandState commandState;
	private String responseTypeId;
	private long resultTime;

	/**
	 * Default constructor to use
	 * 
	 * @param command
	 *            the command to send. This will be the raw data send to the OBD device
	 *            (if a sub-class does not override {@link #getOutgoingBytes()}).
	 */
	public CommonCommand(String command) {
		this.command = command;
		determineResponseByte();
		setCommandState(CommonCommandState.NEW);
	}

	private void determineResponseByte() {
		if (this.command == null || this.command.isEmpty()) return;
		
		String[] array = this.command.split(" ");
		if (array != null && array.length > 1) {
			this.responseTypeId = array[1];
		}
	}

	/**
	 * The state of the command.
	 */
	public enum CommonCommandState {
		NEW, RUNNING, FINISHED, EXECUTION_ERROR, QUEUE_ERROR, SEARCHING, UNMATCHED_RESULT
	}


	public String getResponseTypeID() {
		return responseTypeId;
	}


	public boolean responseAlwaysRequired() {
		return true;
	}

	public abstract void parseRawData(byte[] raw);


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
		sb.append(command);
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
		return command.getBytes();
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

	public abstract byte[] getRawData();

	public Set<Character> getIgnoredChars() {
		return ignoredChars;
	}

}