package jenkins.plugins.clic.commands;

import jenkins.plugins.clic.internal.CommandProcessor;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: pierremarot
 * Date: 19/12/2013
 * Time: 15:35
 */
public class CommandHandler {
    private static final Logger LOGGER = Logger.getLogger(CommandHandler.class.getName());
    private static final String userId = "abc" + "_" + new Date().getTime();


    @JavaScriptMethod
    public int call(String cmd) {

        LOGGER.fine("Hello " + userId + " \n" + cmd + " \n ");

        CommandContext cc = new CommandContext(getWriter());

        CommandProcessor cp = new CommandProcessor(cmd,cc);
        cp.run();
        List<String> output =  cc.getOutputs();
        for(String o:output){
            System.err.println(o);
        }
        try {
            cc.getWriter().close();
        } catch (IOException e) {
            return -1;
        }
        System.out.println("Hello \n\n" + cmd);
        return 666;
    }

    public Writer getWriter(){
        Writer writer = null;
        try {
        writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(userId), "utf-8"));
        } catch (IOException ex) {
            // report
        }
        return writer;
    }
}
