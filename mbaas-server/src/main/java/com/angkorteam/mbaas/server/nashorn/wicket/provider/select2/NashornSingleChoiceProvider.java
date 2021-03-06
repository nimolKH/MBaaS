package com.angkorteam.mbaas.server.nashorn.wicket.provider.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.SingleChoiceProvider;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.Session;
import com.google.gson.Gson;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 5/31/16.
 */
public class NashornSingleChoiceProvider extends SingleChoiceProvider<Map<String, Object>> {

    private final Factory factory;

    private final String id;

    private final String script;

    public NashornSingleChoiceProvider(Factory factory, String id, String script) {
        this.factory = factory;
        this.id = id;
        this.script = script;
    }

    @Override
    public final Map<String, Object> toChoice(String id) {
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
            return (Map<String, Object>) invocable.invokeFunction(this.id + "__to_choice", jdbcTemplate, id);
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    @Override
    public final List<Option> query(String term, int page) {
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
            return (List<Option>) invocable.invokeFunction(this.id + "__query", jdbcTemplate, this.factory, term, page);
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    @Override
    public final boolean hasMore(String term, int page) {
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
            return (Boolean) invocable.invokeFunction(this.id + "__has_more", jdbcTemplate, term, page);
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        }
        return false;
    }

    @Override
    public final Gson getGson() {
        return ApplicationUtils.getApplication().getGson();
    }

}
