package com.angkorteam.mbaas.request;

import com.angkorteam.mbaas.request.Request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Khauv Socheat on 2/15/2016.
 */
public class DocumentQueryRequest extends Request {

    @Expose
    @SerializedName("query")
    private Map<String, Object> query = new LinkedHashMap<>();

    public Map<String, Object> getQuery() {
        return query;
    }

    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }

}