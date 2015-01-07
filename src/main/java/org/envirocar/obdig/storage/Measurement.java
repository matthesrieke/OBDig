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
package org.envirocar.obdig.storage;

import java.util.HashMap;
import java.util.Map;

import org.envirocar.obdig.exception.LocationInvalidException;

/**
 * Measurement class that contains all the measured values
 * 
 * @author jakob
 * 
 */

public class Measurement {

	// All measurement values
	public enum PropertyKey {
		SPEED {
			public String toString() {
		        return "Speed";
		    }
		},
		MAF {
			public String toString() {
		        return "MAF";
		    } 
		}, 
		CALCULATED_MAF {
			public String toString() {
		        return "Calculated MAF";
		    } 
		}, 
		RPM {
			public String toString() {
		        return "Rpm";
		    } 
		}, 
		INTAKE_TEMPERATURE {
			public String toString() {
		        return "Intake Temperature";
		    } 
		}, 
		INTAKE_PRESSURE {
			public String toString() {
		        return "Intake Pressure";
		    } 
		},
		CO2 {
			public String toString() {
		        return "CO2";
		    } 
		},
		CONSUMPTION {
			public String toString() {
		        return "Consumption";
		    } 
		},
		THROTTLE_POSITON {
			@Override
			public String toString() {
				return "Throttle Position";
			}
		},
		ENGINE_LOAD {
			@Override
			public String toString() {
				return "Engine Load";
			}
		},
		GPS_ACCURACY {
			@Override
			public String toString() {
				return "GPS Accuracy";
			}
		},
		GPS_SPEED {
			@Override
			public String toString() {
				return "GPS Speed";
			}
		},
		GPS_BEARING {
			@Override
			public String toString() {
				return "GPS Bearing";
			}
		},
		GPS_ALTITUDE {
			@Override
			public String toString() {
				return "GPS Altitude";
			}
		},
		GPS_PDOP {
			@Override
			public String toString() {
				return "GPS PDOP";
			}
		},
		GPS_HDOP {
			@Override
			public String toString() {
				return "GPS HDOP";
			}
		},
		GPS_VDOP {
			@Override
			public String toString() {
				return "GPS VDOP";
			}
		},
		LAMBDA_VOLTAGE {
			@Override
			public String toString() {
				return "O2 Lambda Voltage";
			}
		},
		LAMBDA_VOLTAGE_ER {
			@Override
			public String toString() {
				return LAMBDA_VOLTAGE.toString().concat(" ER");
			}
		},
		LAMBDA_CURRENT {
			@Override
			public String toString() {
				return "O2 Lambda Current";
			}
		},
		LAMBDA_CURRENT_ER {
			@Override
			public String toString() {
				return LAMBDA_CURRENT.toString().concat(" ER");
			}
		},
		FUEL_SYSTEM_LOOP {
			@Override
			public String toString() {
				return "Fuel System Loop";
			}
		},
		FUEL_SYSTEM_STATUS_CODE {
			@Override
			public String toString() {
				return "Fuel System Status Code";
			}
		},
		LONG_TERM_TRIM_1 {
			@Override
			public String toString() {
				return "Long-Term Fuel Trim 1";
			}
		},
		SHORT_TERM_TRIM_1 {
			@Override
			public String toString() {
				return "Short-Term Fuel Trim 1";
			}
		}
		
	}
	
	public static final Map<String, PropertyKey> PropertyKeyValues = new HashMap<String, PropertyKey>();
	
	static {
		for (PropertyKey pk : PropertyKey.values()) {
			PropertyKeyValues.put(pk.toString(), pk);
		}
	}

	private double latitude;
	private double longitude;
	private long time;
	
	protected Map<PropertyKey, Double> propertyMap = new HashMap<PropertyKey, Double>();

	/**
	 * Create a new measurement. Latitude AND longitude are not allowed to both
	 * equal 0.0. This method also sets the measurement time according to the
	 * System.currentTimeMillis() method.
	 * 
	 * @param latitude
	 *            Latitude of the measurement (WGS 84)
	 * @param longitude
	 *            Longitude of the measurement (WGS 84)
	 * @throws LocationInvalidException
	 *             If latitude AND longitude equal 0.0
	 */

	public Measurement(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.time = System.currentTimeMillis();
	}
	
	public synchronized Double getProperty(PropertyKey key) {
		return (Double) propertyMap.get(key);
	}
	
	public synchronized Map<PropertyKey, Double> getAllProperties() {
		return propertyMap;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Measurement [");
		sb.append("latitude=");
		sb.append(latitude);
		sb.append(", longitude=");
		sb.append(longitude);
		sb.append(", time=");
		sb.append(time);
		sb.append(", ");
		
		synchronized (this) {
			for (PropertyKey key : propertyMap.keySet()) {
				sb.append(key.toString());
				sb.append("=");
				sb.append(propertyMap.get(key));
				sb.append(", ");
			}	
		}
		
		return sb.toString();
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the measurementTime
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param measurementTime
	 *            the measurementTime to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	
	public synchronized boolean hasProperty(PropertyKey key) {
		return propertyMap.containsKey(key);
	}

	public synchronized void setProperty(PropertyKey key, Double value) {
		propertyMap.put(key, value);
	}



	public Measurement carbonCopy() {
		Measurement result = new Measurement(this.latitude, this.longitude);
		
		synchronized (this) {
			for (PropertyKey pk : this.propertyMap.keySet()) {
				result.propertyMap.put(pk, this.propertyMap.get(pk));
			}	
		}
		
		result.time = this.time;
		return result;
	}

	public void reset() {
		latitude = 0.0;
		longitude = 0.0;
		
		synchronized (this) {
			propertyMap.clear();
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Measurement)) {
			return false;
		}
		
		Measurement m = (Measurement) o;
		if (m.time != this.time
				|| m.latitude != this.latitude
				|| m.longitude != this.longitude) {
			return false;
		}
		
		for (PropertyKey pk : this.propertyMap.keySet()) {
			if (m.getProperty(pk) != this.getProperty(pk)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return (int) (this.time / 1000) +
				(int) (this.latitude * 1000) + 
				(int) (this.longitude * 1000);
	}
}
