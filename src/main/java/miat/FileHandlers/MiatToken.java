package miat.FileHandlers;

import java.io.*;

public class MiatToken {
    public static String read(String s) {
        try {
            FileReader file = new FileReader(s);
            BufferedReader buffer = new BufferedReader(file);

            return buffer.readLine();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
