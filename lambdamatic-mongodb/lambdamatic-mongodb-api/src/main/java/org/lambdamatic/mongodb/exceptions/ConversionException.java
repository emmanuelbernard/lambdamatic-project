/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.exceptions;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ConversionException extends RuntimeException {
	/** serialVersionUID */
	private static final long serialVersionUID = 7216008533037657142L;

	/**
	 * Constructor without an underlying cause {@link Exception}.
	 * 
	 * @param message
	 *            the exception message
	 */
	public ConversionException(final String message) {
		super(message);
	}

	/**
	 * Constructor with an underlying cause {@link Exception}.
	 * 
	 * @param message
	 *            the contextual message
	 */
	public ConversionException(final String message, final Exception cause) {
		super(message, cause);
	}

}
