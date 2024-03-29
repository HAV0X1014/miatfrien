package miat.FunFeatures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Inspiro {
    private static HttpURLConnection conn;
    public static String inspiro() {
        String line;
        StringBuffer responseContent = new StringBuffer();
        BufferedReader reader;

        try {
            URL url = new URL("https://inspirobot.me/api?generate=true");
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            int status = conn.getResponseCode();

            if (status > 299) {
                return "Failed to get an image";
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
                return responseContent.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            if (responseContent.toString() != null) {
            }
        }
        return "Failed";
    }
}
