package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public class NashornDropDownChoice extends org.apache.wicket.markup.html.form.DropDownChoice<Map<String, Object>> {

    private String script;

    public NashornDropDownChoice(String id, IModel<Map<String, Object>> model, IModel<? extends List<? extends Map<String, Object>>> choices, IChoiceRenderer<? super Map<String, Object>> renderer) {
        super(id, model, choices, renderer);
    }

    public void registerValidator(String event) {
        NashornValidator validator = new NashornValidator(getId(), event, this.script);
        super.add(validator);
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
