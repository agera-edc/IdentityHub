# Command-Line client

## Decision

A command-line client tool is provided to access the Identity Hub.

The [picocli](https://picocli.info) framework is used to build the command-line tool.

## Rationale

A command-line tool can be run by an administrator or automated process to push Verifiable Credentials into an Identity Hub. At the moment of writing no authentication is required to access the Identity Hub.

Picocli is a popular, well-maintained and lightweight tool. It produced elegant console output, and provides good developer productivity.
