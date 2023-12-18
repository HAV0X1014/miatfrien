package miat.FileHandlers;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;
import org.json.JSONObject;

public class CollectMessages {
    public static JSONArray collect(MessageCreateEvent mc, DiscordApi api, String prompt, String systemContent) {
        JSONArray messages = new JSONArray();
        Message message = mc.getMessage();

        Message currentMessage = message;
        String newMessageId;

        JSONObject firstMessage = new JSONObject();
        firstMessage.put("role","user");
        firstMessage.put("name",message.getUserAuthor().get().getDisplayName(mc.getServer().get()));
        firstMessage.put("content", prompt);
        messages.put(firstMessage);

        while (currentMessage.getMessageReference().isPresent()) {
            newMessageId = currentMessage.getMessageReference().get().getMessageId().get().toString();
            currentMessage = api.getMessageById(newMessageId, mc.getChannel()).join();
            JSONObject reply = new JSONObject();
            if (!currentMessage.getEmbeds().isEmpty()) {
                reply.put("role","assistant");
                reply.put("content",currentMessage.getEmbeds().get(0).getDescription().get().toString());
            } else {
                reply.put("role","user");
                reply.put("name",message.getUserAuthor().get().getDisplayName(mc.getServer().get()));
                reply.put("content", currentMessage.getContent());
            }
            messages.put(reply);
        }

        //get the content of the message at the very end and remove the command so it doesnt fuck with the AI
        JSONObject initialCommandObject = messages.getJSONObject(messages.length() - 1);
        String content = initialCommandObject.getString("content");
        String prefixRemoved = content.replaceAll("^\\S* ", "");
        initialCommandObject.put("content", prefixRemoved);


        //this goes last because we have to flip the whole array around anyway to make it in chronological order
        JSONObject systemPrompt = new JSONObject();
        systemPrompt.put("role","system");
        systemPrompt.put("content",systemContent);
        //put the system prompt first
        messages.put(systemPrompt);

        JSONArray chronologicalMessageOrder = new JSONArray();
        //flip the whole message order around
        for (int i = messages.length() - 1; i >= 0; i--) {
            JSONObject jsonObject = messages.getJSONObject(i);
            chronologicalMessageOrder.put(jsonObject);
        }
        return chronologicalMessageOrder;
    }
}
