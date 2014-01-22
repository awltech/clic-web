package jenkins.plugins.clic.controller;

import com.worldline.clic.utils.mvn.MavenClicCommandLine;
import com.worldline.clic.utils.mvn.Maven;
import hudson.model.AsyncAperiodicWork;
import hudson.model.Build;
import hudson.model.Executor;
import hudson.model.Job;
import jenkins.plugins.clic.commands.Command;
import jenkins.plugins.clic.commands.UsersCommands;
import jenkins.plugins.clic.tools.CommandProcessor;
import jenkins.plugins.clic.exception.CommandParsingException;
import jenkins.plugins.clic.tools.Tool;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.kohsuke.stapler.framework.io.LargeText;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    @JavaScriptMethod
    public String call(String commandStr) {
        String ret = "";
        boolean statusOk = true;

        //File to store logs
        Path path = Tool.getNextPath();
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            //File already exists
        } catch (IOException e) {
            //permission error occur
            statusOk = false;
            e.printStackTrace();
        }

        /* Get rid of the first space and everything before it (command name)*/
        String paramsString = "";
        if (commandStr.indexOf(" ") == 0) {
            paramsString = commandStr.substring(1);
        } else {
            paramsString = commandStr;
        }

        /* Parse the parameter : second validation level*/
        String[] params = {};
        try {
            params = CommandProcessor.parseCommandLine(paramsString);

        } catch (CommandParsingException e) {
            ret = "Error during parsing of the command at : " + e.getMessage();
            statusOk = false;
        }

        if (statusOk && params.length != 0) {
            init();
            Command command = UsersCommands.addCommand(Tool.getUserName(), path);
            FileOutputHandler iOH = new FileOutputHandler(path);
            OptionSet options = parser.parse(params);

            CommandExecutor cE = new CommandExecutor(command,options, mCCL.getMavenParameters(), mCCL.getMavenReference(), mCCL.getMavenCommand(), iOH);
            cE.start();
        }

        if (statusOk) {
            ret = Tool.withoutExtension(path.getFileName().toString());
        } else {
            ret = "ERROR" + ret;
        }
        return ret;
    }

    @JavaScriptMethod
    public String getLogs(String timestamp) {
    /*public void doGetLogs(StaplerRequest req, StaplerResponse resp){
         String timestamp = req.getParameter("0");*/
         Command command = UsersCommands.getCommand(Tool.getUserName(),timestamp);

         List<String> list =  UsersCommands.getCommandLog(Tool.getUserName(),timestamp);

        /*List<String> list = new ArrayList<String>();
        list.add("tata");
        list.add("toto");*/
            String ret = "";
            Iterator it = list.iterator();
            while(it.hasNext()){
                ret += (String) it.next();
                if(it.hasNext()){
                    ret += '\n';
                }
            }

           /* resp.setHeader("X-More-Data","true");
            resp.setHeader("Data",ret);*/
            return ret;
        }



    private void init() {
        parser = new OptionParser();
        mCCL = new MavenClicCommandLine();
        mCCL.configureParser(parser);
    }



}
