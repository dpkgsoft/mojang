package com.dpkgsoft.mojang;

import com.dpkgsoft.mojang.request.AuthRequest;
import com.dpkgsoft.mojang.request.RefreshRequest;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class MojangAuth {
    private static final String API_ENDPOINT = "https://authserver.mojang.com/";

    public static AuthRequest authenticate(String login, String password, String clientToken) throws APIException {
        JSONObject requestJson = new JSONObject();
        requestJson.put("agent", new JSONObject().put("name", "Minecraft").put("version", 1))
                .put("username", login)
                .put("password", password)
                .put("clientToken", clientToken);
        try {
            String json = Utils.postJson(API_ENDPOINT + "/authenticate", requestJson.toString());
            JSONObject auth = new JSONObject(json);
            if (auth.has("error")) {
                if (auth.getString("error").equalsIgnoreCase("ForbiddenOperationException") &&
                    auth.has("cause") && auth.getString("cause").equalsIgnoreCase("UserMigratedException")) {
                    throw new APIException("User migrated", 400);
                } else if (auth.getString("error").equalsIgnoreCase("ForbiddenOperationException")) {
                    throw new APIException("Invalid credentials", 403);
                } else throw new APIException(auth.getString("errorMessage"), 400);
            }
            return new AuthRequest(auth.getString("accessToken"), auth.getString("clientToken"),
                    new Profile(auth.getJSONObject("selectedProfile").getString("id"),
                            auth.getJSONObject("selectedProfile").getString("name")));
        } catch (IOException e) {
            throw new APIException(e.toString(), 1);
        }
    }

    public static AuthRequest authenticate(String login, String password) throws APIException {
        return authenticate(login, password, UUID.randomUUID().toString().replace("-", ""));
    }

    public static RefreshRequest refresh(String accessToken, String clientToken) throws APIException {
        JSONObject requestJson = new JSONObject();
        requestJson.put("accessToken", accessToken)
                .put("clientToken", clientToken);
        try {
            String json = Utils.postJson(API_ENDPOINT + "/refresh", requestJson.toString());
            JSONObject auth = new JSONObject(json);
            if (auth.has("error")) {
                if (auth.getString("error").equalsIgnoreCase("ForbiddenOperationException")) {
                    throw new APIException("Invalid credentials", 403);
                } else throw new APIException(auth.getString("errorMessage"), 400);
            }
            return new RefreshRequest(auth.getString("accessToken"), auth.getString("clientToken"),
                    new Profile(auth.getJSONObject("selectedProfile").getString("id"),
                            auth.getJSONObject("selectedProfile").getString("name")));
        } catch (IOException e) {
            throw new APIException(e.toString(), 1);
        }
    }

    public static boolean validate(String accessToken, String clientToken) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("accessToken", accessToken)
                .put("clientToken", clientToken);
        try {
            String data = Utils.postJson(API_ENDPOINT + "/validate", requestJson.toString());
            return data.equalsIgnoreCase("");
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean invalidate(String accessToken, String clientToken) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("accessToken", accessToken)
                .put("clientToken", clientToken);
        try {
            String data = Utils.postJson(API_ENDPOINT + "/invalidate", requestJson.toString());
            return data.equalsIgnoreCase("");
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean signout(String login, String password) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("username", login)
                .put("password", password);
        try {
            String data = Utils.postJson(API_ENDPOINT + "/signout", requestJson.toString());
            return data.equalsIgnoreCase("");
        } catch (IOException e) {
            return false;
        }
    }
}
