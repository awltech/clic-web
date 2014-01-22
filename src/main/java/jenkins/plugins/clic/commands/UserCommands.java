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
        commands = new HashMap<String,Command>();

    }

    public List<String> getCommandLog(String timestamp){
        List<String> ret = null;
        Command c = (Command) commands.get(timestamp);

        if(c != null){
           ret = c.getLogs();
        }

        return ret;
    }

    public Command getCommand(String timestamp){
        return (Command) commands.get(timestamp);
    }


    public void addCommand(Path path) {
       commands.put(Tool.withoutExtension(path.getFileName().toString()),new Command(path,false));
    }

    public void addCommand(Command command){
        commands.put(command.getTimestamp(),command);
    }
}
