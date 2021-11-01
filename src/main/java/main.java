import api.GTM;
import com.google.api.services.tagmanager.model.Condition;
import com.google.api.services.tagmanager.model.Parameter;
import util.FileReaders;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class main {

    public static void main(String ...args) throws GeneralSecurityException, IOException {
        GTM gtm = new GTM();
        gtm.initialize();

        gtm.setAccountId("6004589006");
        gtm.setContainerId("53606788"); // 1_1268

        List<Parameter> filterParams = new ArrayList<>();
        filterParams.add(new Parameter().setType("template").setKey("arg0").setValue("{{_event}}"));
        filterParams.add(new Parameter().setType("template").setKey("arg1").setValue("이벤트명"));
        List<Condition> filters = new ArrayList<>();
        filters.add(new Condition().setType("equals").setParameter(filterParams));

        gtm.createTrigger(
                "custom event trigger test",
                "customEvent",
                filters,
                null,
                null
                );

    }

}
