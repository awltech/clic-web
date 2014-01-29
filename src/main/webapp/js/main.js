(function ($, undefined) {
    //Remove history for security reason
    /*for(var key in localStorage){
     if(key.startsWith("clic-task_")){
     localStorage.removeItem(key);
     }
     }*/

    var out, currentCmdParams, Commands;

    $.getJSON("/jenkins/plugin/clic/js/commands.json",function(data){
        Commands = data;

    });



    /**
     * Created by pierremarot on 12/12/2013.
     */

    var CommandHandler = {
        call: function (name, params) {
            if(name == "help"){
                helpExecute(params);
            }else if(name == "list"){
                listExecute();
            }else{
                backExecute(params)
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
            var paramMandatory = this.getMandatoryParams(cmd.params)
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
                return param.mandatory == true;
            });
        },
        validateParams: function (cmdParams, input) {
            var params = cmdParams.slice();
            var inputParamsString = input;
            //keep number of param in a var because params is not immutable
            var nbParam = params.length;
            for (var i = 0; i < nbParam; i++) {

                inputParamsString = $.trim(inputParamsString);

                if (inputParamsString.indexOf("-") == 0) {
                    var r = inputParamsString.match(/^\-{1,2}[a-zA-Z0-9][a-zA-Z0-9-]*/);
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
                            //multiple is parameters that kind be repeated in command like -D
                            if (p.multiple && param.startsWith(p.name)) {
                                multiple = true;
                            }
                            if (p.name == param || (p.miniName && p.miniName == param) || multiple) {
                                //replace param mini name by param name
                                if (!multiple) {
                                    param = p.name;

                                }else{
                                    i--;
                                }
                                //known param
                                //now check for value
                                if (p.value) {
                                    var value;
                                    var v = inputParamsString.match(/^ ?=?([\w||.|:|/|\-|\\]+)/);


                                    if (v == null || v[0].length <= 0) {

                                        var v = inputParamsString.match(/=['|"][\w|.|?|=|&|+| |:|/|\-|\\]*['|"]/);
                                        if (v == null) {
                                            return false;
                                        }
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
                                    value = v[0];
                                    currentCmdParams[param] = value;
                                    var u = inputParamsString.split(value);
                                    if (u.length >= 2) {
                                        inputParamsString = inputParamsString.replace(value,"");
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
                        })
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
            if ((params.length != 0 && !CommandHandler.getMandatoryParams(params)) && (inputParamsString != "")) {
                return false
            }
            return true;
        }
    };

    /*Command specific*/

    var listExecute = function () {
        var ret = 0;
        var output = '';
        for (var object in Commands) {
            output += object + Commands[object].doc + "\n";
        }

        out.echo(output);
        $(document).trigger("clicFinished", ret);
        return ret;
    };

    var helpLogic = function (params, displayResult) {
        var ret = 0;
        var output = '';
        var commandName = params[Commands.help.params[0].name];
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
            ret = 1
        }
        out.echo(output);
        if (displayResult) {
            $(document).trigger("clicFinished", ret);
        }
        return ret;
    }

    var helpExecute = function (params) {
        helpLogic(params, true);
    };
    var backExecute = function (params) {
        out.pause();
        var timeout, stop = false;

        var managePulling = function (timestamp, callback) {
            timeout = setInterval(function () {
                callback(timestamp);
            }, 1000)
        }

        var getLogs = function (timestamp) {
            cmdController.getLogs(timestamp, function (r) {
                var result = r.responseObject();
                if (result.log !== "") {
                    out.echo(result.log);
                }
                if (result.finished && !stop) {
                    stop = true;
                    clearInterval(timeout);
                    cmdController.getExitCode(timestamp, function (r) {
                        $(document).trigger("clicFinished", r.responseObject());
                        out.resume();
                    })

                    return;
                }
            })
        }

        var command = "clic:mvn"; // don't need as we only support this command for now
        var ret = 0;

        _.each(params, function (v, k) {
            command += " " + k;
            if (v.charAt(0) != '=') {
                command += " ";
            }
            command += v;
        })
        cmdController.call(command, function (r) {
            var result = r.responseObject();
            if (!result.startsWith("ERROR")) {
                managePulling(result, getLogs);
            } else {
                out.error(result);
                cmdController.getExitCode(timestamp, function (r) {
                    $(document).trigger("clicFinished", r.responseObject());
                    out.resume();
                })
            }
        });
        return ret;
    };



    $('#terminal').terminal(function (c, term) {
        out = term;
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
    });

    $(document).on("clicFinished", function (e, ret) {
        out.echo("---");
        if (ret === 0) {
            out.echo("Execution: OK");
        }
        else {
            out.error("Execution: KO");
        }
    });

})
    (jQuery);