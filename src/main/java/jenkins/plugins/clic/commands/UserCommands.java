package jenkins.plugins.clic.commands;


import jenkins.plugins.clic.tools.Tool;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: pierremarot
 * Date: 18/01/2014
 * Time: 14:12
 */


public class UserCommands {

    private Map<String,Command> commands;
    private String user;

    public UserCommands(String user){
        this.user = user;
        commands = new HashMap<>();

    }

    public List<String> getCommandLog(String timestamp){
        List<String> ret = null;
        Command c = commands.get(timestamp);

        if(c != null){
           ret = c.getLogs();
        }

        return ret;
    }

    public Command getCommand(String timestamp){
        return commands.get(timestamp);
    }

    @SuppressWarnings("unused")
    public void addCommand(Path pathLog,Path pathResult) {
       commands.put(Tool.getTimestamp(pathLog),new Command(pathLog,pathResult));
    }

    public void addCommand(Command command){
        commands.put(command.getTimestamp(),command);
    }

    public String getUser(){
        return user;
    }
}
