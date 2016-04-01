package com.angkorteam.mbaas.server.page.oauth2;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.plain.response.monitor.MonitorTimeResponse;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.oauth2.OAuth2Client;
import com.angkorteam.mbaas.server.oauth2.OAuth2DTO;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by socheat on 3/30/16.
 */
@Mount("/oauth2/access/token")
public class AccessTokenPage extends AdminLTEPage {

    private String accessToken;
    private Label accessTokenLabel;

    private String serverTime;
    private Label serverTimeLabel;

    private Button serverTimeButton;

    private String state;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.state = getPageParameters().get("state").toString("");

        this.form = new Form<>("form");
        add(this.form);

        OAuth2DTO oauth2DTO = (OAuth2DTO) getSession().getAttribute(this.state);
        if (oauth2DTO != null) {
            this.accessToken = oauth2DTO.getAccessToken();
        }
        this.accessTokenLabel = new Label("accessTokenLabel", new PropertyModel<>(this, "accessToken"));
        this.form.add(this.accessTokenLabel);

        this.serverTimeLabel = new Label("serverTimeLabel", new PropertyModel<>(this, "serverTime"));
        this.form.add(this.serverTimeLabel);

        this.serverTimeButton = new Button("serverTimeButton");
        this.serverTimeButton.setOnSubmit(this::serverTimeButtonOnSubmit);
        this.form.add(this.serverTimeButton);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        OAuth2DTO oauth2DTO = (OAuth2DTO) getSession().getAttribute(this.state);
        if (oauth2DTO == null) {
            setResponsePage(StarterPage.class);
        }
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    private void serverTimeButtonOnSubmit(Button button) {
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        OAuth2DTO oauth2DTO = (OAuth2DTO) getSession().getAttribute(this.state);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpFunction.getHttpAddress(request))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OAuth2Client client = retrofit.create(OAuth2Client.class);
        Call<MonitorTimeResponse> responseCall = client.monitorTime("Bearer " + oauth2DTO.getAccessToken());
        try {
            this.serverTime = responseCall.execute().body().getData();
        } catch (IOException e) {
        }
    }

}