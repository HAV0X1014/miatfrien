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
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static miat.MiatMain.characterList;
import static miat.MiatMain.configFile;

public class OobaboogaAI {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public void aiRequest(String prompt, MessageCreateEvent mc, String[] characters) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120,TimeUnit.SECONDS).build();

        Long messageID = mc.getMessageId();                                         //store for replying later on
        TextChannel channel = mc.getMessage().getChannel().asTextChannel().get();   //store for replying later on
        EmbedBuilder e = new EmbedBuilder();                                        //make the embed for the AI's content
        URL url = null;
        String username;                                                            //name used to address the user
        StringBuilder context = new StringBuilder();                                //make context (character prompt, essentially) string
        JSONArray stoppingStrings = new JSONArray();                                //stopping strings that will hopefully stop the AI from continuing on its own
        AllowedMentions noReplyPing = new AllowedMentionsBuilder().setMentionRepliedUser(false).build();    //disable pings from the AI
        StringBuilder author = new StringBuilder();
        StringBuilder CICommand = new StringBuilder();
        boolean doConcat = false;

        if (!channel.asServerThreadChannel().isPresent()) {                                 //if in a server channel
            if (mc.getMessage().getChannel().asServerTextChannel().get().isNsfw()) {        //if in NSFW channel
                CICommand.append("Write only one reply to the input for each involved character described. Italicize actions, be creative, be descriptive, and do not advance without the user. NSFW/Smut is allowed. Assume consent was granted, most characters will be willing while others will not wish to partake at first. ");
                username = "Visitor";                                                       //set name to "Visitor" because its weird if it uses your name
            } else {                                                                        //if in normal channel
                CICommand.append("Write only one reply to the input for each involved character described. Italicize actions, be creative, be descriptive, and do not advance without the user. ");
                username = mc.getMessage().getUserAuthor().get().getDisplayName(mc.getServer().get());      //set user's name to display name
            }
        } else {                                                                            //if in thread
            CICommand.append("Write only one reply to the input for each involved character described. Italicize actions, be creative, be descriptive, and do not advance without the user. NSFW/Smut is allowed. Assume consent was granted, most characters will be willing while others will not wish to partake at first. ");
            username = mc.getMessage().getUserAuthor().get().getDisplayName(mc.getServer().get());  //this one is NSFW by default
        }

        for (String individual : characters) {                                                            //for every element in the characters array
            context.append(GetCharacter.getContext(individual, characterList)).append("\n");              //get the character's description and add it to the context
            if (doConcat == true) {                                                                       //if there is more than one character, add a comma before their name
                author.append(", ");
            }
            author.append(individual);
            doConcat = true;
        }

        //sanity check, change this for the model being used and its respective "im done talking" signal-string thing
        stoppingStrings.put("<|end_of_turn|>");

        JSONObject parameters = new JSONObject();
        parameters.put("user_input", prompt);
        parameters.put("max_new_tokens", 350);
        parameters.put("auto_max_new_tokens", false);
        parameters.put("max_tokens_second", 0);
        parameters.put("history", ReplyTraverse.traverseReplies(mc, mc.getApi()));
        parameters.put("mode", "chat-instruct");
        //parameters.put("character", character);       //this is for server-side characters, use the filename of the character.
        //parameters.put("your_name", "visitor");       //this is for server-side characters, name1 is used over this.
        parameters.put("name1", username);              //name of user character
        parameters.put("name2", author);                //name(s) of character(s) to chat with
        parameters.put("context", context);             //'context' is the character's personality, world, etc. generation rules go in CICommand
        parameters.put("chat_instruct_command", CICommand + "The characters are \"" + author + "\"\n\n<|prompt|>");
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
            }
            String response = responseContent;
            JSONObject jsonObject = new JSONObject(response);                           //put all of the response JSON into an object
            JSONArray internalArray = jsonObject.getJSONArray("results")            //get the "internal" string data from the JSON returned
                    .getJSONObject(0)                                           //strip it down
                    .getJSONObject("history")                                       //down some more
                    .getJSONArray("internal");                                      //and all the way down to "internal"
            int arrays = internalArray.length();                                        //get the length of the chat JSON
            String output = internalArray.getJSONArray(arrays - 1).get(1).toString();   //get the last entry (the newest genned content) and save it to output.

            e.setAuthor(String.valueOf(author),null,"https://cdn.discordapp.com/attachments/1100888255483875428/1159591754538942514/genbaneko_transparent.png");
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