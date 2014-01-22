package jenkins.plugins.clic.commands;

import jenkins.plugins.clic.tools.Tool;

import java.io.BufferedReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: pierremarot
 * Date: 18/01/2014
 * Time: 14:10
 */
public class UsersCommands {

    private static Map<String,UserCommands> usersCommands = new HashMap<String,UserCommands>();




    public static List<String> getCommandLog(String user,String timestamp){
        return usersCommands.get(user).getCommandLog(timestamp);
    }

    public static Command addCommand(String user,Path path){
        Command newCommand = new Command(path);
        UserCommands uCS = usersCommands.get(user);
        if(uCS == null){
            uCS = new UserCommands(user);
            usersCommands.put(user,uCS);
        }
        uCS.addCommand(newCommand);
        return newCommand;
    }

    public static void finish(String timestamp){
        usersCommands.get(Tool.getUserName()).getCommand(timestamp).finish();
    }

    public static Command getCommand(String user,String timestamp){
       return usersCommands.get(user).getCommand(timestamp);
    }


}
