package jenkins.plugins.clic;

import hudson.Extension;
import hudson.model.RootAction;

/**
 * Entry point to all the UI samples.
 * 
 * @author Kohsuke Kawaguchi
 */
@Extension
public class Root implements RootAction{
    public String getIconFileName() {
        return "terminal.png";
    }

    public String getDisplayName() {
        return "CLiC";
    }

    public String getUrlName() {
        return "clic";
    }


}
