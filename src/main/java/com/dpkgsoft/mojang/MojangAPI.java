package com.dpkgsoft.mojang;

import com.dpkgsoft.mojang.request.TexturesRequest;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MojangAPI {
    private static final String API_ENDPOINT = "https://api.mojang.com/";
    private static final String STATUS_ENDPOINT = "https://status.mojang.com/";
    private static final String SESSION_ENDPOINT = "https://sessionserver.mojang.com/";

    public enum StatusService {
        MINECRAFT_NET,
        SESSION,
        ACCOUNT,
        AUTH_SERVER,
        SESSION_SERVER,
        API,
        TEXTURES,
        MOJANG_COM
    }

    public enum StatusCode {
        GREEN,
        YELLOW,
        RED
    }

    public static StatusCode getStatus(StatusService service) throws APIException {
        return getStatuses().get(service);
    }

    public static Map<StatusService, StatusCode> getStatuses() throws APIException {
        try {
            Map<StatusService, StatusCode> response = new HashMap<>();

            String data = Utils.get(STATUS_ENDPOINT + "check");
            JSONArray json = new JSONArray(data);
            for (int i = 0; i < json.length(); i++) {
                JSONObject service = json.getJSONObject(i);
                if (service.has("minecraft.net")) response.put(StatusService.MINECRAFT_NET, stringToStatusCode(service.getString("minecraft.net")));
                if (service.has("session.minecraft.net")) response.put(StatusService.SESSION, stringToStatusCode(service.getString("session.minecraft.net")));
                if (service.has("account.mojang.com")) response.put(StatusService.ACCOUNT, stringToStatusCode(service.getString("account.mojang.com")));
                if (service.has("authserver.mojang.com")) response.put(StatusService.AUTH_SERVER, stringToStatusCode(service.getString("authserver.mojang.com")));
                if (service.has("sessionserver.mojang.com")) response.put(StatusService.SESSION_SERVER, stringToStatusCode(service.getString("sessionserver.mojang.com")));
                if (service.has("api.mojang.com")) response.put(StatusService.API, stringToStatusCode(service.getString("api.mojang.com")));
                if (service.has("textures.minecraft.net")) response.put(StatusService.TEXTURES, stringToStatusCode(service.getString("textures.minecraft.net")));
                if (service.has("mojang.com")) response.put(StatusService.MOJANG_COM, stringToStatusCode(service.getString("mojang.com")));
            }
            return response;
        } catch (IOException e) {
            throw new APIException(e.getMessage(), 1);
        }
    }

    private static StatusCode stringToStatusCode(String code) {
        if (code.equalsIgnoreCase("green")) return StatusCode.GREEN;
        else if (code.equalsIgnoreCase("yellow")) return StatusCode.YELLOW;
        else return StatusCode.RED;
    }

    public static Profile getProfile(String username) throws APIException {
        try {
            String data = Utils.get(API_ENDPOINT + "users/profiles/minecraft/" + username);
            if (data.equalsIgnoreCase("")) throw new APIException("User not found", 404);
            JSONObject json = new JSONObject(data);
            return new Profile(json.getString("id"), json.getString("name"));
        } catch (IOException e) {
            throw new APIException(e.getMessage(), 1);
        }
    }

    public static String[] nameHistory(String uuid) throws APIException {
        try {
            String data = Utils.get(API_ENDPOINT + "user/profiles/" + uuid + "/names");
            if (data.equalsIgnoreCase("")) throw new APIException("User not found", 404);
            JSONArray json = new JSONArray(data);
            String[] profiles = new String[json.length()];
            for (int i = 0; i < json.length(); i++) {
                profiles[i] = json.getJSONObject(i).getString("name");
            }
            return profiles;
        } catch (IOException e) {
            throw new APIException(e.getMessage(), 1);
        }
    }

    public static Profile[] getProfiles(String[] playerNames) throws APIException {
        try {
            JSONArray array = new JSONArray();
            for (String name : playerNames) {
                array.put(name);
            }
            String data = Utils.postJson(API_ENDPOINT + "profiles/minecraft", array.toString());
            //if (data.equalsIgnoreCase("")) throw new APIException("User not found", 404);
            JSONArray json = new JSONArray(data);
            Profile[] profiles = new Profile[json.length()];
            for (int i = 0; i < json.length(); i++) {
                profiles[i] = new Profile(json.getJSONObject(i).getString("id"),
                        json.getJSONObject(i).getString("name"),
                        json.getJSONObject(i).has("demo"),
                        json.getJSONObject(i).has("legacy"));
            }
            return profiles;
        } catch (IOException e) {
            throw new APIException(e.getMessage(), 1);
        }
    }

    public static TexturesRequest getTextures(String uuid) throws APIException {
        try {
            String data = Utils.get(SESSION_ENDPOINT + "session/minecraft/profile/" + uuid + "?unsigned=false");
            if (new JSONObject(data).has("error")) {
                throw new APIException(400);
            }
            JSONObject json = new JSONObject(new String(Base64.decode(new JSONObject(data).getJSONArray("properties").getJSONObject(0).getString("value"))));
            TexturesRequest texture = new TexturesRequest();
            texture.setSignature(new JSONObject(data).getJSONArray("properties").getJSONObject(0).getString("signature"));
            if (json.getJSONObject("textures").has("SKIN")) {
                texture.setHasSkin(true);
                texture.setSkin(json.getJSONObject("textures").getJSONObject("SKIN").getString("url"));
                if (json.getJSONObject("textures").getJSONObject("SKIN").has("metadata") &&
                        json.getJSONObject("textures").getJSONObject("SKIN").getJSONObject("metadata").getString("model").equalsIgnoreCase("slim")) {
                    texture.setSlim(true);
                }
            }
            if (json.getJSONObject("textures").has("CAPE")) {
                texture.setHasCape(true);
                texture.setCape(json.getJSONObject("textures").getJSONObject("CAPE").getString("url"));
            }
            return texture;
        } catch (IOException e) {
            throw new APIException(e.getMessage(), 1);
        }
    }
}
