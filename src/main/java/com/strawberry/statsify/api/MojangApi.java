package com.strawberry.statsify.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MojangApi {

    public String fetchUUID(String username) {
        try {
            String urlString = "https://api.minecraftservices.com/minecraft/profile/lookup/name/" + username;
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) response.append(line);
                in.close();
                String uuid = extractUUID(response.toString());
                return uuid != null ? uuid : "NICKED";
            }

            if (responseCode == 404) return "NICKED";

            if (responseCode == 429) {
                // Rate limited, fallback to minetools
                urlString = "https://api.minetools.eu/uuid/" + username;
                connection = (HttpURLConnection) new URL(urlString).openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) response.append(line);
                in.close();

                if (response.toString().contains("\"id\": null")) return "NICKED";
                String[] parts = response.toString().split("\"id\":\"");
                if (parts.length > 1) {
                    return parts[1].split("\"")[0];
                } else {
                    return "NICKED";
                }
            }

        } catch (Exception ignored) {}

        return "NICKED";
    }

    private String extractUUID(String response) {
        String[] parts = response.split("\"");
        if (response.contains("Couldn't")) {
            return "NICKED";
        }

        if (parts.length >= 5) {
            return parts[3];
        }

        return null;
    }

    public String getUUIDFromName(String playerName) {
        for (NetworkPlayerInfo info : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            if (info.getGameProfile().getName().equalsIgnoreCase(playerName)) {
                return String.valueOf(info.getGameProfile().getId());
            }
        }
        return null; // Player not found (probably not in tab list)
    }
}
