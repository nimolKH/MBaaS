package com.angkorteam.mbaas.plain.request.collection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Khauv Socheat on 2/15/2016.
 */
public class CollectionPermissionUsernameRequest {

    @Expose
    @SerializedName("collectionName")
    private String collectionName;

    @Expose
    @SerializedName("actions")
    private List<Integer> actions = new LinkedList<>();

    @Expose
    @SerializedName("username")
    private String username;


    public List<Integer> getActions() {
        return actions;
    }

    public void setActions(List<Integer> actions) {
        this.actions = actions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

}
