package com.strawberry.statsify.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;

public class UrchinApi {

    private final Map<String, Pair<Integer, Long>> pingCache =
        new ConcurrentHashMap<>();

    // Prevent duplicate fetches for the same player
    private final Set<String> fetchInProgress = ConcurrentHashMap.newKeySet();

    private static final long CACHE_DURATION_MS = 7_200_000; // 2 hours

    public int getCachedPing(String uuid) {
        Pair<Integer, Long> cached = pingCache.get(uuid);

        if (
            cached != null &&
            System.currentTimeMillis() - cached.getRight() < CACHE_DURATION_MS
        ) {
            return cached.getLeft();
        }

        return -1;
    }

    public void updateCache(String uuid, int ping) {
        pingCache.put(uuid, Pair.of(ping, System.currentTimeMillis()));
    }

    /**
     * Returns true if fetch started, false if it was blocked
     * (already in progress).
     */
    public boolean tryStartFetch(String uuid) {
        return fetchInProgress.add(uuid);
    }

    public void finishFetch(String uuid) {
        fetchInProgress.remove(uuid);
    }

    public int fetchPingBlocking(String uuid) {
        try {
            URL url = new URL("https://coral.urchin.ws/api/ping?uuid=" + uuid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty(
                "Referer",
                "https://coral.urchin.ws/player/" + uuid
            );
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) response.append(line);

                in.close();

                JsonObject json = new JsonParser()
                    .parse(response.toString())
                    .getAsJsonObject();

                if (json.has("success") && json.get("success").getAsBoolean()) {
                    JsonArray data = json.getAsJsonArray("data");
                    if (data.size() > 0) {
                        JsonObject latest = data.get(0).getAsJsonObject();
                        int ping = latest.get("avg").getAsInt();

                        updateCache(uuid, ping);
                        return ping;
                    }
                }
            }
        } catch (Exception ignored) {}

        return -1;
    }

    public String fetchUrchinTags(String playerName, String urchinKey)
        throws IOException {
        String tagsURL =
            "https://urchin.ws/player/" +
            playerName +
            "?key=" +
            urchinKey +
            "&sources=MANUAL";
        URL tagsAPIURL = new URL(tagsURL);
        HttpURLConnection statsConnection =
            (HttpURLConnection) tagsAPIURL.openConnection();
        statsConnection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0"
        );

        int responseCode = statsConnection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("URCHIN: " + responseCode);
        }

        InputStreamReader inputStream = new InputStreamReader(
            statsConnection.getInputStream()
        );
        BufferedReader reader = new BufferedReader(inputStream);
        StringBuilder responseText = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            responseText.append(line);
        }
        reader.close();

        String response = responseText.toString();
        if (!response.isEmpty()) {
            try {
                String regex =
                    "\"type\":\"(.*?)\".*?\"reason\":\"(.*?)\".*?\"added_on\":\"(.*?)\"";

                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response);

                if (matcher.find()) {
                    String type = matcher.group(1);
                    String reason = matcher.group(2);
                    return "§r" + type + ". §rReason: §6" + reason;
                }
            } catch (NumberFormatException ignored) {}
        }
        return "";
    }
}
