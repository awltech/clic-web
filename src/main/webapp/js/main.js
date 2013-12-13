(function ($, undefined) {

    var param, out;
    /**
     * Created by pierremarot on 12/12/2013.
     */

    var CommandHandler = {
        call: function (name, params) {
            var ret;
            ret = Commands[name].execute(params);
            out.echo("---");
            if (ret === 0) {
                out.echo("Execution: OK");
            }
            else {
                out.error("Execution: KO");
            }
        },
        validate: function (input) {
            input = input.trim();
            var m = input.match(/([a-zA-Z0-9]*:?[a-zA-Z0-9]+)(\s*-{1,2}[a-zA-Z0-9][a-zA-Z0-9-]*(=?\s?['|"]?[\w|.|?|=|&|+| |:|/|\\]*['|"]?)?)*$/g);

            if (m != null && m[0] === input) {
                var tab, name, params;

                tab = m[0].split(" ");
                name = tab[0];
                if (Commands[name]) {
                    if (tab.length > 1) {
                        tab.splice(0, 1);
                        params = tab.join(" ");
                        return {name: name, params: params};
                    }
                    return {name: name, params: null};
                }
            }
                return false;
        },
        validateParam: function (cmd) {
            var isParamPresent = function (param) {
                if (cmd.params.indexOf(param.name) == -1) {
                    if (param.miniName) {
                        if (cmd.params.indexOf(param.miniName) == -1) {
                            return false;
                        }
                    }
                }
                return true;
            }
            var params = {};
            var value = "";
            var paramMandatory = _.filter(Commands[cmd.name].params, function (param) {
                return param.mandatory == true;
            });
            if (!cmd.params) {
                return(paramMandatory.length == 0);
            }
            else {
                _.each(paramMandatory, function (param) {

                    if (isParamPresent(param)) {
                        var subParam = cmd.params.split(param.name);
                        if (subParam.length == 1) {
                            subParam = cmd.params.split(param.miniName);
                        }
                        if (param.value) {
                            value = subParam[1].match(/(=?\s?['|"]?[\w|.|?|=|&|+| |:|/|\\]*['|"]?)?/)[0];
                            if (value == "") {
                                return false;
                            }
                            value = value.trim();
                        }
                        params[param.name] = value;

                    }
                });
                return params;

            }
            return false;

        }

    };

    var listExecute = function () {
        var ret = 0;
        var output = '';
        for (object in Commands) {
            output += object + Commands[object].doc + "\n";
        }

        out.echo(output);
        return ret;
    }
    var helpExecute = function (command) {


        var ret = 0;
        var output = '';
        for (object in Commands) {
            output += object + Commands[object].doc + "\n";
        }

        out.echo(output);
        return ret;

    }
    var mvnExecute = function () {

    }

    var Commands = {
        list: {
            doc: " - Displays the list of the available commands",
            template: "list",
            execute: listExecute
        },
        help: {
            doc: " - Gives parameters information for given command",
            template: "help -command $command$",
            params: [
                {
                    name: "-command",
                    miniName: "-co",
                    doc: "command name",
                    mandatory: true,
                    value: true,
                    docValue: "<CommandName: -co commandName>"
                }
            ],
            execute: helpExecute
        },
        "clic:mvn": {
            doc: " - Executes a task embedded in Maven",
            template: "",
            params: [
                {
                    name: "-D",
                    doc: "Allows to specify JVM-Parameters, like -Dparam=value",
                    mandatory: false,
                    value: true,
                    docValue: "<KeyValuePair: -Dparam=value>"
                },
                {
                    name: "--generate-pom",
                    doc: "Should be specified if the Maven executable refers to a configured pom.xml file",
                    mandatory: false,
                    value: false
                },
                {
                    name: "--maven-command",
                    doc: "Reference to the Maven command that should be executed",
                    mandatory: true,
                    value: false
                },
                {
                    name: "-maven-reference",
                    doc: "Reference to a Maven executable using the groupId:artifactId:version format",
                    mandatory: true,
                    value: true,
                    docValue: "<])+:groupId:artifactId:version>"
                }
            ],
            execute: mvnExecute
        }
    };


    $('#terminal').terminal(function (c, term) {
        out = term;
        var cmd = CommandHandler.validate(c);
        if (cmd) {
            var params = CommandHandler.validateParam(cmd)
            if (params) {
                var result = CommandHandler.call(cmd.name, params);
            }
            else {
                term.error('Bad Usage ! ');
                // CommandHandler.call("help",{"-command":cmd.name})
            }
        }
        else {
            term.error('Error : Your command does not exist.');
        }
    }, {
        greetings: 'Welcome in CLiC!\n Please enter your command  or "list" to know about available commands.',
        name: 'clic-task',
        height: 400,
        prompt: '>'
    });


})
    (jQuery);