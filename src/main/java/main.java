import api.Admin;
import api.GTM;
import com.google.api.services.tagmanager.model.Condition;
import com.google.api.services.tagmanager.model.ContainerVersion;
import com.google.api.services.tagmanager.model.Parameter;
import service.GTMService;
import util.FileReaders;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class main {

    public static void main(String ...args) throws GeneralSecurityException, IOException, InterruptedException {
        GTM gtm = new GTM();
        gtm.initialize();

        gtm.setAccountId("6004589006");
        System.out.println(gtm.getContainers());

        gtm.setContainerId("54144322"); // api test
        System.out.println(gtm.getWorkspaces());
        gtm.setWorkspaceId(gtm.getWorkspaces().get(0).getWorkspaceId());

        System.out.println(gtm.createVersion());

        //System.out.println(gtm.getContainers());

        /*
        GTMService gtmService = new GTMService();
        gtmService.setCafe24EcommerceGTM("6001659788", "UA-154449260-1", "페가시");
         */


        //gtm.publish(containerVersion.getContainerVersionId());


    }

}
