package miat.FunFeatures;

import miat.FileHandlers.ReplyTraverse;
import okhttp3.*;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class OobaboogaAI {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static void aiRequest(String prompt, MessageCreateEvent mc, String character, String context) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120,TimeUnit.SECONDS).build();

        String messageID = mc.getMessage().getIdAsString();                         //store for replying later on
        ServerTextChannel channel = mc.getMessage().getServerTextChannel().get();   //store for replying later on
        URL url = null;
        int code;

        JSONObject parameters = new JSONObject();
        parameters.put("user_input", prompt);
        parameters.put("max_new_tokens", 350);
        parameters.put("auto_max_new_tokens", false);
        parameters.put("max_tokens_second", 0);
        parameters.put("history", ReplyTraverse.traverseReplies(mc, mc.getApi()));
        parameters.put("mode", "chat");
        //parameters.put("character", character);       //this is for server-side characters, use the filename of the character.
        //parameters.put("your_name", "anzel");         //this is for server-side characters, name1 is used over this.
        parameters.put("name1","Visitor");              //name of user character
        parameters.put("name2", character);             //name of character to chat with
        parameters.put("context", context);             //'context' is the character's personality, world, etc.
        parameters.put("regenerate", false);
        parameters.put("_continue", false);

        RequestBody requestBody = RequestBody.create(JSON, parameters.toString());
        try {
            url = new URL("http://localhost:5000/api/v1/chat");             //chat endpoint allows chatting, and not just prompt continuing
            Request request = new Request.Builder().url(url).post(requestBody).build();     //make the actual post request

            String responseContent;                                                 //declare the content-holding string
            try (Response resp = client.newCall(request).execute()) {               //send request to the server
                responseContent = resp.body().string();                             //get the returned content and put it in the respective string
                code = resp.code();                                                 //get response code (vestigial youchat code)
            }
            String response = responseContent;
            JSONObject jsonObject = new JSONObject(response);                           //put all of the response JSON into an object
            JSONArray internalArray = jsonObject.getJSONArray("results")            //get the "internal" string data from the JSON returned
                    .getJSONObject(0)                                           //strip it down
                    .getJSONObject("history")                                       //down some more
                    .getJSONArray("internal");                                      //and all the way down to "internal"
            int arrays = internalArray.length();                                        //get the length of the chat JSON
            String output = internalArray.getJSONArray(arrays - 1).get(1).toString();   //get the last entry (the newest genned content) and save it to output.

            if (code < 199 || code > 300) {                                         //vestigial code from youchat API when it would go down every 3 seconds
                response = "There has been an error between the API and the bot.\n\nHTTP status code ``" + code + "``.";
            }

            EmbedBuilder e = new EmbedBuilder();                                    //make the embed for the AI's content
            e.setAuthor(character,null,"https://cdn.discordapp.com/attachments/1100888255483875428/1159591754538942514/genbaneko_transparent.png");
            e.setDescription(output);
            e.setColor(Color.cyan);
            e.setFooter("Prefix your query with \"search\" to search the internet!");

            channel.getMessageById(messageID).thenAccept(message -> {               //this sends the reply to the user by messageid
                message.reply(e);
            });

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}