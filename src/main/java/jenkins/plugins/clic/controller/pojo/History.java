package jenkins.plugins.clic.controller.pojo;

import java.util.List;

/**
 * User: pierremarot
 * Date: 30/01/2014
 * Time: 11:00
 */
public class History {

    private List<String> history;

    public History(List<String> history) {
        this.history = history;
    }

    @SuppressWarnings("unused")
    public List<String> getHistory() {
        return history;
    }

    @SuppressWarnings("unused")
    public void setHistory(List<String> history) {
        this.history = history;
    }
}
