package jenkins.plugins.clic.controller.pojo;

import hudson.model.Saveable;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

/**
 * User: pierremarot
 * Date: 13/02/2014
 * Time: 15:12
 */
public class Alias implements Saveable,Serializable {

    private String name;

    private String command;

    public Alias(String name,String command){
        this.name = name;
        this.command = command;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public JSONObject read(){
        JSONObject json = new JSONObject();
        json.accumulate("name",name).accumulate("command",command);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alias)) return false;

        Alias alias = (Alias) o;

        if (!name.equals(alias.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public void save() throws IOException {
        System.out.print("call");
    }
}
