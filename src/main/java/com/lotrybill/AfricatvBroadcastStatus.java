package com.lotrybill;
import org.json.simple.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AfricatvBroadcastStatus {

    private static final String API_URL = "https://api.afreecatv.com/broad/a/watch";

    public static String getBroadcastStatus(String broadcasterId) throws Exception {
        String apiUrl = "http://bjapi.afreecatv.com/api/" + broadcasterId + "/player_info";
        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();

        String response = content.toString();
        int startIndex = response.indexOf("broadcasting\":") + "broadcasting\":".length();
        int endIndex = response.indexOf(",", startIndex);
        String status = response.substring(startIndex, endIndex);

        return status.equals("true") ? "방송 중" : "방송 종료";
    }
}