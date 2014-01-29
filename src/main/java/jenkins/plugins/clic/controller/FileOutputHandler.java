package jenkins.plugins.clic.controller;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * User: pierremarot
 * Date: 22/01/2014
 * Time: 12:32
 */
public class FileOutputHandler implements InvocationOutputHandler {

    private BufferedWriter bw;

    public FileOutputHandler(Path path){
        try {
            bw = Files.newBufferedWriter(path, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void consumeLine(String s) {
        try {
            bw.write(s + '\n');
            bw.flush();
            //bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeBuffer(){
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
