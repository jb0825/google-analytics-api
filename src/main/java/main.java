import api.GTM;
import com.google.api.services.tagmanager.model.Parameter;
import util.FileReaders;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class main {

    public static void main(String ...args) throws GeneralSecurityException, IOException {
        GTM gtm = new GTM();
        gtm.initialize();

        gtm.setAccountId("6004589006");
        gtm.setContainerId("53606788"); // 1_1268


    }

}
