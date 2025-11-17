package com.strawberry.statsify.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.strawberry.statsify.config.StatsifyOneConfig;
import com.strawberry.statsify.config.StatsifyOneConfig;
import com.strawberry.statsify.util.Utils;
import com.strawberry.statsify.util.Utils;
import java.io.IOException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormat;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WinstreakApi {

    private final OkHttpClient client = new OkHttpClient();
    private final StatsifyOneConfig config;

    public WinstreakApi(StatsifyOneConfig config) {
        this.config = config;
    }

    public String fetchPlayerStats(String playerName) throws IOException {
        String uuid = Utils.getUUIDFromPlayerName(playerName);
        if (uuid == null) {
            return (
                " §cCould not find " + playerName + " in the current lobby."
            );
        }

        String url =
            "https://api.winstreak.ws/v1/player/bedwars/tabstats?player=" +
            uuid +
            "&key=" +
            config.winstreakKey;

        Request request = new Request.Builder()
            .url(url)
            .header("User-Agent", "Statsify/4.1.0")
            .header("Accept", "application/json")
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.code() == 403) {
                    return " §cInvalid Winstreak.ws API Key";
                }
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonObject rootObject = new JsonParser()
                .parse(responseBody)
                .getAsJsonObject();

            if (rootObject.has("error")) {
                return " §c" + rootObject.get("error").getAsString();
            }

            JsonObject player = rootObject.getAsJsonObject("player");
            String name = player.get("name").getAsString();

            JsonObject stats = rootObject.getAsJsonObject("stats");

            String level = stats.has("level")
                ? stats.get("level").getAsString()
                : "0";
            level = Utils.formatStars(level);

            int finalKills = stats.has("finalKills")
                ? stats.get("finalKills").getAsInt()
                : 0;
            int finalDeaths = stats.has("finalDeaths")
                ? stats.get("finalDeaths").getAsInt()
                : 0;

            double fkdr = (finalDeaths == 0)
                ? finalKills
                : (double) finalKills / finalDeaths;
            String fkdrColor = "§7";
            if (fkdr >= 1 && fkdr < 3) fkdrColor = "§f";
            if (fkdr >= 3 && fkdr < 8) fkdrColor = "§a";
            if (fkdr >= 8 && fkdr < 16) fkdrColor = "§6";
            if (fkdr >= 16 && fkdr < 25) fkdrColor = "§d";
            if (fkdr > 25) fkdrColor = "§4";

            DecimalFormat df = new DecimalFormat("#.##");
            String formattedFkdr = df.format(fkdr);

            return (
                name + " §r" + level + " FKDR: " + fkdrColor + formattedFkdr
            );
        }
    }
}
