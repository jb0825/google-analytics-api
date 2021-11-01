package service;

import api.GTM;
import com.google.api.services.tagmanager.model.Condition;
import com.google.api.services.tagmanager.model.Container;
import com.google.api.services.tagmanager.model.Parameter;
import util.FileReaders;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GTMService {
    public static GTM gtm;
    public static FileReaders fileReaders;

    public GTMService() throws GeneralSecurityException, IOException {
        gtm = new GTM();
        gtm.initialize();

        fileReaders = new FileReaders();
    }

    public Boolean setCafe24EcommerceGTM(String accountId, String UAPropertyId, String containerName, Map<String, String> variables) {
        gtm.setAccountId(accountId);

        try {
            // create GTM Container
            Container container = gtm.createContainer(containerName);

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

            // create UA Triggers & Tags
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

            for (int i = 0; i < customEventData.size(); i++) {
                String name = customEventData.get(i);
                String event = customEventData.get(i++);

                List<Parameter> UATagParams = new ArrayList<>();
                for(String[] d : UATagData)
                    UATagParams.add(new Parameter().setType(d[0]).setKey(d[1]).setValue(d[2]));
                UATagParams.set(3, UATagParams.get(3).setValue(event));

                List<Parameter> customEventFilterParams = new ArrayList<>();
                customEventFilterParams.add(new Parameter().setType("template").setKey("arg0").setValue("{{_event}}"));
                customEventFilterParams.add(new Parameter().setType("templete").setKey("arg1").setValue(event));

                gtm.createTag(
                    "GA " + name,
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



        } catch (IOException e) {
            System.out.println("[ERROR] : " + e.getLocalizedMessage());
            return false;
        }

        return true;
    }
}
