package api;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tagmanager.TagManager;
import com.google.api.services.tagmanager.TagManagerScopes;
import com.google.api.services.tagmanager.model.Account;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Slf4j
public class GTM {
    private static String KEY_FILE_LOCATION = "";

    private TagManager tagManager;
    private String accountId;
    private String containerId;

    /**
     * TagManager 객체 생성
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void initialize() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = GoogleCredential
                .fromStream(new FileInputStream(KEY_FILE_LOCATION))
                .createScoped(TagManagerScopes.all());

        tagManager = new TagManager(httpTransport, GsonFactory.getDefaultInstance(), credential);
    }

    public List<Account> getAccounts() {
        try {
            return tagManager.accounts().list().execute().getAccount();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
