package com.angkorteam.mbaas.server.page.flow;

import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.nashorn.JavaFilter;
import com.angkorteam.mbaas.server.nashorn.JavascripUtils;
import com.angkorteam.mbaas.server.nashorn.function.IOnBeforeRender;
import com.angkorteam.mbaas.server.nashorn.function.IOnInitialize;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 5/28/16.
 */
@Mount("/flow")
public class FlowPage extends MasterPage {

    private String pageId;

    private IOnInitialize iOnInitialize;

    private IOnBeforeRender iOnBeforeRender;

    private Factory factory;

    private Map<String, Object> userModel;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.userModel = new HashMap<>();
        this.factory = new Factory(this, this.userModel);
        this.pageId = getRequest().getQueryParameters().getParameterValue("pageId").toString("");
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine engine = factory.getScriptEngine(new JavaFilter(getDSLContext()));
        Bindings bindings = engine.createBindings();
        engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        JavascripUtils.eval(engine);
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", this.pageId);
        try {
            engine.eval((String) pageRecord.get(Jdbc.Page.JAVASCRIPT));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Invocable invocable = (Invocable) engine;
        this.iOnInitialize = invocable.getInterface(IOnInitialize.class);
        this.iOnBeforeRender = invocable.getInterface(IOnBeforeRender.class);
        if (this.iOnInitialize != null) {
            this.iOnInitialize.onInitialize(this.factory, this.userModel);
        }
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (this.iOnBeforeRender != null) {
            this.iOnBeforeRender.onBeforeRender(this.factory, this.userModel);
        }
    }

    @Override
    public String getVariation() {
        return this.pageId;
    }

}
