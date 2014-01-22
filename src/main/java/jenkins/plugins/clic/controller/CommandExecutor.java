package jenkins.plugins.clic.controller;

import com.worldline.clic.utils.mvn.Maven;
import hudson.slaves.CommandConnector;
import jenkins.plugins.clic.commands.Command;
import jenkins.plugins.clic.commands.UserCommands;
import jenkins.plugins.clic.commands.UsersCommands;
import jenkins.plugins.clic.tools.Tool;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.KeyValuePair;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.IOException;

/**
 * User: pierremarot
 * Date: 22/01/2014
 * Time: 14:09
 */
public class CommandExecutor extends Thread{
    
    private OptionSet options;
    private OptionSpec<KeyValuePair> mavenParameters;
    private OptionSpec<String> mavenReference;
    private OptionSpec<String> mavenCommand;
    private InvocationOutputHandler iOH;
    private Command command;

    public CommandExecutor(Command command,OptionSet options,OptionSpec<KeyValuePair> mavenParameters,OptionSpec<String> mavenReference,OptionSpec<String> mavenCommand,InvocationOutputHandler iOH){
        this.options = options;
        this.mavenParameters = mavenParameters;
        this.mavenReference = mavenReference;
        this.mavenCommand = mavenCommand;
        this.iOH = iOH;
        this.command = command;
    }
    public void run(){

            /* todo : create thread */
        try {
            Maven.executeCommandLine(options, mavenParameters, mavenReference, mavenCommand, iOH);
            command.finish();
        } catch (IOException e) {
            iOH.consumeLine("ERROR IO CliC: While writing access log file");
            e.printStackTrace();
        } catch (MavenInvocationException e) {
            iOH.consumeLine("ERROR Maven CliC: While running maven command");
            e.printStackTrace();
        }
    }
}
