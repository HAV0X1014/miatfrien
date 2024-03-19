package miat.FileHandlers;

import java.io.*;

public class DeletedMessageLogChannel {
    public static String channelRetriever(String serverID) {
        File channelIDfile = new File("ServerFiles/" + serverID + ".txt");
        String logChannel = null;

        if (!channelIDfile.exists()) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("ServerFiles/" + serverID + ".txt"));
                writer.write("");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else

        logChannel = ReadFile.getFirstLine("ServerFiles/" + serverID + ".txt");
        return logChannel;
    }
}
