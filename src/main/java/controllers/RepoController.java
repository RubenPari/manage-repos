package controllers;

import java.io.IOException;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import io.javalin.http.Context;
import redis.clients.jedis.Jedis;
import utils.Utils;

public class RepoController {

    private static final Logger log = Logger.getLogger(RepoController.class.getName());

    /**
     * Delete all repos from the older to n
     *
     * @param ctx
     * @throws IOException
     */
    public static void deleteLatests(@NotNull Context ctx) throws IOException {
        // get db from session
        Jedis db = ctx.sessionAttribute("db");

        // get access token from db
        String accessToken = db.get("accessToken");

        // get github client
        GitHub github = new GitHubBuilder().withOAuthToken(accessToken).build();

        // get limit path param
        int limit = Integer.parseInt(ctx.pathParam("number"));

        // delete all repos from the older to n
        GHRepository[] repos = github.getMyself().listRepositories().toArray();

        // order list by creation date by oldest to newest
        GHRepository[] reposOrder = Utils.quickSort(repos);

        // delete the oldest n repos
        for (int i = 0; i < limit; i++) {
            reposOrder[i].delete();

            log.info("Deleted repo: " + reposOrder[i].getName() + " with date: " + reposOrder[i].getCreatedAt());
        }

        // return response
        ctx.json(new JSONObject() {
            {
                put("status", "success");
                put("message", "Deleted " + limit + " repos");
            }
        });
    }

    /**
     * Get all repos by language
     *
     * @param ctx
     * @throws IOException
     */
    public static void getByLang(@NotNull Context ctx) throws IOException {
        // get db from session
        Jedis db = ctx.sessionAttribute("db");

        // get access token from db
        String accessToken = db.get("accessToken");

        // get github client
        GitHub github = new GitHubBuilder().withOAuthToken(accessToken).build();

        // get language path param
        String lang = ctx.pathParam("lang");

        // get repos by language
        GHRepository[] repos = github.getMyself().listRepositories().toArray();

        // filter repos by language
        GHRepository[] reposFiltered = Utils.filterByLang(repos, lang);

        // return response
        ctx.json(reposFiltered);

    }
}
