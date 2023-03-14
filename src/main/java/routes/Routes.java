package routes;

import controllers.AuthController;
import controllers.RepoController;
import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;

public class Routes {
    public static void setupRoutes(@NotNull Javalin app) {
        // ##### Auth routes #####
        app.get("/auth/login", AuthController::login);
        app.get("/auth/callback", AuthController::callback);

        // ##### Repo routes #####
        app.delete("/repos/delete/{number}", RepoController::deleteLatests);
        app.get("/repos/by-lang/{lang}", RepoController::getByLang);
    }
}
