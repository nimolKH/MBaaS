package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.spring.ApplicationContext;
import com.angkorteam.mbaas.server.spring.JavascriptJob;
import com.angkorteam.mbaas.server.spring.JavascriptTrigger;
import jdk.nashorn.api.scripting.ClassFilter;
import org.jooq.DSLContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.context.ServletContextAware;

import javax.script.ScriptEngineFactory;
import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 4/23/16.
 */
public class JavascriptServiceFactoryBean implements FactoryBean<JavascriptServiceFactoryBean.JavascriptService>, InitializingBean, ServletContextAware {

    private JavascriptService javascriptService;

    private ServletContext servletContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.javascriptService = applicationContext.getJavascriptService();
    }

    @Override
    public JavascriptService getObject() throws Exception {
        return this.javascriptService;
    }

    @Override
    public Class<?> getObjectType() {
        return JavascriptService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public static class JavascriptService {

        private final List<String> jobs = new ArrayList<>();

        private final TaskScheduler scheduler;

        private final ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource;

        private final DSLContext context;

        private final ScriptEngineFactory scriptEngineFactory;

        private final ClassFilter classFilter;

        public JavascriptService(DSLContext context, ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource, TaskScheduler scheduler, ScriptEngineFactory scriptEngineFactory, ClassFilter classFilter) {
            this.scheduler = scheduler;
            this.context = context;
            this.applicationDataSource = applicationDataSource;
            this.scriptEngineFactory = scriptEngineFactory;
            this.classFilter = classFilter;
        }

        public void schedule(String applicationCode, String jobId) {
            if (jobs.contains(applicationCode + "=>" + jobId)) {
                return;
            }
            ApplicationTable applicationTable = Tables.APPLICATION.as("applicationUser");
            ApplicationRecord applicationRecord = this.context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.CODE.eq(applicationCode)).fetchOneInto(applicationTable);
            String jdbcUrl = "jdbc:mysql://" + applicationRecord.getMysqlHostname() + ":" + applicationRecord.getMysqlPort() + "/" + applicationRecord.getMysqlDatabase() + "?" + applicationRecord.getMysqlExtra();
            JdbcTemplate jdbcTemplate = this.applicationDataSource.getJdbcTemplate(applicationCode, jdbcUrl, applicationRecord.getMysqlUsername(), applicationRecord.getMysqlPassword());
            Map<String, Object> jobRecord = null;
            jobRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JOB + " WHERE " + Jdbc.Job.JOB_ID + " = ?", jobId);
            if (jobRecord != null) {
                try {
                    this.scheduler.schedule(new JavascriptJob(this.scriptEngineFactory, this.classFilter, this.context, this.applicationDataSource, applicationCode, jobId), new JavascriptTrigger(this.context, this.applicationDataSource, applicationCode, jobId, (String) jobRecord.get("cron")));
                    jobs.add(jobId);
                } catch (IllegalArgumentException e) {
                }
            }
        }
    }
}
