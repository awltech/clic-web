package jenkins.plugins.clic.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * User: pierremarot
 * Date: 19/01/2014
 * Time: 12:28
 */
public class Command {

    private boolean isFinished;
    private int exitCode;
    private Path pathLog;
    private Path pathResult;
    private BufferedReader buffer = null;
    private String command;

    public Command(Path pathLog, Path pathResult) {
        this.pathLog = pathLog;
        this.pathResult = pathResult;
        this.isFinished = Files.exists(pathResult);


        if (!isFinished) {
            this.exitCode = -1;
            try {
                buffer = Files.newBufferedReader(pathLog, Charset.defaultCharset());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedReader bR = Files.newBufferedReader(pathResult, Charset.defaultCharset());
                exitCode = bR.read();
                bR.close();
                //todo : result files and so... init exit code
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getTimestamp() {
        return pathLog.getParent().getFileName().toString();
    }

    public List<String> getLogs() {
        List<String> ret = new ArrayList<>();
        if (buffer == null) {
            try {

                ret = Files.readAllLines(pathLog, Charset.defaultCharset());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String line;
            try {
                while ((line = buffer.readLine()) != null) {
                    ret.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }


    public void complete() {
        isFinished = true;
    }

    public void finish() {
        isFinished = true;
    }

    public boolean isFinished() {

        return isFinished;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        closeBuffer();
        saveResult();
        return exitCode;

    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    private void closeBuffer() {
        try {
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveResult() {
        try {
            Path path = Files.createFile(pathResult);
            BufferedWriter writer = Files.newBufferedWriter(path, Charset.defaultCharset());
            writer.write(exitCode + "");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
