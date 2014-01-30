package jenkins.plugins.clic.controller.pojo;

/**
 * User: pierremarot
 * Date: 30/01/2014
 * Time: 17:27
 */
public class CommandResult {

    private int exitCode;
    private String command;

    public CommandResult(int exitCode, String command) {
        this.exitCode = exitCode;
        this.command = command;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
