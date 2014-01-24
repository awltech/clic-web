package jenkins.plugins.clic.controller;

/**
 * User: pierremarot
 * Date: 24/01/2014
 * Time: 13:14
 */
public class CommandLog {

    private String log;

    private boolean finished;

    public CommandLog(String log,boolean finished){
        this.log = log;
        this.finished = finished;
    }

    public String getLog() {
        return log;
    }

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
