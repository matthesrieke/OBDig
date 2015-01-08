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
package org.envirocar.obdig.protocol.adapter.drivedeck;

import java.util.List;

import org.envirocar.obdig.commands.AbstractCommand;
import org.envirocar.obdig.commands.PIDUtil;

public class CycleCommand extends AbstractCommand {
	
	public static enum PID {
		SPEED {
			@Override
			public String toString() {
				return convert("0D");
			}
		},
		MAF {
			@Override
			public String toString() {
				return convert("10");
			}
		},
		RPM {
			@Override
			public String toString() {
				return convert("0C");
			}
		},
		IAP {
			@Override
			public String toString() {
				return convert("0B");
			}
		},
		IAT {
			@Override
			public String toString() {
				return convert("0F");
			}
		},
		SHORT_TERM_FUEL_TRIM {
			@Override
			public String toString() {
				return convert("06");
			}
		},
		LONG_TERM_FUEL_TRIM {
			@Override
			public String toString() {
				return convert("07");
			}
		},
		O2_LAMBDA_PROBE_1_VOLTAGE {
			@Override
			public String toString() {
				return convert("24");
			}
		},
		O2_LAMBDA_PROBE_1_CURRENT {
			@Override
			public String toString() {
				return convert(PIDUtil.PID.O2_LAMBDA_PROBE_1_CURRENT.toString());
			}
		};
		
		protected String convert(String string) {
			return Integer.toString(incrementBy13(hexToInt(string)));
		}

		protected int hexToInt(String string) {
			return Integer.valueOf(string, 16);
		}

		protected int incrementBy13(int hexToInt) {
			return hexToInt + 13;
		}

		protected String intToHex(int val) {
			String result = Integer.toString(val, 16);
			if (result.length() == 1) result = "0"+result;
			return "0x".concat(result);
		}
	}

	private static final String NAME = "a17";
	public static final char RESPONSE_PREFIX_CHAR = 'B';
	public static final char TOKEN_SEPARATOR_CHAR = '<';
	private byte[] bytes;
	

	public CycleCommand(List<PID> pidList) {
		bytes = new byte[3+pidList.size()];
		byte[] prefix = "a17".getBytes();
		
		for (int i = 0; i < prefix.length; i++) {
			bytes[i] = prefix[i];
		}
		
		int i = 0;
		for (PID pid : pidList) {
			bytes[prefix.length + i++] = (byte) Integer.valueOf(pid.toString()).intValue();
		}
	}

	@Override
	public void parseRawData(byte[] data) {
		
	}

	@Override
	public String getCommandName() {
		return NAME;
	}
	
	public byte[] getOutgoingBytes() {
		return bytes;
	}

	@Override
	public byte[] getRawData() {
		return null;
	}

	@Override
	public String getPIDAsString() {
		return "a17";
	}

}
