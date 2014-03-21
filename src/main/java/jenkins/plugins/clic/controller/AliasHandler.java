package jenkins.plugins.clic.controller;

import jenkins.plugins.clic.controller.pojo.Alias;
import jenkins.plugins.clic.tools.AliasTool;
import net.sf.json.JSONArray;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import java.io.IOException;
import java.util.List;

/**
 * User: pierremarot
 * Date: 19/12/2013
 * Time: 15:35
 */

public class AliasHandler {

    @JavaScriptMethod
    @SuppressWarnings("unused")
    public JSONArray loadAlias() {

        try {
            List<Alias> list = AliasTool.queryAliasses().getAliasses();
            JSONArray jsonArray = new JSONArray();
            for(Alias a:list){
                jsonArray.add(a.read());
            }
            return jsonArray;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    @JavaScriptMethod
    @SuppressWarnings("unused")
    public int addAlias(String name, String command) {
        int ret = 0;
        Alias alias = new Alias(name, command);

        try {
            AliasTool.addAlias(alias);
        } catch (IOException e) {
            e.printStackTrace();
            ret = 1;
        }
        return ret;
    }

    @JavaScriptMethod
    @SuppressWarnings("unused")
    public int removeAlias(String name) {
        int ret = 0;
        try {
            AliasTool.removeAlias(name);
        } catch (IOException e) {
            e.printStackTrace();
            ret = 1;
        }
        return ret;
    }


}
