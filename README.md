# CliC Jenkins Plugin

This version of [CliC] allows to execute [CliC] commands from Jenkins.

## Requirement

 ${maven.home} must be set on your Jenkins Server.

 ## Features

 - clic:mvn commands
 - alias

## Installation

- Compile

skip the tests for now (Those tests are injected by Jenkins. Not sure what they do)

  mvn clean install -DskipTests

- In your Browser,  go to {Jenkins URL}/pluginManager/advanced
- Submit your hpi file you have just compiled. ( target/clic.hpi)
- That's it.


## Security

In Root.java  the getRequiredPermission()  specify which Permission define who is allowed to access the CliC interface.


[CliC]:  http://awltech.github.io/clic/