(function ($, undefined) {

    var out, currentCmdParams;
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
        validateCommand: function (input) {
            input = $.trim(input);
            //begin with a command like format
            var m = input.match(/[a-zA-Z0-9]+(:[a-zA-Z0-9]+)?/);
            if (m[0] != null) {

                var cmdInput = m[0];
                var cmd = Commands[cmdInput];

                if (cmd) {
                    cmd.name = cmdInput;
                    //Command known
                    /*if (cmdInput != input) {
                     //full regex
                     var n = input.match(/([a-zA-Z0-9]+(:[a-zA-Z0-9]+)?)(\s+\-{1,2}[a-zA-Z0-9][a-zA-Z0-9-]*(=?\s?['|"]?[\w|.|?|=|&|+| |:|\/|\-|\\]*['|"]?)?)*$/g);
                     if (n[0] != input) {

                     out.error("Bad usage !");
                     CommandHandler.call("help", {"-command": cmd.name})
                     return false;
                     }
                     }*/
                    return cmd;
                }
                else {
                    out.error("Error. This command does not exist.");
                    return false;
                }
            }
        },
        parseParams: function (cmd, input) {
            var indexFirstSpace = input.indexOf(" ");
            var paramMandatory = _.filter(cmd.params, function (param) {
                return param.mandatory == true;
            });
            if (indexFirstSpace == -1) {
                return(paramMandatory.length == 0);
            }
            else {
                var inputParamsString = input.substr(indexFirstSpace);

                var paramOptional = _.filter(cmd.params, function (param) {
                    return param.mandatory == false;
                });
                inputParamsString = validateParams(paramMandatory, inputParamsString, true);
                if (inputParamsString) {
                    var inputParamsString = validateParams(paramOptional, inputParamsString, false);
                }
                if (inputParamsString || inputParamsString === "") {
                    return true;
                }
                if (inputParamsString) {
                    out.error("Syntax error near : " + inputParamsString);
                    return false;
                } else {
                    return false;
                }
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
    var helpExecute = function (params) {
        var ret = 0;
        var output = '';
        var commandName = params[this.params[0].name];
        var command = Commands[commandName];
        if (command) {
            output += commandName + ': ' + command.doc;
            output += '\n\tParameters:\nOption (* = required)\t\t\tDescription\n---------------------\t\t-----------------\n';
            if (!command.params) {
                output += 'No parameter';

            } else {
                _.each(command.params, function (param) {
                    if (param.mandatory) {
                        output += '* ';
                    }
                    output += param.name + ' ' + (param.docValue ? param.docValue : '') + '\t' + param.doc;
                    output += '\n';
                });

            }

        } else {
            output += commandName + " is not a known command";
        }
        out.echo(output);
        return ret;

    }
    var mvnExecute = function (params) {
        cmdController.call(params);
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
                    value:false,

                },
                {
                    name: "--maven-command",
                    doc: "Reference to the Maven command that should be executed",
                    mandatory: true,
                    value: true,
                    docValue: "<goal[:executable]>"
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
        var cmd = CommandHandler.validateCommand(c);
        if (cmd) {

            currentCmdParams = {};
            var paramsOk = CommandHandler.parseParams(cmd, c);
            if (paramsOk) {
                var result = CommandHandler.call(cmd.name, currentCmdParams);
            }
            else {
                term.error('Bad Usage ! ');
                CommandHandler.call("help", {"-command": cmd.name})
            }
        }
        else {
            //command not found or bad usage. Output are done in validateCommand() function
        }
    }, {
        greetings: 'Welcome in CLiC!\n Please enter your command  or "list" to know about available commands.',
        name: 'clic-task',
        height: 400,
        prompt: '>'
    });

    var isParamPresent = function (param, cmdParams) {
        if (cmdParams.indexOf(param.name) == -1) {
            if (param.miniName) {
                if (cmdParams.indexOf(param.miniName) == -1) {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        return true;
    }
    var validateParams = function (params, input, mandatory) {
        var inputParamsString = input;
        //keep number of param in a var because params is not immutable
        var nbParam = params.length;
        for (var i = 0; i < nbParam; i++) {

            inputParamsString = $.trim(inputParamsString);

            if (inputParamsString.indexOf("-") == 0) {
                var ret = false;
                var r = inputParamsString.match(/^\-{1,2}[a-zA-Z0-9][a-zA-Z0-9-]*/);
                var param = r[0];

                var t = inputParamsString.split(param);
                if (t.length == 2) {
                    //found
                    inputParamsString = $.trim(t[1]);
                    _.each(params, function (p, i) {
                        if (p.name == param || (p.miniName && p.miniName == param)) {
                            //replace param mini name by param name
                            param = p.name;
                            //known param
                            //now check for value
                            if (p.value) {
                                var value;
                                var v = inputParamsString.match(/([\w|.|:|/|\-|\\]*)/);
                                value = v[0];
                                if (!v || value.length <= 0) {

                                    var v = inputParamsString.match(/=['|"][\w|.|?|=|&|+| |:|/|\-|\\]*['|"]/);
                                    value = v[0];
                                    if (v && value.length != 0) {
                                        var comaOpen = value.charAt(1);
                                        var comaClose = value.charAt(value.length - 1);
                                        if (comaClose != comaOpen) {
                                            return false;
                                        }
                                    }
                                    else {
                                        return false;
                                    }
                                }
                                currentCmdParams[param] = value;
                                var u = inputParamsString.split(value);
                                if (u.length == 2) {
                                    inputParamsString = u[1];

                                    params.splice(i, 1);
                                }
                            } else {
                                currentCmdParams[param] = "";
                                params.splice(i, 1);
                            }
                        }
                    })
                } else {
                    return false;
                }
            } else {
                if (inputParamsString.length != 0) {
                    return false;
                }

            }
            console.log("hello");
        }

        inputParamsString = $.trim(inputParamsString);
        if ((mandatory && params.length != 0) || (!mandatory && inputParamsString != "")) {
            return false
        }
        if (mandatory) {
            return inputParamsString;
        } else {
            return true;
        }
    }


    var merge_options = function (obj1, obj2) {
        var obj3 = {};
        for (var attrname in obj1) {
            obj3[attrname] = obj1[attrname];
        }
        for (var attrname in obj2) {
            obj3[attrname] = obj2[attrname];
        }
        return obj3;
    }


})
    (jQuery);