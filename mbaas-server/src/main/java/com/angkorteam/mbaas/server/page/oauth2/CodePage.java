package com.angkorteam.mbaas.server.page.oauth2;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ClientTable;
import com.angkorteam.mbaas.model.entity.tables.records.ClientRecord;
import com.angkorteam.mbaas.plain.response.oauth2.OAuth2AuthorizeResponse;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.oauth2.OAuth2Client;
import com.angkorteam.mbaas.server.oauth2.OAuth2DTO;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by socheat on 3/30/16.
 */
@Mount("/oauth2/code")
public class CodePage extends AdminLTEPage {

    private String code;
    private Label codeLabel;

    private String state;
    private Label stateLabel;

    private Button claimAccessTokenButton;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.code = getPageParameters().get("code").toString("");
        this.state = getPageParameters().get("state").toString("");

        this.form = new Form<>("form");
        add(this.form);

        this.codeLabel = new Label("codeLabel", new PropertyModel<>(this, "code"));
        this.form.add(this.codeLabel);

        this.stateLabel = new Label("stateLabel", new PropertyModel<>(this, "state"));
        this.form.add(this.stateLabel);

        this.claimAccessTokenButton = new Button("claimAccessTokenButton");
        this.claimAccessTokenButton.setOnSubmit(this::claimAccessTokenButtonOnSubmit);
        this.form.add(this.claimAccessTokenButton);
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    private void claimAccessTokenButtonOnSubmit(Button button) {
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        OAuth2DTO oAuth2DTO = (OAuth2DTO) getSession().getAttribute(this.state);
        oAuth2DTO.setCode(this.code);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpFunction.getHttpAddress(request))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String clientId = oAuth2DTO.getClientId();

        DSLContext context = getSession().getDSLContext();
        ClientTable clientTable = Tables.CLIENT.as("clientTable");

        ClientRecord clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_ID.eq(oAuth2DTO.getClientId())).fetchOneInto(clientTable);

        String clientSecret = clientRecord.getClientSecret();
        String grantType = "authorization_code";
        String redirectUri = oAuth2DTO.getRedirectUri();

        OAuth2Client client = retrofit.create(OAuth2Client.class);
        Call<OAuth2AuthorizeResponse> responseCall = client.authorize(clientId, clientSecret, grantType, redirectUri, this.code);
        try {
            retrofit2.Response<OAuth2AuthorizeResponse> response = responseCall.execute();
            oAuth2DTO.setAccessToken(response.body().getAccessToken());
            oAuth2DTO.setExpiresIn(response.body().getExpiresIn());
            oAuth2DTO.setRefreshToken(response.body().getRefreshToken());
            oAuth2DTO.setTokenType(response.body().getTokenType());
        } catch (IOException e) {
            throw new WicketRuntimeException(e);
        }
    }

}
