package service;

import api.GTM;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.tagmanager.model.Condition;
import com.google.api.services.tagmanager.model.Container;
import com.google.api.services.tagmanager.model.Parameter;
import com.google.api.services.tagmanager.model.Workspace;
import util.FileReaders;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GTMService {
    public static GTM gtm;
    public static FileReaders fileReaders;

    public GTMService() throws GeneralSecurityException, IOException {
        gtm = new GTM();
        gtm.initialize();

        fileReaders = new FileReaders();
    }

    public Boolean setCafe24EcommerceGTM(String accountId, String UAPropertyId, String containerName) throws InterruptedException {
        gtm.setAccountId(accountId);

        try {
            // create GTM Container
            Container container = gtm.createContainer(containerName);
            gtm.setContainerId(container.getContainerId());

            // set Workspace Id
            gtm.setWorkspaceId(gtm.getWorkspaces().get(0).getWorkspaceId());

            // create GA PROPERTY ID Variable
            gtm.createVariable(
                    "GA PROPERTY ID",
                    "gas",
                    Collections.singletonList(new Parameter().setType("template").setKey("trackingId").setValue(UAPropertyId)),
                    null
            );

            // create Ecommerce Variables
            List<String> varData = fileReaders.readFile("cafe24_variables");

            for (int i = 0; i < varData.size(); i++) {
                gtm.createVariable(
                    varData.get(i++),
                    "jsm",
                    Collections.singletonList(new Parameter().setType("template").setKey("javascript").setValue(varData.get(i))),
                    null
                );
            }

            // create Custom Event Triggers & UA Tags
            // 애널리틱스 이벤트 태그
            List<String> customEventData = fileReaders.readFile("cafe24_customEvent");

            String[][] UATagData = {
                {"template", "trackType", "TRACK_EVENT"},
                {"template", "trackingId", UAPropertyId},
                {"template", "eventCategory", "ecommerce"},
                {"template", "eventAction", null},
                {"template", "gaSettings", "{{GA PROPERTY ID}}"},
                {"boolean", "useEcommerceDataLayer", "true"},
                {"boolean", "enableEcommerce", "true"}
            };
            List<Parameter> UATagParams = new ArrayList<>();
            for(String[] d : UATagData)
                UATagParams.add(new Parameter().setType(d[0]).setKey(d[1]).setValue(d[2]));

            List<Parameter> customEventFilterParams = new ArrayList<>();
            customEventFilterParams.add(new Parameter().setType("template").setKey("arg0").setValue("{{_event}}"));
            customEventFilterParams.add(new Parameter().setType("template").setKey("arg1").setValue(""));

            for (int i = 0; i < customEventData.size(); i++) {
                String name = customEventData.get(i);
                String event = customEventData.get(++i);

                UATagParams.set(3, UATagParams.get(3).setValue(event));
                customEventFilterParams.set(1, customEventFilterParams.get(1).setValue(event));

                gtm.createTag(
                    "GA - " + name,
                    "ua",
                    Collections.singletonList(
                        gtm.createTrigger(
                            name,
                            "customEvent",
                            Collections.singletonList(new Condition().setType("equals").setParameter(customEventFilterParams)),
                            null,
                            null
                        ).getTriggerId()
                    ),
                    UATagParams,
                    null
                );
            }
            // create DOM Ready Triggers & HTML Tag
            // 이벤트 전송 HTML 태그
            List<String> domReadyData = fileReaders.readFile("cafe24_ecommerceDomReady");

            List<Parameter> domReadyParams = new ArrayList<>();
            domReadyParams.add(new Parameter().setType("template").setKey("arg0").setValue("{{Page Path}}"));
            domReadyParams.add(new Parameter().setType("template").setKey("arg1").setValue(""));

            for (int i = 0; i < domReadyData.size(); i++) {
                String name = domReadyData.get(i);

                domReadyParams.set(1, domReadyParams.get(1).setValue(domReadyData.get(++i)));

                gtm.createTag(
                    "GA - " + name,
                    "html",
                    Collections.singletonList(
                        gtm.createTrigger(
                            "DOM Ready - " + name,
                            "domReady",
                            Collections.singletonList(new Condition().setType("contains").setParameter(domReadyParams)),
                            Collections.singletonList(new Parameter().setType("boolean").setKey("dom").setValue("false")),
                            null
                        ).getTriggerId()
                    ),
                    Collections.singletonList(new Parameter().setType("template").setKey("html").setValue(domReadyData.get(++i))),
                    null
                );
            }

            // create DOM Ready Triggers & UA Tags
            // 회원가입 이벤트 태그
            List<String> joinDomReadyData = fileReaders.readFile("cafe24_memberDomReady");
            String[][] JoinTagData = {
                    {"template", "trackType", "TRACK_EVENT"},
                    {"template", "trackingId", UAPropertyId},
                    {"template", "eventCategory", "member"},
                    {"template", "eventAction", null},
                    {"template", "gaSettings", "{{GA PROPERTY ID}}"},
            };
            List<Parameter> JoinTagParams = new ArrayList<>();
            for(String[] d : JoinTagData)
                JoinTagParams.add(new Parameter().setType(d[0]).setKey(d[1]).setValue(d[2]));

            for (int i = 0; i < joinDomReadyData.size(); i++) {
                String name = joinDomReadyData.get(i);

                JoinTagParams.set(3, JoinTagParams.get(3).setValue(joinDomReadyData.get(++i)));
                domReadyParams.set(1, domReadyParams.get(1).setValue(joinDomReadyData.get(++i)));

                gtm.createTag(
                    "GA - 이벤트 - " + name,
                    "ua",
                    Collections.singletonList(
                        gtm.createTrigger(
                            "DOM Ready - " + name,
                            "domReady",
                            Collections.singletonList(new Condition().setType("contains").setParameter(domReadyParams)),
                            Collections.singletonList(new Parameter().setType("boolean").setKey("dom").setValue("false")),
                            null
                        ).getTriggerId()
                    ),
                    JoinTagParams,
                    null
                );
            }


            // create All Page View Tag
            UATagParams.set(0, UATagParams.get(0).setValue("TRACK_PAGEVIEW"));

            gtm.createTag(
                "All Page View",
                "ua",
                Collections.singletonList("2147479553"),
                UATagParams,
                null
            );

            // Container Version Publish
            gtm.publish("1");

        } catch (IOException e) {
            System.out.println("[ERROR] : " + e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
