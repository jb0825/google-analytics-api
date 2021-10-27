package service;

import api.GTM;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.tagmanager.model.Container;
import com.google.api.services.tagmanager.model.Parameter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GTMService {
    public static GTM gtm;

    public GTMService() throws GeneralSecurityException, IOException {
        gtm = new GTM();
        gtm.initialize();
    }

    public Boolean setCafe24EcommerceGTM(String accountId, String UAPropertyId, String containerName) {
        gtm.setAccountId(accountId);

        try {
            Container container = gtm.createContainer(containerName);
        } catch (IOException e) {
            System.out.println("[ERROR] : " + e.getLocalizedMessage());
            return false;
        }

        try {
            // create GA PROPERTY ID Variable
            gtm.createVariable(
                "GA PROPERTY ID",
                "gas",
                Collections.singletonList(new Parameter().setType("template").setKey("trackingId").setValue(UAPropertyId)),
                null
            );

            // create Ecommerce Variables
            String[][] varParams;

        } catch (IOException e) {
            System.out.println("[ERROR] : " + e.getLocalizedMessage());
            return false;
        }

        return true;
    }
}
