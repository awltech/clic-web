package jenkins.plugins.clic.tools;

import hudson.Extension;
import hudson.model.Hudson;
import hudson.model.User;
import jenkins.model.Jenkins;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Extension
public class Tool{

    private static final String ROOT_DIRECTORY = "/tmp/clic";
    private static final String BASE_DIRECTORY = "users";
    private static final Logger LOGGER = Logger.getLogger(Tool.class.getName());

    public static String getUserName(){
       return getMe().toString();
    }

    public static User getMe(){
        try {
            return Jenkins.getInstance().getMe();
        } catch (Exception e) {
            try {
                return Jenkins.getInstance().getUser(Hudson.ANONYMOUS.getName());
            } catch (Exception e2) {
                return null;
            }
        }
    }

    public static Path getNextPath(){
        //change this with the home jenkins directory

        long now = new Date().getTime();
        Path path = Paths.get(ROOT_DIRECTORY,BASE_DIRECTORY,getUserName(),now + ".log");

        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            // directory already exists
        }
        return path;
    }

    public static List<Path> getAllCommands(){
        List<Path> paths = new ArrayList<Path>();
        try (DirectoryStream<Path> ds =
                     Files.newDirectoryStream(Paths.get(ROOT_DIRECTORY, BASE_DIRECTORY, getUserName()))) {
            for(Path path :ds){
                paths.add(path);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    public static Writer getNewWriter(Path path){
        Writer writer = null;
        try {
            writer = Files.newBufferedWriter(path, Charset.defaultCharset(), StandardOpenOption.CREATE_NEW);
        } catch (IOException ex) {
            LOGGER.info("FATAL ERROR while creating files :" + path.toString() + "\n\n");
            ex.printStackTrace();
        }
        return writer;
    }

    public static String withoutExtension(String fileName){
       return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}