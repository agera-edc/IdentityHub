/*
 *  Copyright (c) 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.identityhub.verifier;

import org.eclipse.dataspaceconnector.spi.result.AbstractResult;
import org.eclipse.dataspaceconnector.spi.result.Failure;

import java.util.List;

//TODO: Add javadoc.
class VerificationResult<T> extends AbstractResult<T, Failure> {
    VerificationResult(T successfulResult, List<String> failureMessage) {
        super(successfulResult, failureMessage.isEmpty() ? null : new Failure(failureMessage));
    }
}