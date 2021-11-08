package api;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.tagmanager.TagManagerScopes;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class Reporting {
    private static String KEY_FILE_LOCATION = "key/key_file.json";

    private AnalyticsReporting reporting;
    private Analytics analytics;

    public void reportingInit() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = GoogleCredential
                .fromStream(new FileInputStream(KEY_FILE_LOCATION))
                .createScoped(AnalyticsScopes.all());

    }

}
