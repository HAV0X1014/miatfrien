package miat.FunFeatures;

import miat.FileHandlers.ReadFirstLine;
import miat.FileHandlers.ReadFull;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class KemoYou {
    private static HttpURLConnection conn;

    public static EmbedBuilder askServal(String prompt) {
        String line;
        StringBuffer responseContent = new StringBuffer();
        BufferedReader reader;
        String response;
        EmbedBuilder e = new EmbedBuilder();

        try {
            URL url = new URL("https://api.betterapi.net/youdotcom/chat?message="+ ReadFull.read("ServerFiles/KemoYouPrompt.txt") + prompt + "&key=" + ReadFirstLine.read("ServerFiles/apiKey.txt"));
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(25000);
            conn.setReadTimeout(25000);

            int status = conn.getResponseCode();

            if (status > 299) {
                e.setTitle("YouChat AI response");
                e.setAuthor("YouChat","https://you.com/chat","https://cdn.discordapp.com/attachments/1100888255483875428/1100888270923120782/you_logo.png");
                e.addField("Response Content","Failed to get a response from the API (malformed or unavailable - HTTP 299)");
                e.setColor(Color.yellow);
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();

                response = responseContent.toString();
                response = response.replaceAll(".\"message\":\"","");
                response = response.replaceAll("\",\"time\":.*","");
                response = response.replaceAll("\\\\n\\\\n","\n\n");
                response = response.replaceAll("\\\\n","\n");

                e.setTitle("Serval AI");
                e.setAuthor("Serval","https://you.com/chat","https://cdn.discordapp.com/attachments/1100888255483875428/1100971753553006592/image.png");
                e.addField("Response Content",response);
                e.setColor(Color.yellow);
            }
            return e;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            conn.disconnect();
        }
        e.setTitle("YouChat AI response");
        e.setAuthor("YouChat","https://you.com/chat","https://cdn.discordapp.com/attachments/1100888255483875428/1100888270923120782/you_logo.png");
        e.addField("Response Content","Failed to get a response from the API (timed out)");
        e.setColor(Color.yellow);

        return e;
    }
}