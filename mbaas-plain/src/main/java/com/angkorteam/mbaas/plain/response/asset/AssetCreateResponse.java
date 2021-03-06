package com.angkorteam.mbaas.plain.response.asset;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 4/9/16.
 */
public class AssetCreateResponse extends Response<AssetCreateResponse.Body> {

    public AssetCreateResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("assetId")
        private String assetId;

        @Expose
        @SerializedName("contentType")
        private String contentType;

        @Expose
        @SerializedName("filename")
        private String filename;

        @Expose
        @SerializedName("address")
        private String address;

        public String getAssetId() {
            return assetId;
        }

        public void setAssetId(String assetId) {
            this.assetId = assetId;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
