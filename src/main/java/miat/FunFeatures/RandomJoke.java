package miat.FunFeatures;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class RandomJoke {
    public static EmbedBuilder randomJoke() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        URL url = null;
        String responseContent;
        Color jokeAPI = new Color(16,23,71);
        try {
            url = new URL("https://v2.jokeapi.dev/joke/Any?format=txt");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Request request = new Request.Builder().url(url).build();

        try (Response resp = client.newCall(request).execute()) {
            responseContent = resp.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EmbedBuilder e = new EmbedBuilder();
        e.setAuthor("JokeAPI.dev","https://jokeapi.dev/","");
        e.setDescription(responseContent);
        e.setColor(jokeAPI);

        //from HAV0X - wow i suck at programming
        //from HAV0X less than an hour later - it could be worse!
        return e;
    }
}
