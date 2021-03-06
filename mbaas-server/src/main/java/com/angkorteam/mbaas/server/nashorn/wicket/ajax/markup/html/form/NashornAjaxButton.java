package com.angkorteam.mbaas.server.nashorn.wicket.ajax.markup.html.form;

import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;

/**
 * Created by socheat on 5/31/16.
 */
public class NashornAjaxButton extends org.apache.wicket.ajax.markup.html.form.AjaxButton {

    private String script;

    private Map<String, Object> userModel;

    private Factory factory;

    public NashornAjaxButton(String id) {
        super(id);
    }

    public NashornAjaxButton(String id, IModel<String> model) {
        super(id, model);
    }

    public NashornAjaxButton(String id, Form<?> form) {
        super(id, form);
    }

    public NashornAjaxButton(String id, IModel<String> model, Form<?> form) {
        super(id, model, form);
    }

    public Map<String, Object> getUserModel() {
        return userModel;
    }

    public void setUserModel(Map<String, Object> userModel) {
        this.userModel = userModel;
    }

    @Override
    protected final void onSubmit(AjaxRequestTarget target, Form<?> form) {
        Session session = (Session) Session.get();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());

        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
        if (this.script != null || !"".equals(this.script)) {
            try {
                scriptEngine.eval(this.script);
            } catch (ScriptException e) {
            }
        }
        Invocable invocable = (Invocable) scriptEngine;
        try {
            invocable.invokeFunction(getId() + "__on_submit", RequestCycle.get(), jdbcTemplate, this.factory, target, form, this.userModel);
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        } catch (EmptyResultDataAccessException e) {
        }
    }

    @Override
    protected final void onError(AjaxRequestTarget target, Form<?> form) {
        Session session = (Session) Session.get();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());

        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
        if (this.script != null || !"".equals(this.script)) {
            try {
                scriptEngine.eval(this.script);
            } catch (ScriptException e) {
            }
        }
        Invocable invocable = (Invocable) scriptEngine;
        try {
            invocable.invokeFunction(getId() + "__on_error", RequestCycle.get(), jdbcTemplate, this, target, form, this.userModel);
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        } catch (EmptyResultDataAccessException e) {
        }
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }
}
