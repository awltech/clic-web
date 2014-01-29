package jenkins.plugins.clic.controller;

import com.worldline.clic.utils.mvn.MavenClicCommandLine;
import jenkins.plugins.clic.commands.Command;
import jenkins.plugins.clic.commands.UsersCommands;
import jenkins.plugins.clic.tools.CommandProcessor;
import jenkins.plugins.clic.tools.Tool;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * User: pierremarot
 * Date: 19/12/2013
 * Time: 15:35
 */
public class CommandHandler {

    private MavenClicCommandLine mCCL;
    private OptionParser parser;


    private String splitCommand(String command) {
        String paramsString;
        int indexOfSpace = command.indexOf(" ");
        if (command.contains(" ")) {
            paramsString = command.substring(indexOfSpace + 1);
        } else {
            paramsString = command;
        }
        return paramsString;
    }

    @JavaScriptMethod
    @SuppressWarnings("unused")
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
            //Store command in history (only the valid ones for now)
            Tool.addCommandToHistory(commandStr);

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
        @SuppressWarnings("unused")
        public CommandLog getLogs(String timestamp) {
        String ret;
        Command command = UsersCommands.getCommand(Tool.getUserName(), timestamp);

        List<String> list = UsersCommands.getCommandLog(Tool.getUserName(), timestamp);

        ret = makeString(list);

        return new CommandLog(ret,command.isFinished());
    }

    @JavaScriptMethod
    @SuppressWarnings("unused")
    public int getExitCode(String timestamp) {
        Command command;
        try {
            command = UsersCommands.getCommand(Tool.getUserName(), timestamp);
        } catch (NoSuchElementException e) {
            return -1;
        }
        return command.getExitCode();
    }

    @JavaScriptMethod
    @SuppressWarnings("unused")
    public History getHistory(){
      /* return makeString(Tool.getHistory());*/
        return new History(Tool.getHistory());

    }

    private void init() {
        parser = new OptionParser();
        mCCL = new MavenClicCommandLine();
        mCCL.configureParser(parser);
    }

    private String makeString(List<String> list){
        String ret = "";
        Iterator it = list.iterator();
        while (it.hasNext()) {
            ret += (String) it.next();
            if (it.hasNext()) {
                ret += '\n';
            }
        }
        return ret;
    }

    private class History{
        private List<String> history;

        public History(List<String> history){
            this.history = history;
        }

        @SuppressWarnings("unused")
        public List<String> getHistory() {
            return history;
        }
        @SuppressWarnings("unused")
        public void setHistory(List<String> history) {
            this.history = history;
        }
    }

    private class CommandLog{
        private String log;

        private boolean finished;

        public CommandLog(String log,boolean finished){
            this.log = log;
            this.finished = finished;
        }

        @SuppressWarnings("unused")
        public String getLog() {
            return log;
        }

        @SuppressWarnings("unused")
        public void setLog(String log) {
            this.log = log;
        }

        public boolean isFinished() {
            return finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }
    }

}
