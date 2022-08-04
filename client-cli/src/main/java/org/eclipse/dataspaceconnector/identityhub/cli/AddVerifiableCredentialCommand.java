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

package org.eclipse.dataspaceconnector.identityhub.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.iam.did.spi.key.PrivateKeyWrapper;
import org.eclipse.dataspaceconnector.identityhub.credentials.model.VerifiableCredential;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

import static org.eclipse.dataspaceconnector.identityhub.credentials.CryptoUtils.readPrivateEcKey;


@Command(name = "add", description = "Adds a verifiable credential to identity hub")
class AddVerifiableCredentialCommand implements Callable<Integer> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @ParentCommand
    private VerifiableCredentialsCommand command;

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(names = { "-c", "--verifiable-credential" }, required = true, description = "Verifiable Credential as JSON")
    private String verifiableCredentialJson;

    @CommandLine.Option(names = { "-i", "--issuer" }, required = true, description = "DID of the Verifiable Credential issuer")
    private String issuer;

    @CommandLine.Option(names = { "-b", "--subject" }, required = true, description = "DID of the Verifiable Credential subject")
    private String subject;

    @CommandLine.Option(names = { "-k", "--private-key" }, required = true, description = "PEM file with EC private key for signing Verifiable Credentials")
    private String privateKeyPemFile;

    @Override
    public Integer call() {
        var out = spec.commandLine().getOut();

        VerifiableCredential vc;
        try {
            vc = MAPPER.readValue(verifiableCredentialJson, VerifiableCredential.class);
        } catch (JsonProcessingException e) {
            throw new CliException("Error while processing request json.");
        }

        PrivateKeyWrapper privateKey;
        try {
            privateKey = readPrivateEcKey(privateKeyPemFile);
        }
        catch (Exception e) {
            throw new CliException("Error while reading private key file: " + e.getMessage());
        }
        var signedJwt = command.cli.verifiableCredentialsJwtMarshaller.buildSignedJwt(vc, issuer, subject, privateKey);
        if (signedJwt.failed()) {
            throw new CliException("Error while signing Verifiable Credential: " + signedJwt.getFailureDetail());
        }

        command.cli.identityHubClient.addVerifiableCredential(command.cli.hubUrl, signedJwt.getContent());

        out.println("Verifiable Credential added successfully");

        return 0;
    }

}
