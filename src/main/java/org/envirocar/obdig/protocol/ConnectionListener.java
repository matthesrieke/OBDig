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
package org.envirocar.obdig.protocol;

import java.io.IOException;

/**
 * Interface is used by the {@link OBDCommandLooper} to provide
 * information on the connection state.
 * 
 * @author matthes rieke
 *
 */
public interface ConnectionListener {
	
	/**
	 * a serious exception occured while using the connection
	 * 
	 * @param e the exception
	 */
//	public void onConnectionException(IOException e);

	/**
	 * called when the connetion is verified (= a useful response
	 * was received)
	 */
	public void onConnectionVerified();

	/**
	 * called when all registered adapters failed to create a connection
	 */
	public void onAllAdaptersFailed();

	/**
	 * @param message the status update message
	 */
	public void onStatusUpdate(String message);

	/**
	 * called when the underlying adpater wants to re-establish the connection
	 * @param reason 
	 */
	public void requestConnectionRetry(IOException reason);

}
