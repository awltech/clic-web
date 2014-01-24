package jenkins.plugins.clic.controller;

import com.worldline.clic.utils.mvn.MavenClicCommandLine;
import jenkins.plugins.clic.commands.Command;
import jenkins.plugins.clic.commands.UsersCommands;
import jenkins.plugins.clic.exception.CommandParsingException;
import jenkins.plugins.clic.tools.CommandProcessor;
import jenkins.plugins.clic.tools.Tool;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;


/**
 * User: pierremarot
 * Date: 19/12/2013
 * Time: 15:35
 */
public class CommandHandler {
    private static final Logger LOGGER = Logger.getLogger(CommandHandler.class.getName());

    private MavenClicCommandLine mCCL;
    private OptionParser parser;


    private String splitCommand(String command) {
        String paramsString;
        int indexOfSpace = command.indexOf(" ");
        if (command.indexOf(" ") != -1) {
            paramsString = command.substring(indexOfSpace + 1);
        } else {
            paramsString = command;
        }
        return paramsString;
    }

    @JavaScriptMethod
    public String call(String commandStr) {
        boolean statusOk = true;
        String ret = "";

        //File to store logs
        Path pathLog = Tool.getNextPath();
        Path pathResult = Tool.getResultPath(Tool.getTimestamp(pathLog));
        try {
            Files.createFile(pathLog);
        } catch (FileAlreadyExistsException e) {
            //OK : File already exists
        } catch (IOException e) {
            //permission error occur
            statusOk = false;
            e.printStackTrace();
        }

        /* Get rid of the first space and everything before it (command name)*/
        String paramsString = splitCommand(commandStr);
        OptionSet options = null;

        /* Parse the parameter : second validation level*/
        String[] params = {};
        try {
            params = CommandProcessor.parseCommandLine(paramsString);
            init();
            options = parser.parse(params);
        } catch (Exception e) {
            ret = " during parsing of the command. Please ensure that all parameters have the correct syntax." ;
            e.printStackTrace();
            statusOk = false;
            e.printStackTrace();
        }

        if (statusOk && params.length != 0) {
            Command command = UsersCommands.addCommand(Tool.getUserName(), pathLog, pathResult);
            command.setCommand(commandStr);

            FileOutputHandler iOH = new FileOutputHandler(pathLog);

            CommandExecutor cE = new CommandExecutor(command, options, mCCL.getMavenParameters(), mCCL.getMavenReference(), mCCL.getMavenCommand(), iOH);
            cE.start();
        }

        if (statusOk) {
            ret = Tool.getTimestamp(pathLog);
        } else {
            ret = "ERROR" + ret;
        }
        return ret;
    }

        @JavaScriptMethod
        public CommandLog getLogs(String timestamp) {
    /*public void doGetLogs(StaplerRequest req, StaplerResponse resp) throws ServletException, IOException {
        String timestamp = req.getParameter("0"); */
        String ret = "";
        Command command = UsersCommands.getCommand(Tool.getUserName(), timestamp);

        List<String> list = UsersCommands.getCommandLog(Tool.getUserName(), timestamp);


        Iterator it = list.iterator();
        while (it.hasNext()) {
            ret += (String) it.next();
            if (it.hasNext()) {
                ret += '\n';
            }


        }

        return new CommandLog(ret,command.isFinished());
        //return ret;
        /*resp.setHeader("X-More-Data", !command.isFinished() + "");
        resp.setHeader("Data", ret);
        resp.forwardToPreviousPage(req);*/
    }

    @JavaScriptMethod
    public int getExitCode(String timestamp) {
        Command command;
        try {
            command = UsersCommands.getCommand(Tool.getUserName(), timestamp);
        } catch (NoSuchElementException e) {
            return -1;
        }
        return command.getExitCode();
    }

    private void init() {
        parser = new OptionParser();
        mCCL = new MavenClicCommandLine();
        mCCL.configureParser(parser);
    }

}
