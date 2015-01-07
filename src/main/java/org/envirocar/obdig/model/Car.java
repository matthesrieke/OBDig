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
package org.envirocar.obdig.model;

import java.io.Serializable;

/**
 * Class holding all information for a car instance
 * 
 * @author matthes rieke
 *
 */
public class Car implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6321429785990500936L;
	private static final String GASOLINE_STRING = "gasoline";
	private static final String DIESEL_STRING = "diesel";

	public static final String TEMPORARY_SENSOR_ID = "%TMP_ID%";
	
	public enum FuelType {
		GASOLINE {
		    public String toString() {
		        return GASOLINE_STRING;
		    }
		    
		},
		DIESEL {
			public String toString() {
		        return DIESEL_STRING;
		    }
		}
		
	}
	
	private FuelType fuelType;
	private String manufacturer;
	private String model;
	private String id;
	private int constructionYear;
	private int engineDisplacement;
	
	public Car(FuelType fuelType, String manufacturer, String model, String id, int year, int engineDisplacement) {
		this.fuelType = fuelType;
		this.manufacturer = manufacturer;
		this.model = model;
		this.id = id;
		this.constructionYear = year;
		this.engineDisplacement = engineDisplacement;
	}
	
	public FuelType getFuelType() {
		return fuelType;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public String getModel() {
		return model;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String newID){
		this.id = newID;
	}

	public int getConstructionYear() {
		return constructionYear;
	}

	public void setConstructionYear(int year) {
		this.constructionYear = year;
	}

	/**
	 * @return the engine displacement in cubic centimeters
	 */
	public int getEngineDisplacement() {
		return engineDisplacement;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(manufacturer);
		sb.append(" ");
		sb.append(model);
		sb.append(" ");
		sb.append(constructionYear);
		sb.append(" (");
		sb.append(fuelType);
		sb.append(" / ");
		sb.append(engineDisplacement);
		sb.append("cc)");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof Car) {
			Car c = (Car) o;
			result = this.fuelType == c.fuelType 
					&& this.manufacturer.equals(c.manufacturer)
					&& this.model.equals(c.model)
					&& this.id.equals(c.id)
					&& this.constructionYear == c.constructionYear
					&& this.engineDisplacement == c.engineDisplacement;
		}
		return result;
	}

	public static FuelType resolveFuelType(String foolType) {
		if (foolType.equals(GASOLINE_STRING)) {
			return FuelType.GASOLINE;
		} else if (foolType.equals(DIESEL_STRING)) {
			return FuelType.DIESEL;
		}
		return FuelType.GASOLINE;
	}

	public static double ccmToLiter(int ccm) {
		float result = ccm / 1000.0f;
		return result;
	}
	
}
