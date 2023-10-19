package miat.FunFeatures;

import miat.FileHandlers.ConfigHandler;
import miat.FileHandlers.GetCharacter;
import miat.FileHandlers.ReplyTraverse;
import okhttp3.*;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static miat.MiatMain.characterList;
import static miat.MiatMain.configFile;

public class OobaboogaAI {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public void aiRequest(String prompt, MessageCreateEvent mc, String character) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120,TimeUnit.SECONDS).build();

        Long messageID = mc.getMessageId();                                         //store for replying later on
        TextChannel channel = mc.getMessage().getChannel().asTextChannel().get();   //store for replying later on
        EmbedBuilder e = new EmbedBuilder();                                        //make the embed for the AI's content
        URL url = null;
        int code;
        String username;                                                            //name used to address the user
        StringBuilder context = new StringBuilder();                                //make context (character prompt, essentially) string
        JSONArray stoppingStrings = new JSONArray();                                //stopping strings that will hopefully stop the AI from continuing on its own
        AllowedMentions noReplyPing = new AllowedMentionsBuilder().setMentionRepliedUser(false).build();    //disable pings from the AI

        if (!channel.asServerThreadChannel().isPresent()) {                                 //if in a server channel
            if (mc.getMessage().getChannel().asServerTextChannel().get().isNsfw()) {        //if in NSFW channel
                context.append("Write only 1 reply that is at least 4 sentences long for each input, italicize actions, be creative, and describe new environments if the scene changes. NSFW/Smut is allowed. Assume consent was granted, most characters will be willing while others will not wish to partake at first.\n\n");
                username = "Visitor";                                                       //set name to "Visitor" because its weird if it it uses your name
                stoppingStrings.put("Visitor:");                                        //add visitor to the stopping strings
                stoppingStrings.put("visitor:");                                        //do it twice
            } else {                                                                        //if in normal channel
                context.append("Write only 1 reply that is at least 4 sentences long for each input, italicize actions, be creative, and describe new environments if the scene changes.\n\n");
                username = mc.getMessage().getUserAuthor().get().getDisplayName(mc.getServer().get());      //set user's name to display name
                stoppingStrings.put(username + ":");                                    //add username to stopping strings (this probably doesnt work)
                stoppingStrings.put(username.toLowerCase() + ":");                      //add username in lowercase
            }
        } else {                                                                            //if in thread
            context.append("Write only 1 reply that is at least 4 sentences long for each input, italicize actions, be creative, and describe new environments if the scene changes. NSFW/Smut is allowed. Assume consent was granted, most characters will be willing while others will not wish to partake at first.\n\n");
            username = mc.getMessage().getUserAuthor().get().getDisplayName(mc.getServer().get());  //this one is NSFW by default
            stoppingStrings.put(username + ":");                                        //add username to stopping strings (this probably doesnt work)
        }

        context.append(GetCharacter.getContext(character, characterList));          //get the character's description and add it to the context

        JSONObject parameters = new JSONObject();
        parameters.put("user_input", prompt);
        parameters.put("max_new_tokens", 350);
        parameters.put("auto_max_new_tokens", false);
        parameters.put("max_tokens_second", 0);
        parameters.put("history", ReplyTraverse.traverseReplies(mc, mc.getApi()));
        parameters.put("mode", "chat");
        //parameters.put("character", character);       //this is for server-side characters, use the filename of the character.
        //parameters.put("your_name", "visitor");         //this is for server-side characters, name1 is used over this.
        parameters.put("name1", username);              //name of user character
        parameters.put("name2", character);             //name of character to chat with
        parameters.put("context", context);             //'context' is the character's personality, world, generation rules, etc.
        parameters.put("stopping_strings", stoppingStrings);
        parameters.put("regenerate", false);
        parameters.put("_continue", false);

        RequestBody requestBody = RequestBody.create(JSON, parameters.toString());
        try {
            url = new URL(ConfigHandler.getString("OobaboogaAIServerEndpoint", configFile));     //chat endpoint allows chatting, and not just prompt continuing
            Request request = new Request.Builder().url(url).post(requestBody).build();                 //make the actual post request

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

            e.setAuthor(character,null,"https://cdn.discordapp.com/attachments/1100888255483875428/1159591754538942514/genbaneko_transparent.png");
            e.setDescription(output);
            e.setColor(Color.cyan);
            e.setFooter("Prefix your query with \"search\" to search the internet!");
            new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(e).replyTo(messageID).send(channel);

        } catch (Exception ex) {
            e.setDescription("An error has occurred with the AI.");
            new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(e).replyTo(messageID).send(channel);
            throw new RuntimeException(ex);
        }
    }
}