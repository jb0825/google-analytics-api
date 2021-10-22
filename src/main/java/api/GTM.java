package api;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tagmanager.TagManager;
import com.google.api.services.tagmanager.TagManagerScopes;
import com.google.api.services.tagmanager.model.Account;
import com.google.api.services.tagmanager.model.Container;
import com.google.api.services.tagmanager.model.Folder;
import com.google.api.services.tagmanager.model.Workspace;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GTM {
    private static String KEY_FILE_LOCATION = "key/key_file.json";

    private TagManager tagManager;
    private String accountId;
    private String containerId;

    public GTM(String accountId) {
        this.accountId = accountId;
    }

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

    private String getContainerPath() { return "accounts/" + accountId + "/containers/" + containerId; }
    private String getWorkspacePath() { return "accounts/" + accountId + "/containers/" + containerId + "/workspaces/2"; }

    /* Account
    * setAccountId(accountId) 호출 후 실행
    * */
    public List<Account> getAccounts() throws IOException { return tagManager.accounts().list().execute().getAccount(); }
    public Account getAccount(String accountId) throws IOException { return tagManager.accounts().get("accounts/" + accountId).execute(); }

    /* Container
    * setAccountId(accountId), setContainerId(containerId) 호출 후 실행
    * */
    public List<Container> getContainers() throws IOException {
        return tagManager.accounts().containers().list("accounts/" + accountId).execute().getContainer();
    }

    public Container getContainer() throws IOException {
        return tagManager.accounts().containers().get(getContainerPath()).execute();
    }

    public Container createContainer(String name) throws IOException {
        Container container = new Container();
        container.setName(name);
        container.setUsageContext(Collections.singletonList("0"));

        return tagManager.accounts().containers().create("accounts/" + accountId, container).execute();
    }

    public void deleteContainer() throws IOException { tagManager.accounts().containers().delete(getContainerPath()).execute(); }

    /* folder
    * setAccountId(accountId), setContainerId(containerId) 호출 후 실행
    * */
    public List<Folder> getFolders() throws IOException {
        return tagManager.accounts().containers().workspaces().folders().list(getWorkspacePath()).execute().getFolder();
    }

    public Folder getFolder(String folderId) throws IOException {
        return tagManager.accounts().containers().workspaces().folders().get(getWorkspacePath() + "/folders/" + folderId).execute();
    }


}
