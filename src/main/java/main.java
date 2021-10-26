import api.GTM;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class main {

    public static void main(String ...args) throws GeneralSecurityException, IOException {
        GTM gtm = new GTM();
        gtm.initialize();

        gtm.getAccounts().forEach(i -> System.out.println(i.toString()));

        gtm.getAccount("12312333");

        /*
        System.out.println(gtm.getAccount("6005040042"));

        gtm.setAccountId("6004589006");
        gtm.getContainers().forEach(i -> System.out.println(i.toString()));

        System.out.println(gtm.getContainer());
        */

        /*
                try {
            return tagManager.accounts().get(getAccountPath()).execute();
        } catch (GoogleJsonResponseException e) {
            System.out.println("[STATUS] : " + e.getStatusCode() + " " + e.getStatusMessage());
            System.out.println("[ERROR] : Invalid arguments");

            return null;
        }
         */
    }

}
