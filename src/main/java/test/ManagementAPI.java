package test;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.*;
import com.google.api.services.analytics.model.Accounts;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class ManagementAPI {

    private static Analytics analytics;

    private static final String APPLICATION_NAME = "Hello Analytics";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String KEY_FILE_LOCATION = "key/key_file.json";

    private static final String accountId = "201461977";
    private static final String propertyId = "UA-201461977-1";

    public static void main(String... args) throws IOException, GeneralSecurityException {
        analytics = initializeAnalytic();

        getProps();
        createProfile();
    }



    private static Analytics initializeAnalytic() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = GoogleCredential
                .fromStream(new FileInputStream(KEY_FILE_LOCATION))
                .createScoped(AnalyticsScopes.all());

        // Construct the Analytics service object.
        return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    public static void getViews() throws IOException {
        Accounts accounts = analytics.management().accounts().list().execute();
        Webproperties properties = analytics.management().webproperties().list(accountId).execute();

        Profiles profiles = analytics.management().profiles().list(accountId, propertyId).execute();

        for (Profile profile : profiles.getItems()) {
            if (profile.getName().equals("전체 웹사이트 데이터")) {
                System.out.println(profile.getName() + " " + profile.getId());
            }
        }
    }

    public static void createUAProp() throws IOException {
        String accountId = "198242552";

        Webproperty property = new Webproperty();
        property.setWebsiteUrl("https://www.naver.com");
        property.setName("create UA property test2");

        Webproperty response = analytics.management().webproperties().insert(accountId, property).execute();
    }

    public static void updateProp() throws IOException {
        Webproperty property = new Webproperty();
        property.setWebsiteUrl("https://www.naver.com");
        property.setName("update!!!!");

        Webproperty response = analytics.management().webproperties().update(accountId, propertyId, property).execute();
        System.out.println(response.toString());
    }

    public static void getProps() throws IOException {
        List<Webproperty> properties = analytics.management().webproperties().list(accountId).execute().getItems();
        properties.forEach(i -> {
            i.forEach((k, v) -> {
                System.out.println(k + " : " + v);
            });
        });
    }

    public static void createProfile() throws IOException {
        Profile profile = new Profile();
        profile.setName("보기 생성 테스트");
        profile.setWebsiteUrl("https://www.naver.com");
        profile.setTimezone("Asia/Seoul");
        profile.setCurrency("KRW");
        profile.setECommerceTracking(true);
        profile.setEnhancedECommerceTracking(true);

        Profile response = analytics.management().profiles().insert(accountId, propertyId, profile).execute();
        System.out.println(response.toString());
    }

    public static void updateProfile() throws IOException {
        Profile profile = new Profile();
        profile.setName("보기 수정했습니다");

        Profile response = analytics
                .management().profiles()
                .update(accountId, propertyId, "248956458", profile).execute();
        System.out.println(response.getName());
    }

    public static void deleteProfile() throws IOException {
        analytics.management().profiles().delete(accountId, propertyId, "248956458").execute();
    }

    public static void getProfiles() throws IOException {
        List<Profile> profiles = analytics
                .management().profiles()
                .list(accountId, propertyId).execute().getItems();
        profiles.forEach(i -> System.out.println(i.toString()));
    }
}
