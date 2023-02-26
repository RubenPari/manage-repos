import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import routes.Routes;

public class server {
    public static void main(String[] args) {
        // create server
        var app = Javalin.create();

        // load and set .env in session
        Dotenv env = Dotenv.load();
        app.before(ctx -> ctx.sessionAttribute("env", env));

        // setup db
        try (
                JedisPool pool = new JedisPool("localhost", 6379)) {
            Jedis jedis = pool.getResource();
            app.before(ctx -> ctx.sessionAttribute("db", jedis));
        }

        // setup routes
        Routes.setupRoutes(app);

        // start server
        app.start(3000);
    }
}
