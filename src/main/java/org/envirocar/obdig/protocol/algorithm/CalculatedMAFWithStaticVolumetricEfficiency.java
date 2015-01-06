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

import org.envirocar.obdig.model.Car;

public class CalculatedMAFWithStaticVolumetricEfficiency extends
		AbstractCalculatedMAFAlgorithm {

	private static final double GAS_CONSTANT = 8.3144621;
	private static final double MOLECULAR_MASS_OF_AIR = 28.9644;
	private double volumetricEfficiency = 85.0d;
	private Car car;
	
	public CalculatedMAFWithStaticVolumetricEfficiency(Car car) {
		this.car = car;
	}

	@Override
	public double calculateMAF(double rpm, double intakeTemperature,
			double intakePressure) {
		//calculate alternative maf from iat (convert to °K), map, rpm
		double imap = rpm * intakePressure / (intakeTemperature + 273.15d);
		//VE = 85 in most modern cars
		double calculatedMaf = imap / 120.0d * this.volumetricEfficiency / 100.0d * Car.ccmToLiter(this.car.getEngineDisplacement()) * MOLECULAR_MASS_OF_AIR / GAS_CONSTANT;	

		return calculatedMaf;
	}

}
