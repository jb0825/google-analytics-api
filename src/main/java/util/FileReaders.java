package util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FileReaders {
    public static String PATH = "file/gtm/";

    public List<String> readFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(PATH + fileName + ".txt"));
        List<String> lines = new ArrayList<>();
        String line;

        while((line = reader.readLine()) != null)
            lines.add(line);

        reader.close();
        return lines;
    }
}
