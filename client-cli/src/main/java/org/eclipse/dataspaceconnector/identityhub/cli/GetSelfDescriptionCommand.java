/*
 *  Copyright (c) 2022 Amadeus
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Amadeus - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.identityhub.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;


@Command(name = "sd", description = "Display Self-Description document.")
class GetSelfDescriptionCommand implements Callable<Integer> {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    @ParentCommand
    IdentityHubCli cli;

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @Override
    public Integer call() throws Exception {
        var out = spec.commandLine().getOut();
        var result = cli.identityHubClient.getSelfDescription(cli.hubUrl);
        if (result.succeeded()) {
            MAPPER.writeValue(out, result.getContent());
            out.println();
        } else {
            throw new CliException("Error while getting Self-Description: " + result.getFailureDetail());
        }
        return 0;
    }
}


