package api;

import com.google.analytics.admin.v1alpha.*;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Date;


@NoArgsConstructor
public class Admin {

    private static AnalyticsAdminServiceClient adminServiceClient;

    public String createAccountTicket(String token, String displayName, String redirectUri) throws IOException {
        Date date = new Date();
        date.setTime(date.getTime() + 1);
        AccessToken accessToken = new AccessToken(token, date);
        OAuth2Credentials credentials = OAuth2Credentials.create(accessToken);

        AnalyticsAdminServiceSettings settings = AnalyticsAdminServiceSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
        adminServiceClient = AnalyticsAdminServiceClient.create(settings);

        Account account = Account.newBuilder().setDisplayName(displayName).setCountryCode("KR").build();

        ProvisionAccountTicketRequest request = ProvisionAccountTicketRequest
                .newBuilder()
                .setAccount(account)
                .setRedirectUri(redirectUri)
                .build();

        ProvisionAccountTicketResponse response = adminServiceClient.provisionAccountTicket(request);

        return response.getAccountTicketId();
    }


}
