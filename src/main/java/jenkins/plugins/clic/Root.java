package jenkins.plugins.clic;

import hudson.Extension;
import hudson.model.RootAction;
import jenkins.plugins.clic.commands.CommandHandler;

/**
 * Entry point to all the UI samples.
 * 
 * @author Kohsuke Kawaguchi
 */
@Extension
public class Root implements RootAction{

    private static CommandHandler ch;


    public String getIconFileName() {
        return "terminal.png";
    }

    public String getDisplayName() {
        return "CLiC";
    }

    public String getUrlName() {
        return "clic";
    }


    public CommandHandler getCommandHandler(){
        if(ch == null){
            ch = new CommandHandler();
        }
        return ch;
    }

}
