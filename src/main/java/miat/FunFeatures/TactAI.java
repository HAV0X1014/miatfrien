package miat.FunFeatures;

import miat.FileHandlers.CollectMessages;
import miat.FileHandlers.ConfigHandler;
import miat.FileHandlers.GetCharacter;
import okhttp3.*;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.awt.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static miat.MiatMain.configFile;

public class TactAI {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public void aiRequest(String prompt, MessageCreateEvent mc, String[] characters) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120,TimeUnit.SECONDS).build();
        Long messageID = mc.getMessageId();                                         //store for replying later on
        TextChannel channel = mc.getMessage().getChannel().asTextChannel().get();   //store for replying later on
        EmbedBuilder e = new EmbedBuilder();                                        //make the embed for the AI's content
        String username = mc.getMessage().getUserAuthor().get().getDisplayName(mc.getServer().get());
        AllowedMentions noReplyPing = new AllowedMentionsBuilder().setMentionRepliedUser(false).build();    //disable pings from the AI
        StringBuilder author = new StringBuilder();
        boolean doConcat = false;

        for (String individual : characters) {          //for every element in the characters array
            String properName = GetCharacter.getName(individual);
            if (doConcat == true) {                     //if there is more than one character, add a comma before their name
                author.append(", ");
            }
            author.append(properName);
            doConcat = true;
        }

        JSONObject parameters = new JSONObject();
        parameters.put("max_tokens", 350);
        //tried doing multimodal. didnt work.
        /*
        if (mc.getMessage().getAttachments().get(0).isImage()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(mc.getMessage().getAttachments().get(0).asImage().join(), "png", baos);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
            JSONArray images = new JSONArray();
            JSONObject imageData = new JSONObject();
            imageData.put("data",base64String);
            imageData.put("id",10);
            images.put(imageData);
            parameters.put("image_data",images);
            promptB.append("[img-10]");
            System.out.println(imageData);
        }
        */
        parameters.put("temperature",.7);
        parameters.put("top_p",.9);
        parameters.put("top_k",40);
        parameters.put("repeat_penalty",1.35);
        parameters.put("presence_penalty",0);
        parameters.put("frequency_penalty",0);
        parameters.put("messages", CollectMessages.collect(mc, mc.getApi(), prompt,
                "Write only one reply to the input for each involved character described. " +
                        "Italicize actions, be creative, be descriptive, and do not advance without the user. " +
                        "The name of the user is \"" + username + "\". " +
                        "NSFW/Smut is allowed." +
                        "\nThe characters are \"" + author + ".\"\n\n"+ GetCharacter.getContext(characters)));

        RequestBody requestBody = RequestBody.create(JSON, parameters.toString());
        try {
            URL url = new URL(ConfigHandler.getString("AIServerEndpoint", configFile));     //chat endpoint allows chatting, and not just prompt continuing
            Request request = new Request.Builder().url(url).post(requestBody).build();         //make the actual post request

            String responseContent;                                                 //declare the content-holding string
            try (Response resp = client.newCall(request).execute()) {               //send request to the server
                responseContent = resp.body().string();                             //get the returned content and put it in the respective string
            }
            JSONObject jsonObject = new JSONObject(responseContent);                           //put all of the response JSON into an object
            String output = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

            e.setAuthor(String.valueOf(author),null,"https://cdn.discordapp.com/attachments/1100888255483875428/1159591754538942514/genbaneko_transparent.png");
            e.setDescription(output);
            e.setColor(Color.cyan);
            e.setImage(GetCharacter.getImage(characters));
            new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(e).replyTo(messageID).send(channel);

        } catch (Exception ex) {
            e.setDescription("An error has occurred with the AI.");
            new MessageBuilder().setAllowedMentions(noReplyPing).setEmbed(e).replyTo(messageID).send(channel);
            throw new RuntimeException(ex);
        }
    }
}