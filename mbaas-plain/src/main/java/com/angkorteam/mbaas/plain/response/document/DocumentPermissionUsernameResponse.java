package com.angkorteam.mbaas.plain.response.document;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/19/16.
 */
public class DocumentPermissionUsernameResponse extends Response<DocumentPermissionUsernameResponse.Body> {

    public DocumentPermissionUsernameResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("permission")
        private Integer permission;

        public Integer getPermission() {
            return permission;
        }

        public void setPermission(Integer permission) {
            this.permission = permission;
        }
    }
}
