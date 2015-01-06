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
package org.envirocar.obdig.protocol.algorithm;

import static org.envirocar.obdig.storage.Measurement.PropertyKey.CALCULATED_MAF;
import static org.envirocar.obdig.storage.Measurement.PropertyKey.MAF;

import org.envirocar.obdig.exception.FuelConsumptionException;
import org.envirocar.obdig.model.Car;
import org.envirocar.obdig.model.Car.FuelType;
import org.envirocar.obdig.storage.Measurement;

public class BasicConsumptionAlgorithm extends AbstractConsumptionAlgorithm {

	private Car car;

	public BasicConsumptionAlgorithm(Car car) {
		this.car = car;
	}

	@Override
	public double calculateConsumption(Measurement measurement) throws FuelConsumptionException, UnsupportedFuelTypeException {
		if (car.getFuelType() == FuelType.DIESEL)
			throw new UnsupportedFuelTypeException(FuelType.DIESEL);
		
		double maf;
		if (measurement.hasProperty(MAF)) {
			maf = measurement.getProperty(MAF);	
		} else if (measurement.hasProperty(CALCULATED_MAF)) {
			maf = measurement.getProperty(CALCULATED_MAF);
		} else throw new FuelConsumptionException("Get no MAF value");
		
		double airFuelRatio;
		double fuelDensity;
		if (this.car.getFuelType() == FuelType.GASOLINE) {
			airFuelRatio = 14.7;
			fuelDensity = 745;
		}
		else if (this.car.getFuelType() == FuelType.DIESEL) {
			airFuelRatio = 14.5;
			fuelDensity = 832;
		}
		else throw new FuelConsumptionException("FuelType not supported: "+this.car.getFuelType());
		
		//convert from seconds to hour
		double consumption = (maf / airFuelRatio) / fuelDensity * 3600;
		
		return consumption;
	}

	@Override
	public double calculateCO2FromConsumption(double consumption) throws FuelConsumptionException {
		return calculateCO2FromConsumption(consumption, this.car.getFuelType());
	}

}
