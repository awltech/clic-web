package jenkins.plugins.clic.controller;

import com.worldline.clic.utils.mvn.Maven;
import jenkins.plugins.clic.commands.Command;
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
public class CommandExecutor extends Thread {

    private OptionSet options;
    private OptionSpec<KeyValuePair> mavenParameters;
    private OptionSpec<String> mavenReference;
    private OptionSpec<String> mavenCommand;
    private InvocationOutputHandler iOH;
    private Command command;

    public CommandExecutor(Command command, OptionSet options, OptionSpec<KeyValuePair> mavenParameters, OptionSpec<String> mavenReference, OptionSpec<String> mavenCommand, InvocationOutputHandler iOH) {
        this.options = options;
        this.mavenParameters = mavenParameters;
        this.mavenReference = mavenReference;
        this.mavenCommand = mavenCommand;
        this.iOH = iOH;
        this.command = command;
    }

    public void run() {

        try {
            int exitCode = Maven.executeCommandLine(options, mavenParameters, mavenReference, mavenCommand, iOH);

            command.setExitCode(exitCode);

        } catch (IOException e) {
            iOH.consumeLine("ERROR IO CliC: While writing access log file.");
            command.setExitCode(1);
            e.printStackTrace();
        } catch (MavenInvocationException e) {
            command.setExitCode(1);
            iOH.consumeLine("ERROR Maven CliC: While running maven command.");
            e.printStackTrace();
        } catch (Exception e) {
            //todo : clic-utils should throws an exception when parsing fails
            //somotimes throw here : OptionArgumentConversionException,MissingRequiredOptionException...
            iOH.consumeLine("ERROR during parsing of the command. Please ensure that all parameters have the correct syntax.");
            command.setExitCode(1);
            e.printStackTrace();
        } finally {
            command.finish();
            FileOutputHandler fOH = (FileOutputHandler) iOH;
            fOH.closeBuffer();
            Thread.currentThread().interrupt();
        }
    }
}