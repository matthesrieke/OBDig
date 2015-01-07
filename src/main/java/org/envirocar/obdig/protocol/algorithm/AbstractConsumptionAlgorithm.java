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
package org.envirocar.obdig.protocol.algorithm;

import org.envirocar.obdig.exception.FuelConsumptionException;
import org.envirocar.obdig.model.Car.FuelType;
import org.envirocar.obdig.storage.Measurement;

public abstract class AbstractConsumptionAlgorithm {
	
	public abstract double calculateConsumption(Measurement measurement) throws FuelConsumptionException, UnsupportedFuelTypeException;
	
	public abstract double calculateCO2FromConsumption(double consumption) throws FuelConsumptionException;
	
	/**
	 * An implementation shall calculate the CO2 emission (kg/h) for a fuel consumption value (l/h)
	 * 
	 * @param consumption fuel consumption in l/h
	 * @param type see {@link FuelType}
	 * @return CO2 emission in kg/h
	 * @throws FuelConsumptionException if the fuelType is not supported
	 */
	public static double calculateCO2FromConsumption(double consumption, FuelType type)
			throws FuelConsumptionException {
		if (type == FuelType.GASOLINE) {
			return consumption * 2.35; //kg/h
		}
		else if (type == FuelType.DIESEL) {
			return consumption * 2.65; //kg/h
		}
		else throw new FuelConsumptionException("Unsupported FuelType "+ type);
	}


}
