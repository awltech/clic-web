package jenkins.plugins.clic.tools;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import hudson.XmlFile;
import hudson.util.XStream2;
import jenkins.plugins.clic.controller.pojo.Alias;
import jenkins.plugins.clic.controller.pojo.AliasList;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * User: pierremarot
 * Date: 13/02/2014
 * Time: 16:36
 */
public class AliasTool {


    public static void addAlias(Alias alias) throws IOException {

        AliasList aliasses = queryAliasses();
        aliasses.add(alias);
        saveAliasList(aliasses);

    }

    public static void removeAlias(String name) throws IOException {

        AliasList aliasses = queryAliasses();
        aliasses.remove(name);
        saveAliasList(aliasses);
    }

    public static AliasList queryAliasses() throws IOException {
        Path path = Tool.getAliasPath();
        XStream xs = new XStream(null,new XppDriver(),AliasList.class.getClassLoader());
        if (!Files.exists(path)) {
            AliasList aliasses  = new AliasList();
            Writer writer = Tool.getNewWriter(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            xs.toXML(aliasses, writer);
            writer.flush();
            writer.close();
            return aliasses;
        }

        InputStream in = Files.newInputStream(path,StandardOpenOption.READ);
        AliasList aliasList =  (AliasList) xs.fromXML(in);
        return aliasList;
    }

    public static void saveAliasList(AliasList aliasses) throws IOException {
        Path path = Tool.getAliasPath();

       if (!Files.exists(path)) {
           Files.createFile(path);
       }
        XStream xs = new XStream();
        Writer writer = Tool.getNewWriter(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        xs.toXML(aliasses, writer);
        writer.flush();
        writer.close();
    }

}
