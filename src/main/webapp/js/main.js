/**
 * Created by pierremarot on 12/12/2013.
 */
"use strict";
(function ($) {
    //Clean history for security reason
    $(window).unload(function () {
        for (var key in localStorage) {
            if (key.startsWith("clic-task_")) {
                localStorage.removeItem(key);
            }
        }
    })

    var out, currentCmdParams, Commands, Aliasses;

    /**
     * Get all the user's aliasses
     */
    aliasController.loadAlias(function (result) {
        var aliasses = result.responseObject();
        if (aliasses) {
            Aliasses = aliasses;
        }
    });
    /*get the json that contain all the commands configurations.
     Used for front validation*/

    //noinspection JSUnresolvedVariable
    $.getJSON(rootURL + "/plugin/clic/js/commands.json", function (data) {
        Commands = data;
    });

    var CommandHandler = {
        call: function (name, params) {
            switch (name) {
                case "help":
                    helpExecute(params);
                    break;
                case "list":
                    listExecute();
                    break;
                case "alias":
                    aliasExecute(params);
                    break;
                default :
                    backExecute(params);
            }
        },
        validateCommand: function (input) {
            input = $.trim(input);
            //begin with a command like format
            var m = input.match(/[a-zA-Z0-9]+(:[a-zA-Z0-9]+)?/);
            if (m == null) {
                return false;
            }
            if (m[0] != null) {

                var cmdInput = m[0];
                var cmd = Commands[cmdInput];

                if (cmd) {
                    cmd.name = cmdInput;
                    return cmd;
                }
                else {
                    out.error("Error. This command does not exist.");
                }
            } else {
                out.error("Error. Please enter a command.");
            }
            return false;
        },
        parseParams: function (cmd, input) {
            var indexFirstSpace = input.indexOf(" ");
            var paramMandatory = this.getMandatoryParams(cmd.params);
            if (indexFirstSpace == -1) {
                return(paramMandatory.length == 0);
            }
            else {
                var inputParamsString = input.substr(indexFirstSpace);


                inputParamsString = this.validateParams(cmd.params, inputParamsString);

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
        },
        getMandatoryParams: function (params) {
            return _.filter(params, function (param) {

                //noinspection JSUnresolvedVariable
                return param.mandatory == true;
            });
        },
        validateParams: function (cmdParams, input) {
            var params = cmdParams.slice();
            var inputParamsString = input;
            //keep number of param in a var because params is not immutable
            var nbParam = params.length;
            var isOk = true;
            for (var i = 0; i < nbParam; i++) {
                if (!isOk) {
                    return false;
                }
                inputParamsString = $.trim(inputParamsString);

                if (inputParamsString.indexOf("-") == 0) {
                    var r = inputParamsString.match(/^\-{1,2}[a-zA-Z0-9][\.a-zA-Z0-9-]*/);
                    if (r == null) {
                        return false;
                    }
                    var param = r[0];

                    var t = inputParamsString.split(param);
                    if (t.length == 2) {
                        //found
                        inputParamsString = $.trim(t[1]);
                        _.each(params, function (p) {
                            var multiple = false;
                            //wierd ! Happen with <= IE8
                            if (!p) {
                                return;
                            }
                            //multiple is parameters that kind be repeated in command like -D
                            if (p.multiple && param.startsWith(p.name)) {
                                multiple = true;
                            }
                            //noinspection JSUnresolvedVariable
                            if (p.name == param || (p.miniName && p.miniName == param) || multiple) {
                                //replace param mini name by param name
                                if (!multiple) {
                                    param = p.name;

                                } else {
                                    i--;
                                }
                                //known param
                                //now check for value
                                if (p.value) {
                                    var value;
                                    var v = inputParamsString.match(/^ ?=?([\w||.|:|/|\-|\\]+)/);


                                    if (v == null || v[0].length <= 0) {

                                        v = inputParamsString.match(/=['|"][\w|.|?|=|&|+| |:|/|\-|\\]*['|"]/);
                                        if (v == null) {
                                            isOk = false;
                                            return false;
                                        }
                                        value = v[0];
                                        if (v && value.length != 0) {
                                            var comaOpen = value.charAt(1);
                                            var comaClose = value.charAt(value.length - 1);
                                            if (comaClose != comaOpen) {
                                                isOk = false;
                                                return false;
                                            }
                                        }
                                        else {
                                            isOk = false;
                                            return false;
                                        }
                                    }
                                    value = v[0];
                                    currentCmdParams[param] = value;
                                    var u = inputParamsString.split(value);
                                    if (u.length >= 2) {
                                        inputParamsString = inputParamsString.replace(value, "");
                                        if (!multiple) {
                                            params.splice(params.indexOf(p), 1);
                                        }
                                    }
                                } else {
                                    currentCmdParams[param] = "";
                                    if (!multiple) {
                                        params.splice(params.indexOf(p), 1);
                                    }
                                }
                            }
                        });
                    } else {
                        return false;
                    }
                } else {
                    if (inputParamsString.length != 0) {
                        out.error("Syntax error near : " + inputParamsString);
                        return false;
                    }
                }
            }

            inputParamsString = $.trim(inputParamsString);
            return !((params.length != 0 && !CommandHandler.getMandatoryParams(params)) && (inputParamsString != ""));

        }
    };

    /*Command specific*/




    var listExecute = function () {
        var ret = 0;
        var output = '';

        //print aliasses
        out.echo("Alias : ");
        if (Aliasses.length === 0) {
            out.echo("you have no alias");
        }

        for (var i = 0; i < Aliasses.length; i++) {
            output += formatAlias(Aliasses[i].name) + " - " + Aliasses[i].command + "\n";
        }
        out.echo(output)
        output = '';
        out.echo("Commands : ");
        for (var object in Commands) {
            //noinspection JSUnfilteredForInLoop,JSUnresolvedVariable
            output += formatCommand(object) + Commands[object].doc + "\n";
        }

        out.echo(output);
        $(document).trigger("clicFinished", ret);
        return ret;
    };

    var helpLogic = function (params, displayResult) {
        var ret = 0;
        var output = '';
        //noinspection JSUnresolvedVariable
        var commandName = params[Commands.help.params[0].name];
        var command = Commands[commandName];
        if (command) {
            //noinspection JSUnresolvedVariable
            output += formatCommand(commandName) + ': ' + command.doc;
            output += '\n\tParameters:\nOption (* = required)\t\t\tDescription\n---------------------\t\t-----------------\n';
            if (!command.params) {
                output += 'No parameter';

            } else {
                _.each(command.params, function (param) {
                    //noinspection JSUnresolvedVariable
                    if (param.mandatory) {
                        output += '* ';
                    }
                    //noinspection JSUnresolvedVariable
                    output += formatParam(param.name) + ' ' + (param.docValue ? param.docValue : '') + '\t' + param.doc;
                    output += '\n';
                });
            }
        } else {
            output += commandName + " is not a known command";
            ret = 1
        }
        out.echo(output);
        if (displayResult) {
            $(document).trigger("clicFinished", ret);
        }
        return ret;
    };

    var helpExecute = function (params) {
        helpLogic(params, true);
    };

    var aliasExecute = function (params) {
        var name = params["-n"];
        var remove = params["-d"] == "";

        for (var object in Commands) {
            if (name == object) {
                out.error("Unable to create an alias with this name. " + name + " is taken.");
                out.resume();
                return;
            }
        }
        if (remove) {
            var alias = _.find(Aliasses, function (alias) {
                return alias.name === name;
            });

            if (!alias) {
                out.error("Alias named " + name + " does not exist");
                out.resume();
                return;
            } else {
                aliasController.removeAlias(name, function (result) {
                    var code = result.responseObject();
                    if (code == 0) {
                        Aliasses.splice(Aliasses.indexOf(alias, 1));
                        term.echo("alias successfully removed");
                    } else {
                        out.error("Can't delete this alias. An error occured");
                    }
                });
            }
        } else {
            out.echo("Please enter the desired command for this alias and press enter. You can use the history to help you.");
            localStorage['clic-task_0_alias_commands'] = localStorage['clic-task_0_commands'];
            out.push(function (c, term) {
                var cmd;
                var cmd = CommandHandler.validateCommand(c);
                if(c === 'exit'){
                    term.pop();
                }
                if (cmd) {

                    currentCmdParams = {};
                    var paramsOk = CommandHandler.parseParams(cmd, c);
                    if (paramsOk) {
                        aliasController.addAlias(name, c, function (result) {
                            var code = result.responseObject();
                            if (code == 0) {
                                Aliasses.push({name:name,command:c});
                                term.echo("alias successfully added");
                            } else {
                                term.error("Unable to add this alias. An error occured");
                            }
                            term.pop();
                        });
                    }
                    else {
                        term.error('Bad Usage ! ');
                        helpLogic({"-command": cmd.name}, false);
                        term.error("Type 'exit' to cancel");
                    }
                }

            }, { name: 'alias',
                prompt: '>'});

        }
    }

    var backExecute = function (params) {
        out.pause();
        var timeout, stop = false;

        var managePulling = function (timestamp, callback) {
            timeout = setInterval(function () {
                callback(timestamp);
            }, 1000)
        };

        var getLogs = function (timestamp) {
            //noinspection JSUnresolvedVariable,JSUnresolvedFunction
            cmdController.getLogs(timestamp, function (r) {
                //noinspection JSUnresolvedFunction
                var result = r.responseObject();
                if (result.log !== "") {
                    out.echo(result.log);
                }
                //noinspection JSUnresolvedVariable
                if (result.finished && !stop) {
                    stop = true;
                    clearInterval(timeout);
                    //noinspection JSUnresolvedVariable,JSUnresolvedFunction
                    cmdController.getExitCode(timestamp, function (r) {
                        //noinspection JSUnresolvedFunction
                        $(document).trigger("clicFinished", r.responseObject());
                        out.resume();
                    });
                }
            });
        };

        var command = "clic:mvn"; // don't need as we only support this command for now
        var ret = 0;

        _.each(params, function (v, k) {
            command += " " + k;
            if (v.charAt(0) != '=') {
                command += " ";
            }
            command += v;
        });

        //noinspection JSUnresolvedVariable
        cmdController.call(command, function (r) {
            //noinspection JSUnresolvedFunction
            var result = r.responseObject();
            if (!result.startsWith("ERROR")) {
                managePulling(result, getLogs);
            } else {
                out.error(result);
                $(document).trigger("clicFinished", r.responseObject());
                out.resume();
                //noinspection JSUnresolvedVariable,JSUnresolvedFunction
            }
        });
        return ret;
    };

    //Get the history for that user
    //noinspection JSUnresolvedVariable,JSUnresolvedFunction
    //noinspection JSUnresolvedVariable,JSUnresolvedFunction
    cmdController.getHistory(function (r) {
        //noinspection JSUnresolvedFunction
        var commands = r.responseObject().history;
        localStorage['clic-task_0_commands'] = $.json_stringify(commands);
        ;

        $('#terminal').terminal(function (c, term) {
            out = term;
            var alias = _.find(Aliasses,function(a){
                return a.name === c;
            });
            if(alias){
                term.insert(alias.command);
                return;
            }
            var cmd = CommandHandler.validateCommand(c);
            if (cmd) {

                currentCmdParams = {};
                var paramsOk = CommandHandler.parseParams(cmd, c);
                if (paramsOk) {
                    CommandHandler.call(cmd.name, currentCmdParams);
                }
                else {
                    term.error('Bad Usage ! ');
                    helpLogic({"-command": cmd.name}, false)
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
        })
    });

    $(document).on("clicFinished", function (e, ret) {
        out.echo("---");
        if (ret === 0) {
            out.echo("Execution: OK");
        }
        else if (ret === 1) {
            out.error("Execution: KO");
        } else {
            out.error("An error occurred. Unable to know the exit code");
        }
    });

    var format = function (name, color) {
        return "[[ub;" + color + ";black]" + name + "]";
    }

    var formatAlias = function(name){
        return format(name, "#07C7ED");
    }
    var formatCommand = function(name){
        return format(name,"#00FF85");
    }
    var formatParam = function(name){
        return format(name,"#FF7000");
    }


})
    (jQuery);