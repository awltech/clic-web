package jenkins.plugins.clic.controller.pojo;

import hudson.model.Saveable;
import net.sf.json.JSONArray;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: pierremarot
 * Date: 13/02/2014
 * Time: 15:13
 */
public class AliasList{

    private List<Alias> aliasses;

    public AliasList() {
        aliasses = new ArrayList<Alias>();
    }

    public List<Alias> getAliasses() {
        return aliasses;
    }


    public void add(Alias alias) {
        if (!aliasses.contains(alias)) {
            aliasses.add(alias);
        }
    }

    public void remove(String name){
        aliasses.remove(new Alias(name,""));
    }

    public JSONArray read(){
        JSONArray jsonArray = new JSONArray();
        for(Alias a:aliasses){
            jsonArray.add(a.read());
        }
        return jsonArray;
    }

}
