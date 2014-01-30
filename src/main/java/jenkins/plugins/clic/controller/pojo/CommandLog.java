package jenkins.plugins.clic.controller.pojo;

/**
 * User: pierremarot
 * Date: 30/01/2014
 * Time: 11:36
 */
public class CommandLog {

    private String log;

    private boolean finished;

    public CommandLog(String log, boolean finished) {
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
