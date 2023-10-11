package miat.FunFeatures;

import org.javacord.api.event.message.MessageCreateEvent;
import java.io.*;

public class ReWords {
    public static void scoreModifier(MessageCreateEvent mc, int updown) {
        String userID = mc.getMessageAuthor().getIdAsString();
        File file = new File("ReWordsScores/"+ userID + ".txt");
        int score = 0;

        try {
            if (!file.exists()) {
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter("ReWordsScores/" + userID + ".txt"));
                writer.write("0");
                writer.close();
            } else {
                BufferedReader reader = new BufferedReader(new FileReader("ReWordsScores/" + userID + ".txt"));
                String line = reader.readLine();
                if (line != null) {
                    score = Integer.parseInt(line);
                }
                reader.close();
            }
            score = score + updown;
            BufferedWriter writer = new BufferedWriter(new FileWriter("ReWordsScores/" + userID + ".txt", false));
            writer.write(String.valueOf(score));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
