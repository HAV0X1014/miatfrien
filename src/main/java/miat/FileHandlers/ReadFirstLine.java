package miat.FileHandlers;

import java.io.*;

public class ReadFirstLine {
    public static String read(String filepath) {
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            line = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
}
