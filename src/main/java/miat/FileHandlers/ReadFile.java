package miat.FileHandlers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ReadFile {
    public static String getFirstLine(String filepath) {
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

    public static String getFull(String filePath) {
        String fullText = "";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                fullText += line;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fullText;
    }

    public static String getRandomLine(String filePath) {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();

            Random random = new Random();
            String randomLine = lines.get(random.nextInt(lines.size()));

            return randomLine;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
