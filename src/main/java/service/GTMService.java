package service;

import api.GTM;
import com.google.api.services.tagmanager.model.Container;
import com.google.api.services.tagmanager.model.Parameter;
import util.FileReaders;

import java.io.IOException;
import java.security.GeneralSecurityException;
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



        } catch (IOException e) {
            System.out.println("[ERROR] : " + e.getLocalizedMessage());
            return false;
        }

        return true;
    }
}
