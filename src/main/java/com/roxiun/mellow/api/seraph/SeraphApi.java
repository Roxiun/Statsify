package com.roxiun.mellow.api.seraph;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.roxiun.mellow.api.mojang.MojangApi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SeraphApi {

    private final MojangApi mojangApi;

    public SeraphApi(MojangApi mojangApi) {
        this.mojangApi = mojangApi;
    }

    public List<SeraphTag> fetchSeraphTags(String uuid, String seraphApiKey)
        throws IOException {
        try {
            // If the UUID is invalid for any reason, throw an exception
            if (uuid == null || uuid.equals("ERROR") || uuid.isEmpty()) {
                throw new IOException("Invalid UUID provided.");
            }

            String apiUrl = "https://api.seraph.si/cubelify/blacklist/" + uuid;
            if (seraphApiKey != null && !seraphApiKey.isEmpty()) {
                apiUrl += "?key=" + seraphApiKey;
            }

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
                );
                String response = in.lines().collect(Collectors.joining());
                in.close();
                return parseTags(response);
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return new ArrayList<>(); // Player has no tags
            } else {
                throw new IOException(
                    "Seraph API request failed with code: " + responseCode
                );
            }
        } catch (IOException e) {
            // If request fails, return empty list
            return new ArrayList<>();
        }
    }

    private List<SeraphTag> parseTags(String response) {
        try {
            JsonObject json = new JsonParser()
                .parse(response)
                .getAsJsonObject();
            if (json.has("tags")) {
                JsonArray tagsArray = json.getAsJsonArray("tags");
                if (tagsArray.size() > 0) {
                    List<SeraphTag> tags = new ArrayList<>();
                    for (JsonElement tagElement : tagsArray) {
                        JsonObject tagObj = tagElement.getAsJsonObject();

                        String icon = tagObj.has("icon")
                            ? tagObj.get("icon").getAsString()
                            : "";
                        String tooltip = tagObj.has("tooltip")
                            ? tagObj.get("tooltip").getAsString()
                            : "";
                        int color = tagObj.has("color")
                            ? tagObj.get("color").getAsInt()
                            : 0;
                        String tagName = tagObj.has("tag_name")
                            ? tagObj.get("tag_name").getAsString()
                            : "";
                        String text = tagObj.has("text")
                            ? tagObj.get("text").getAsString()
                            : null;
                        int textColor = tagObj.has("textColor")
                            ? tagObj.get("textColor").getAsInt()
                            : 0;

                        // Skip seraph.verified and seraph.advertisement tags
                        if (
                            "seraph.verified".equals(tagName) ||
                            "seraph.advertisement".equals(tagName)
                        ) {
                            continue;
                        }

                        tags.add(
                            new SeraphTag(
                                icon,
                                tooltip,
                                color,
                                tagName,
                                text,
                                textColor
                            )
                        );
                    }
                    return tags;
                }
            }
        } catch (Exception e) {
            // If parsing fails, return empty list
        }
        return new ArrayList<>();
    }
}
