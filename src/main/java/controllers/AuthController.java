package controllers;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.http.Context;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;

public class AuthController {
    public static void login(@NotNull Context ctx) {
        Dotenv env = ctx.sessionAttribute("env");

        String url = "https://github.com" +
                "/login/oauth/authorize" +
                "?scope=user:email%20delete_repo" +
                "&client_id=" + env.get("CLIENT_ID");

        // redirect to page
        ctx.redirect(url);
    }

    public static void callback(@NotNull Context ctx) throws IOException {
        // get env from session
        Dotenv env = ctx.sessionAttribute("env");

        // get db from session
        Jedis db = ctx.sessionAttribute("db");

        // get code from query
        String code = ctx.queryParam("code");

        // create url for POST
        String url = "https://github.com" +
                "/login/oauth/access_token" +
                "?client_id=" + env.get("CLIENT_ID") +
                "&client_secret=" + env.get("CLIENT_SECRET") +
                "&code=" + code;

        // make POST request
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(null, new byte[0]);
        Request requestOAuth = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response responseOAuth = client.newCall(requestOAuth).execute();

        // get access token from response
        String accessToken = responseOAuth.body().string().split("&")[0].split("=")[1];

        // try access token for check if it's valid
        Request requestUserInfo = new Request.Builder().get()
                .url("https://api.github.com/user")
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer " + accessToken)
                .build();
        Response responseUserInfo = client.newCall(requestUserInfo).execute();

        if (responseUserInfo.code() != 200) {
            JSONObject message = new JSONObject();
            message.put("status", "error");
            message.put("message", "Invalid access token");

            ctx.json(message);
        }

        // save access token in redis db
        db.set("accessToken", accessToken);

        // return response
        JSONObject message = new JSONObject();
        message.put("status", "success");
        message.put("message", "Successfully logged in");

        ctx.json(message);
    }
}
