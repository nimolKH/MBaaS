package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.response.UnknownResponse;
import com.angkorteam.mbaas.server.MBaaS;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class ExecutionTimeHandlerInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);

    private Gson gson;

    private final Map<String, Long> executions = new java.util.HashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        Boolean maintenance = configuration.getBoolean(Constants.MAINTENANCE, false);
        String id = request.getSession(true).getId();
        if (maintenance) {
            UnknownResponse responseBody = ResponseUtils.unknownResponse(request, HttpStatus.SERVICE_UNAVAILABLE);
            byte[] json = gson.toJson(responseBody).getBytes();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setContentLength(json.length);
            response.getOutputStream().write(json);
            return false;
        }
        executions.put(id, System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String id = request.getSession(true).getId();
        Long execution = executions.get(id);
        executions.put(id, System.currentTimeMillis() - execution);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String id = request.getSession(true).getId();
        double consume = executions.remove(id);
        LOGGER.info("{} consumed {} ss", request.getRequestURI(), consume / 1000f);
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }
}