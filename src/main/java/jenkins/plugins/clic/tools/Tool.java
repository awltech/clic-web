package jenkins.plugins.clic.tools;

import hudson.model.Hudson;
import hudson.model.User;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tool {

    private static final String BASE_DIRECTORY = "users";
    private static final String CLIC_DIRECTORY = "CliC";
    private static final String HISTORY = "history.txt";
    private static final int NUMBER_OF_COMMAND_STORED = 10;

    public static String getUserName() {
        return getMe().toString();
    }

    public static User getMe() {
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

    private static String getRootDirectory() {
        return Jenkins.getInstance().getRootDir().getAbsolutePath();
    }

    public static Path getNextPath() {
        //change this with the home jenkins directory

        long now = new Date().getTime();
        return getLogPath(now + "");
    }

    //Not Used anymore
    public static Path gethPath(String timestamp) {
        //change this with the home jenkins directory

        Path path = Paths.get(getRootDirectory(), BASE_DIRECTORY, getUserName(), CLIC_DIRECTORY, timestamp);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            // directory already exists
        }
        return path;

    }

    public static Path getLogPath(String timestamp) {
        Path path = Paths.get(getRootDirectory(), BASE_DIRECTORY, getUserName(), CLIC_DIRECTORY, timestamp, "log.txt");
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            // directory already exists
        }
        return path;

    }

    public static Path getResultPath(String timestamp) {
        Path path = Paths.get(getRootDirectory(), BASE_DIRECTORY, getUserName(), CLIC_DIRECTORY, timestamp, "result.xml");
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            // directory already exists
        }
        return path;
    }

    public static Path getHistoryPath() {
        return Paths.get(getRootDirectory(), BASE_DIRECTORY, getUserName(), CLIC_DIRECTORY, HISTORY);
    }

    public static List<String> getHistory() {
        List<String> ret;
        Path path = getHistoryPath();
        try {
            if (Files.exists(path)) {
                ret = Files.readAllLines(path, Charset.defaultCharset());
            } else {
                ret = new ArrayList<>();
                ret.add("No history");
            }

        } catch (IOException e) {
            ret = new ArrayList<>();
            ret.add("Problem while loading history");
        }
        return ret;
    }

    public static void addCommandToHistory(String command) {
        Path path = getHistoryPath();
        Writer writer;
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            List<String> history = Files.readAllLines(path, Charset.defaultCharset());

            if (history.size() < NUMBER_OF_COMMAND_STORED) {
                writer = getNewWriter(path, StandardOpenOption.APPEND,StandardOpenOption.WRITE);
            } else {
                history.remove(0);

                writer = getNewWriter(path, StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE);
                for (String c : history) {
                    writer.write(c + '\n');
                }
            }
            writer.write(command + '\n');
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTimestamp(Path path) {
        return path.getParent().getFileName().toString();
    }

    public static List<Path> getAllCommands() {
        List<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> ds =
                     Files.newDirectoryStream(Paths.get(getRootDirectory(), BASE_DIRECTORY, getUserName()))) {
            for (Path path : ds) {
                paths.add(path);
            }

        } catch (IOException e) {
            //e.printStackTrace();
        }
        return paths;
    }

    public static Writer getNewWriter(Path path, StandardOpenOption... mode) throws IOException {
        return Files.newBufferedWriter(path, Charset.defaultCharset(), mode);

    }

    public static String withoutExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}