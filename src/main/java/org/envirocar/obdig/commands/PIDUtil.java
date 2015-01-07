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

import org.envirocar.obdig.commands.numeric.EngineLoad;
import org.envirocar.obdig.commands.numeric.FuelPressure;
import org.envirocar.obdig.commands.numeric.IntakePressure;
import org.envirocar.obdig.commands.numeric.IntakeTemperature;
import org.envirocar.obdig.commands.numeric.MAF;
import org.envirocar.obdig.commands.numeric.O2LambdaProbe;
import org.envirocar.obdig.commands.numeric.RPM;
import org.envirocar.obdig.commands.numeric.Speed;
import org.envirocar.obdig.commands.numeric.TPS;
import org.envirocar.obdig.commands.raw.FuelSystemStatus;

public class PIDUtil {

	public enum PID {

		FUEL_SYSTEM_STATUS {
			@Override
			public String toString() {
				return "03";
			}
		},
		CALCULATED_ENGINE_LOAD {
			@Override
			public String toString() {
				return "04";
			}
		},
		FUEL_PRESSURE {
			@Override
			public String toString() {
				return "0A";
			}
		},
		INTAKE_MAP {
			@Override
			public String toString() {
				return "0B";
			}
		},
		RPM {
			@Override
			public String toString() {
				return "0C";
			}
		},
		SPEED {
			@Override
			public String toString() {
				return "0D";
			}
		},
		INTAKE_AIR_TEMP {
			@Override
			public String toString() {
				return "0F";
			}
		},
		MAF {
			@Override
			public String toString() {
				return "10";
			}
		},
		TPS {
			@Override
			public String toString() {
				return "11";
			}
		},
		O2_LAMBDA_PROBE_1_VOLTAGE {
			@Override
			public String toString() {
				return "24";
			}
		},
		O2_LAMBDA_PROBE_2_VOLTAGE {
			@Override
			public String toString() {
				return "25";
			}
		},
		O2_LAMBDA_PROBE_3_VOLTAGE {
			@Override
			public String toString() {
				return "26";
			}
		},
		O2_LAMBDA_PROBE_4_VOLTAGE {
			@Override
			public String toString() {
				return "27";
			}
		},
		O2_LAMBDA_PROBE_5_VOLTAGE {
			@Override
			public String toString() {
				return "28";
			}
		},
		O2_LAMBDA_PROBE_6_VOLTAGE {
			@Override
			public String toString() {
				return "29";
			}
		},
		O2_LAMBDA_PROBE_7_VOLTAGE {
			@Override
			public String toString() {
				return "2A";
			}
		},
		O2_LAMBDA_PROBE_8_VOLTAGE {
			@Override
			public String toString() {
				return "2B";
			}
		}, O2_LAMBDA_PROBE_1_CURRENT {
			public String toString() {
				return "34";
			};
		}
		, O2_LAMBDA_PROBE_2_CURRENT {
			public String toString() {
				return "35";
			};
		}
		, O2_LAMBDA_PROBE_3_CURRENT {
			public String toString() {
				return "36";
			};
		}
		, O2_LAMBDA_PROBE_4_CURRENT {
			public String toString() {
				return "37";
			};
		}
		, O2_LAMBDA_PROBE_5_CURRENT {
			public String toString() {
				return "38";
			};
		}
		, O2_LAMBDA_PROBE_6_CURRENT {
			public String toString() {
				return "39";
			};
		}
		, O2_LAMBDA_PROBE_7_CURRENT {
			public String toString() {
				return "3A";
			};
		}
		, O2_LAMBDA_PROBE_8_CURRENT {
			public String toString() {
				return "3B";
			};
		}
	}

	public static PID fromString(String s) {
		if (s == null || s.isEmpty()) return null;
		
		for (PID p : PID.values()) {
			if (s.equalsIgnoreCase(p.toString())) {
				return p;
			}
		}
		
		return null;
	}

	
	public static CommonCommand instantiateCommand(String pid) {
		return instantiateCommand(fromString(pid));
	}
	
	public static CommonCommand instantiateCommand(PID pid) {
		switch (pid) {
		case FUEL_SYSTEM_STATUS:
			return new FuelSystemStatus();
		case CALCULATED_ENGINE_LOAD:
			return new EngineLoad();
		case FUEL_PRESSURE:
			return new FuelPressure();
		case INTAKE_MAP:
			return new IntakePressure();
		case RPM:
			return new RPM();
		case SPEED:
			return new Speed();
		case INTAKE_AIR_TEMP:
			return new IntakeTemperature();
		case MAF:
			return new MAF();
		case TPS:
			return new TPS();
		case O2_LAMBDA_PROBE_1_VOLTAGE:
			return O2LambdaProbe.fromPIDEnum(pid);
		case O2_LAMBDA_PROBE_2_VOLTAGE:
			return O2LambdaProbe.fromPIDEnum(pid);
		case O2_LAMBDA_PROBE_3_VOLTAGE:
			return O2LambdaProbe.fromPIDEnum(pid);
		case O2_LAMBDA_PROBE_4_VOLTAGE:
			return O2LambdaProbe.fromPIDEnum(pid);
		case O2_LAMBDA_PROBE_5_VOLTAGE:
			return O2LambdaProbe.fromPIDEnum(pid);
		case O2_LAMBDA_PROBE_6_VOLTAGE:
			return O2LambdaProbe.fromPIDEnum(pid);
		case O2_LAMBDA_PROBE_7_VOLTAGE:
			return O2LambdaProbe.fromPIDEnum(pid);
		case O2_LAMBDA_PROBE_8_VOLTAGE:
			return O2LambdaProbe.fromPIDEnum(pid);
		default:
			return null;
		}
	}
}
