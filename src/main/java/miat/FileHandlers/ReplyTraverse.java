package miat.FileHandlers;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReplyTraverse {
    public static JSONObject traverseReplies(MessageCreateEvent mc, DiscordApi api) {
        Message message = mc.getMessage();
        JSONArray msgChain = new JSONArray();   //the array that stores the actual message content of the chat
        JSONObject history = new JSONObject();  //the object that holds the complete, correctly formatted history that ooba needs
        ArrayList<String> messageStorage = new ArrayList<>();   //ArrayList that holds the message content strings
        String content;                         //String that holds message content temporarily

        Message currentMessage = message;       //put message to another variable so it doesnt get lost (idk why i did this)
        while (currentMessage.requestReferencedMessage().isPresent()) {     //while the message has replies to another message
            String newMessageId = currentMessage.requestReferencedMessage().get().join().getIdAsString();       //get the messageID of the replied to message
            currentMessage = api.getMessageById(newMessageId,mc.getChannel()).join();       //get the replied to message by ID, and set it as our current message
            if (!currentMessage.getEmbeds().isEmpty()) {        //If the current message DOES have an embed (bot user)
                content = currentMessage.getEmbeds().get(0).getDescription().get().toString();  //get the content of the description and save it in content
            } else {
                content = currentMessage.getContent();          //if no embed, get regular message content (normal user)
            }
            messageStorage.add(content);                        //add the message content from embed or normal to the messageStorage Arraylist, then repeat while loop.
        }
        makePair(msgChain,messageStorage);      //using msgChain as the storage array and messageStorage as the list of content to put into msgChain
        history.put("internal",msgChain);       //put the freshly placed-in content from the above function into the internal array.
        history.put("visible",msgChain);        //put the freshly placed-in content from the above above function into the visible array. ooba reqires this.
        return history;
    }
    private static void makePair(JSONArray msgChain, ArrayList messageStorage) {    //take a 'donor' json array and an array list with content
        for (int i = 0; i < messageStorage.size(); i += 2) {    //obtain the size of the array list, and iterate through it two at a time, starting at 0
            JSONArray ja = new JSONArray();                     //json array that only holds two values (like ["user_input","bot_input] for ooba's case)
            ja.put(messageStorage.get(i));                      //put the i-th element of the arraylist in the first element of the array
            if (i + 1 < messageStorage.size()) {                //if possible, go to the next element in the arraylist
                ja.put(messageStorage.get(i + 1));              //put the i-plus-1-th element in the second element of the array
            }
            msgChain.put(ja);                                   //put the two element arraylist into the message chain.
        }
    }
}