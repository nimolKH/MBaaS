package com.angkorteam.mbaas.server.page.cms.master;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.HtmlTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.JobNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

/**
 * Created by socheat on 5/26/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/cms/master/modify")
public class MasterModifyPage extends MasterPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterModifyPage.class);

    private String masterPageId;

    private String javascript;
    private JavascriptTextArea javascriptField;
    private TextFeedbackPanel javascriptFeedback;

    private String code;
    private Label codeLabel;

    private String html;
    private HtmlTextArea htmlField;
    private TextFeedbackPanel htmlFeedback;

    private String title;
    private TextField<String> titleField;
    private TextFeedbackPanel titleFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Modify Layout";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.form = new Form<>("form");
        add(this.form);

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        this.masterPageId = getPageParameters().get("masterPageId").toString("");
        Map<String, Object> pageRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.MASTER_PAGE + " WHERE " + Jdbc.MasterPage.MASTER_PAGE_ID + " = ?", this.masterPageId);

        this.title = (String) pageRecord.get(Jdbc.MasterPage.TITLE);
        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.add(new JobNameValidator(getSession().getApplicationCode()));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.code = (String) pageRecord.get(Jdbc.MasterPage.CODE);
        this.codeLabel = new Label("codeLabel", new PropertyModel<>(this, "code"));
        this.form.add(codeLabel);

        this.javascript = (String) pageRecord.get(Jdbc.MasterPage.STAGE_JAVASCRIPT);
        this.javascriptField = new JavascriptTextArea("javascriptField", new PropertyModel<>(this, "javascript"));
        this.javascriptField.setRequired(true);
        this.form.add(this.javascriptField);
        this.javascriptFeedback = new TextFeedbackPanel("javascriptFeedback", this.javascriptField);
        this.form.add(this.javascriptFeedback);

        this.description = (String) pageRecord.get(Jdbc.MasterPage.DESCRIPTION);
        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.html = (String) pageRecord.get(Jdbc.MasterPage.STAGE_HTML);
        this.htmlField = new HtmlTextArea("htmlField", new PropertyModel<>(this, "html"));
        this.htmlField.setRequired(true);
        this.form.add(this.htmlField);
        this.htmlFeedback = new TextFeedbackPanel("htmlFeedback", this.htmlField);
        this.form.add(this.htmlFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        Map<String, Object> wheres = new HashMap<>();
        wheres.put(Jdbc.MasterPage.MASTER_PAGE_ID, this.masterPageId);
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.MasterPage.TITLE, this.title);
        fields.put(Jdbc.MasterPage.DESCRIPTION, this.description);
        fields.put(Jdbc.MasterPage.STAGE_JAVASCRIPT, this.javascript);
        fields.put(Jdbc.MasterPage.STAGE_HTML, this.html);
        fields.put(Jdbc.MasterPage.MODIFIED, true);
        fields.put(Jdbc.MasterPage.DATE_MODIFIED, new Date());

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(Jdbc.MASTER_PAGE);
        jdbcUpdate.execute(fields, wheres);

        List<String> pageIds = jdbcTemplate.queryForList("SELECT " + Jdbc.Page.PAGE_ID + " FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.MASTER_PAGE_ID + " = ?", String.class, masterPageId);
        {
            List<String> temp = new ArrayList<>();
            for (String pageId : pageIds) {
                temp.add(com.angkorteam.mbaas.server.page.MasterPage.class.getName() + "_" + pageId + "-stage" + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html");
            }
            for (String cacheKey : temp) {
                getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
            }
        }

        String cacheKey = com.angkorteam.mbaas.server.page.MasterPage.class.getName() + "_" + this.masterPageId + "-stage" + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
        getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
        setResponsePage(MasterManagementPage.class);
    }

}
