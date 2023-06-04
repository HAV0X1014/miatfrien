package miat.FunFeatures;

import miat.FileHandlers.ReadFirstLine;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class YouChat {
    public static void youChat(String prompt, MessageCreateEvent mc) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30,TimeUnit.SECONDS).build();

        String messageID = mc.getMessage().getIdAsString();
        ServerTextChannel channel = mc.getMessage().getServerTextChannel().get();
        EmbedBuilder e = new EmbedBuilder();
        URL url = null;
        int code;
        try {
            url = new URL("https://api.betterapi.net/youchat?inputs=" + prompt + "&key=" + ReadFirstLine.read("ServerFiles/apiKey.txt"));
            Request request = new Request.Builder().url(url).build();

            String responseContent;
            try (Response resp = client.newCall(request).execute()) {
                responseContent = resp.body().string();
                code = resp.code();
            }

            String response = responseContent;
            response = response.replaceAll(".*\"generated_text\":\"", "");
            response = response.replaceAll("\",\"time\":.*", "");
            response = response.replaceAll("\\\\n\\\\n", "\n\n");
            response = response.replaceAll("\\\\n", "\n");
            response = response.replaceAll("&lt;","<");
            response = response.replaceAll("&gt;",">");
            response = response.replaceAll("&amp;","&");
            response = response.replaceAll("&quot;","\"");
            response = response.replaceAll("&apos;","\'");
            response = response.replaceAll("&cent;","¢");
            response = response.replaceAll("&pound;","£");
            response = response.replaceAll("&yen;","¥");
            response = response.replaceAll("&euro;","€");
            response = response.replaceAll("&nbsp;"," ");

            if (response == null) {
                System.out.println("Something is wrong - YouChat");
            }

            if (code < 199 || code > 300) {
                response = "There has been an error between the API and the bot.\n\nHTTP status code ``" + code + "``.";
            }

            e.setAuthor("YouChat","https://you.com/chat","https://cdn.discordapp.com/attachments/1100888255483875428/1100888270923120782/you_logo.png");
            e.setDescription(response);
            e.setColor(Color.cyan);

            channel.getMessageById(messageID).thenAccept(message -> {
                message.reply(e);
            });

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}