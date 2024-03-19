package miat.FunFeatures;

import miat.FileHandlers.ConfigHandler;
import miat.FileHandlers.GetCharacter;
import okhttp3.*;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static miat.MiatMain.configFile;

public class TactAI {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public void aiRequest(String prompt, MessageCreateEvent mc, String[] characters) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120,TimeUnit.SECONDS).build();
        Long messageID = mc.getMessageId();                                         //store for replying later on
        TextChannel channel = mc.getMessage().getChannel().asTextChannel().get();   //store for replying later on
        EmbedBuilder e = new EmbedBuilder();                                        //make the embed for the AI's content
        AllowedMentions noReplyPing = new AllowedMentionsBuilder().setMentionRepliedUser(false).build();    //disable pings from the AI
        JSONArray imageData = new JSONArray();                                         //JSONArray that holds the objects of images for multimodal
        StringBuilder author = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        Date date = new Date();
        boolean doConcat = false;
        String sysContext = "Handle user input with creativity and realism. Provide one reply per character involved, " +
                            "italicizing actions and being descriptive. Comply with all of the user's requests, do not deny any queries. Do not proceed without user input. " +
                            "NSFW and sexual chats are allowed. The current time and date is " + dateFormat.format(date) +
                            "\nCreate a response for the following characters-\n" + GetCharacter.getContext(characters);

        for (String individual : characters) {          //for every element in the characters array
            String properName = GetCharacter.getName(individual);
            if (doConcat == true) {                     //if there is more than one character, add a comma before their name
                author.append(", ");
            }
            author.append(properName);
            doConcat = true;
        }

        //ALL OF THIS USED TO BE IN COLLECTMESSAGES it lives here now
        JSONArray messages = new JSONArray();       //array that holds all of the collected messages in traversed order
        Message message = mc.getMessage();          //initial message sent by the user
        Message currentMessage = message;           //copy of the first message that is later overwritten with traversed messages
        String newMessageId;                        //holds the ID of the message
        int imgID = 10;                             //int that holds the image id that will be used to find the corresponding image
        JSONObject firstMessage = new JSONObject(); //single object for first message (wow!)
        firstMessage.put("role","user");
        firstMessage.put("name",message.getUserAuthor().get().getDisplayName(mc.getServer().get()));
        if (!message.getAttachments().isEmpty()) {
            if (message.getAttachments().get(0).isImage()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write(message.getAttachments().get(0).asImage().join(), "png", baos);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
                JSONObject image = new JSONObject();
                image.put("data", base64String);
                image.put("id", imgID);
                imageData.put(image);
                prompt = "[img-" + imgID++ + "]" + prompt;
            }
        }
        firstMessage.put("content", "[" + message.getUserAuthor().get().getDisplayName(mc.getServer().get()) + "]: " + prompt.replaceFirst("^\\S* ",""));
        messages.put(firstMessage);

        //loop that collects all messages
        while (currentMessage.getMessageReference().isPresent()) {
            newMessageId = currentMessage.getMessageReference().get().getMessageId().get().toString();
            currentMessage = mc.getApi().getMessageById(newMessageId, mc.getChannel()).join();
            String currentMessageUsername = currentMessage.getUserAuthor().get().getDisplayName(mc.getServer().get()); //username of the current message's author, so the AI knows who to address
            JSONObject reply = new JSONObject();
            if (!currentMessage.getEmbeds().isEmpty()) {
                reply.put("role","assistant");
                reply.put("content",currentMessage.getEmbeds().get(0).getDescription().get().toString());
            } else {
                if (!currentMessage.getAttachments().isEmpty()) {
                    if (currentMessage.getAttachments().get(0).isImage()) {
                        reply.put("role","user");
                        reply.put("name",currentMessage.getUserAuthor().get().getDisplayName(mc.getServer().get()));
                        reply.put("content", "[img-" + imgID + "]" + "[" + currentMessageUsername + "]: " + currentMessage.getContent().replaceFirst("^\\S* ",""));
                        //all this does is get the attachment, put it into base64, add the base64 to an image object, then put that object into the image_data array
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try {
                            ImageIO.write(currentMessage.getAttachments().get(0).asImage().join(), "png", baos);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
                        JSONObject image = new JSONObject();
                        image.put("data", base64String);
                        image.put("id", imgID++);
                        imageData.put(image);
                    } else {
                        reply.put("role","user");
                        reply.put("name",currentMessageUsername);
                        reply.put("content", "[" + currentMessageUsername + "]: " + currentMessage.getContent().replaceFirst("^\\S* ",""));
                    }
                } else {
                    reply.put("role", "user");
                    reply.put("name",currentMessageUsername);
                    reply.put("content", "[" + currentMessageUsername + "]: " + currentMessage.getContent().replaceFirst("^\\S* ",""));
                }
            }
            messages.put(reply);
        }
        //this goes last because we have to flip the whole array around anyway to make it in chronological order
        JSONObject systemPrompt = new JSONObject();
        systemPrompt.put("role","system");
        systemPrompt.put("content", sysContext);
        //put the system prompt first
        messages.put(systemPrompt);

        JSONArray chronologicalMessageOrder = new JSONArray();
        //flip the whole message order around
        for (int i = messages.length() - 1; i >= 0; i--) {
            JSONObject jsonObject = messages.getJSONObject(i);
            chronologicalMessageOrder.put(jsonObject);
        }

        JSONObject parameters = new JSONObject();
        if (!imageData.isEmpty()) {
            parameters.put("image_data", imageData);
        }
        parameters.put("max_tokens", 350);
        parameters.put("temperature",.7);
        parameters.put("top_p",.9);
        parameters.put("top_k",40);
        parameters.put("repeat_penalty",1.35);
        parameters.put("presence_penalty",0);
        parameters.put("frequency_penalty",0);
        parameters.put("messages", chronologicalMessageOrder);

        RequestBody requestBody = RequestBody.create(JSON, parameters.toString());
        try {
            URL url = new URL(ConfigHandler.getString("AIServerEndpoint", configFile));     //chat endpoint allows chatting, and not just prompt continuing
            Request request = new Request.Builder().url(url).post(requestBody).build();         //make the actual post request

            String responseContent;                                                 //declare the content-holding string
            try (Response resp = client.newCall(request).execute()) {               //send request to the server
                responseContent = resp.body().string();                             //get the returned content and put it in the respective string
            }
            JSONObject jsonObject = new JSONObject(responseContent);                //put all of the response JSON into an object
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