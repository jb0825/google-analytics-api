package api;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tagmanager.TagManager;
import com.google.api.services.tagmanager.TagManagerScopes;
import com.google.api.services.tagmanager.model.*;
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

    public void initialize() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = GoogleCredential
                .fromStream(new FileInputStream(KEY_FILE_LOCATION))
                .createScoped(TagManagerScopes.all());

        tagManager = new TagManager(httpTransport, GsonFactory.getDefaultInstance(), credential);
    }

    private String getAccountPath() { return "accounts/" + accountId; }
    private String getContainerPath() { return "accounts/" + accountId + "/containers/" + containerId; }
    private String getWorkspacePath() { return "accounts/" + accountId + "/containers/" + containerId + "/workspaces/2"; }

    /**
     * Account
     */
    public List<Account> getAccounts() throws IOException { return tagManager.accounts().list().execute().getAccount(); }

    public Account getAccount(String accountId) throws IOException { return tagManager.accounts().get(getAccountPath()).execute(); }

    public Account update(String name, boolean shareData) throws IOException {
        Account account = new Account();
        account.setName(name);
        account.setShareData(shareData);

        return tagManager.accounts().update(getAccountPath(), account).execute();
    }

    /**
     * Container
     */
    public List<Container> getContainers() throws IOException { return tagManager.accounts().containers().list(getAccountPath()).execute().getContainer(); }

    public Container getContainer() throws IOException {
        return tagManager.accounts().containers().get(getContainerPath()).execute();
    }

    public Container createContainer(String name) throws IOException {
        Container container = new Container();
        container.setName(name);
        container.setUsageContext(Collections.singletonList("0"));

        return tagManager.accounts().containers().create(getAccountPath(), container).execute();
    }

    public Container updateContainer(String name) throws IOException {
        Container container = new Container();
        container.setName(name);

        return tagManager.accounts().containers().update(getContainerPath(), container).execute();
    }

    public void deleteContainer() throws IOException { tagManager.accounts().containers().delete(getContainerPath()).execute(); }

    /**
     * Folder
     */
    public List<Folder> getFolders() throws IOException {
        return tagManager.accounts().containers().workspaces().folders().list(getWorkspacePath()).execute().getFolder();
    }

    public Folder getFolder(String folderId) throws IOException {
        return tagManager.accounts().containers().workspaces().folders().get(getWorkspacePath() + "/folders/" + folderId).execute();
    }

    public Folder createFolder(String name) throws IOException {
        Folder folder = new Folder();
        folder.setName(name);

        return tagManager.accounts().containers().workspaces().folders().create(getWorkspacePath(), folder).execute();
    }

    public Folder updateFolder(String folderId, String name) throws IOException {
        Folder folder = new Folder();
        folder.setName(name);

        return tagManager.accounts().containers().workspaces().folders().update(getWorkspacePath() + "/folders/" + folderId, folder).execute();
    }

    public void deleteFolder(String folderId) throws IOException {
        tagManager.accounts().containers().workspaces().folders().delete(getWorkspacePath() + "/folders/" + folderId).execute();
    }

    /**
     * Tag
     */
    public List<Tag> getTags() throws IOException {
        return tagManager.accounts().containers().workspaces().tags().list(getWorkspacePath()).execute().getTag();
    }

    public Tag getTag(String tagId) throws IOException {
        return tagManager.accounts().containers().workspaces().tags().get(getWorkspacePath() + "/tags/" + tagId).execute();
    }

    /**
     * 참고 : https://developers.google.com/tag-manager/api/v2/reference/accounts/containers/workspaces/tags/create
     * create UA Tag
     * @param type : "ua"
     * @param params : List<Parameter>

     * @return
     * @throws IOException
     */
    public Tag createTag(String name, String type, List<String> triggerId, List<Parameter> params, String folderId) throws IOException {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setType(type);
        tag.setFiringTriggerId(triggerId);
        tag.setParameter(params);
        tag.setParentFolderId(folderId);

        return tagManager.accounts().containers().workspaces().tags().create(getWorkspacePath(), tag).execute();
    }

    public void deleteTag(String tagId) throws IOException {
        tagManager.accounts().containers().workspaces().tags().delete(getWorkspacePath() + "/tags/" + tagId);
    }

    /**
     * Trigger
     */
    public List<Trigger> getTriggers() throws IOException {
        return tagManager.accounts().containers().workspaces().triggers().list(getWorkspacePath()).execute().getTrigger();
    }

    public Trigger getTrigger(String triggerId) throws IOException {
        return tagManager.accounts().containers().workspaces().triggers().get(getWorkspacePath() + "/triggers/" + triggerId).execute();
    }

    /**
     * create Custom Event Trigger :
     * @param type : "customEvent"
     * @param filters :
     * List<Condition> type : "equals"
     *                 parameter : List<Parameter> type : "template", key : "arg0", value : "{{_event}}"
     *                                             type : "template", key : "arg1", value : event name
     * @param params : null
     * @return
     * @throws IOException
     */
    public Trigger createTrigger(String name, String type, List<Condition> filters, List<Parameter> params, String folderId) throws IOException {
        Trigger trigger = new Trigger();
        trigger.setName(name);
        trigger.setType(type);
        trigger.setParameter(params);
        trigger.setParentFolderId(folderId);

        if (type.equals("customEvent")) { trigger.setCustomEventFilter(filters); }
        else { trigger.setFilter(filters); }

        return tagManager.accounts().containers().workspaces().triggers().create(getWorkspacePath(), trigger).execute();
    }

    public void deleteTrigger(String triggerId) throws IOException {
        tagManager.accounts().containers().workspaces().triggers().delete(getWorkspacePath() + "/triggers/" + triggerId).execute();
    }

    /**
     * Variable
     */
    public List<Variable> getVariables() throws IOException {
        return tagManager.accounts().containers().workspaces().variables().list(getWorkspacePath()).execute().getVariable();
    }

    public Variable getVariable(String variableId) throws IOException {
        return tagManager.accounts().containers().workspaces().variables().get(getWorkspacePath() + "/variables/" + variableId).execute();
    }

    /**
     * create GA PROPERTY ID Variable :
     * @param name : "GA PROPERTY ID"
     * @param type : "gas"
     * @param params : Collections.singletonList(new Parameter().setType("template").setKey("trackingId").setValue(UAPropertyId))
     * @param folderId
     *
     * @return
     * @throws IOException
     */
    public Variable createVariable(String name, String type, List<Parameter> params, String folderId) throws IOException {
        Variable variable = new Variable();
        variable.setName(name);
        variable.setType(type);
        variable.setParameter(params);
        variable.setParentFolderId(folderId);

        return tagManager.accounts().containers().workspaces().variables().create(getWorkspacePath(), variable).execute();
    }

    public void deleteVariable(String variableId) throws IOException {
        tagManager.accounts().containers().workspaces().variables().delete(getWorkspacePath() + "/variables/" + variableId);
    }

    /**
     * Version
     */
    public ContainerVersion getLiveVersion() throws IOException {
        return tagManager.accounts().containers().versions().live(getContainerPath()).execute();
    }

    public ContainerVersion getVersion(String versionId) throws IOException {
        return tagManager.accounts().containers().versions().get(getContainerPath() + "/versions/" + versionId).execute();
    }

    public ContainerVersion publish(String version) throws IOException {
        return tagManager.accounts().containers().versions().publish(getContainerPath() + "/versions/" + version).execute().getContainerVersion();
    }
}
