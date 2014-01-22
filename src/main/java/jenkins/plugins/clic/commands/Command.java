package jenkins.plugins.clic.commands;

import jenkins.plugins.clic.tools.Tool;

import java.io.BufferedReader;
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

    private Path path;
    private BufferedReader buffer = null;

    public Command(Path path, boolean completed) {
        this.path = path;
        this.isFinished = completed;

        if (!isFinished) {
            try {
                buffer = Files.newBufferedReader(path, Charset.defaultCharset());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Command(Path path) {
        this(path, false);
    }


    public String getFileName() {
        return path.getFileName().toString();
    }

    public String getTimestamp() {
        return Tool.withoutExtension(getFileName());
    }

    public List<String> getLogs() {
        List<String> ret = new ArrayList<String>();
        if (buffer == null) {
            try {

                ret = Files.readAllLines(path, Charset.defaultCharset());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String line;
            try {
                while((line = buffer.readLine()) != null){
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

    public void finish(){
        isFinished = true;
    }
    public boolean isFinished() {

        return isFinished;
    }

}
