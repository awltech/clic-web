package jenkins.plugins.clic;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.RootAction;
import hudson.security.Permission;
import jenkins.plugins.clic.controller.CommandHandler;
import jenkins.plugins.clic.tools.Tool;


@Extension
@SuppressWarnings("unused")
public class Root implements RootAction {

    private static CommandHandler ch;


    public String getIconFileName() {
        if (Tool.getMe() !=null) {
            return "terminal.png";
        } else {
            return null;
        }
    }


    public String getDisplayName() {
        if (Tool.getMe() !=null) {
            return "CLiC";
        } else {
            return null;
        }
    }

    public String getUrlName() {
        return "clic";
    }

    @SuppressWarnings("unused")
    public CommandHandler getCommandHandler() {
        if (ch == null) {
            ch = new CommandHandler();
        }
        return ch;
    }

    @SuppressWarnings("unsued")
    public Permission getRequiredPermission() {
        return Job.CREATE;
    }

}
