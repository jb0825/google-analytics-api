package test;

import java.io.IOException;
import java.net.URISyntaxException;

public class OAuthURI {
    /**
     *   location.href =
     *     "https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?" +
     *     "client_id=" +
     *     oAuthId +
     *     "&redirect_uri=" +
     *     oAuthRedirect +
     *     "&response_type=code" +
     *     "&scope=" +
     *     oAuthScope +
     *     "&approval_prompt=force" +
     *     "&flowName=GeneralOAuthFlow";
     */
    public static void main(String... args) {
        String clientId = "1044853970573-kj20f2d6aoddu2udqmk3goeqa5q9aj59.apps.googleusercontent.com";
        String redirectURI = "http://localhost/user/oauth/redirect";
        String scope = "https://www.googleapis.com/auth/analytics.edit https://www.googleapis.com/auth/analytics.manage.users https://www.googleapis.com/auth/analytics.provision https://www.googleapis.com/auth/tagmanager.edit.containers";

        String oauthURI =
                "https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?client_id=" + clientId +
                        "&redirect_uri=" + redirectURI +
                        "&response_type=code&scope=" + scope +
                        "&approval_prompt=force&flowName=GeneralOAuthFlow"
                                .replaceAll(" ", "%20");

    }
}
