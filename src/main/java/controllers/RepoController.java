package controllers;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import io.javalin.http.Context;

public class RepoController {
    /**
     * Delete all repos from the older to n
     *
     * @param ctx
     * @throws IOException
     */
    public static void deleteLatests(@NotNull Context ctx) throws IOException {
        // get access token from session
        String accessToken = ctx.sessionAttribute("accessToken");

        // get github client
        GitHub github = new GitHubBuilder().withOAuthToken(accessToken).build();

        // get limit path param
        int limit = Integer.parseInt(ctx.pathParam("number"));

        // initialize response
        JSONObject response = new JSONObject();

        // delete all repos from the older to n
        github.getMyself().listRepositories()
                .toList().stream().skip(limit).forEach(repo -> {
                    try {
                        repo.delete();
                    } catch (Exception e) {
                        // log error
                        System.out.println(e.getMessage());

                        // return response with error
                        response.put("status", "error");
                        response.put("error", e.getMessage());

                        return;
                    }
                });

        // return response

        ctx.json(response);
    }
}
