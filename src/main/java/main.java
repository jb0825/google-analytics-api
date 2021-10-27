import api.GTM;
import util.FileReaders;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class main {

    public static void main(String ...args) throws GeneralSecurityException, IOException {
        FileReaders fileReader = new FileReaders();
        fileReader.getGTMCafe24Variables();

    }

}
