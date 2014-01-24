package jenkins.plugins.clic.commands;

import jenkins.plugins.clic.tools.Tool;

import java.io.BufferedReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * User: pierremarot
 * Date: 18/01/2014
 * Time: 14:10
 */
public class UsersCommands {

    private static Map<String, UserCommands> usersCommands = new HashMap<String, UserCommands>();

    public static List<String> getCommandLog(String user, String timestamp) {
        return usersCommands.get(user).getCommandLog(timestamp);
    }

    public static Command addCommand(String user, Path pathLog, Path pathResult) {
        Command newCommand = new Command(pathLog, pathResult);
        UserCommands uCS = usersCommands.get(user);
        if (uCS == null) {
            uCS = new UserCommands(user);
            usersCommands.put(user, uCS);
        }
        uCS.addCommand(newCommand);
        return newCommand;
    }

    public static Command getCommand(String user, String timestamp) throws NoSuchElementException {
        UserCommands uCS = usersCommands.get(user);
        Path pathLog = Tool.getLogPath(timestamp);
        Path pathResult = Tool.getResultPath(timestamp);
        Command command;
        if (uCS == null) {
            command = addCommand(user, pathLog, pathResult);
        } else {
            command = uCS.getCommand(timestamp);
            if (command == null) {
                command = addCommand(user, pathLog, pathResult);
            }
        }
        return command;
    }
}
