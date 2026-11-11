import com.google.gson.Gson;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.util.Base64;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

/** Standalone remote Actions HTTP acceptance, against Main's actual Javalin server. */
public class HttpProbe {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String query = "?words=request,airport&startYear=2006&endYear=2007";
        var text = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:4567/historytext" + query)).GET().build(), HttpResponse.BodyHandlers.ofString());
        if (text.statusCode() != 200) { throw new AssertionError(text); }
        String decoded = new Gson().fromJson(text.body(), String.class);
        String expected = "request: {2006=2.44740192927834E-5, 2007=2.464488338318067E-5}\nairport: {2007=6.2068176510855946E-6}\n";
        if (!expected.equals(decoded)) { throw new AssertionError(decoded); }
        Files.writeString(Path.of("evidence/http-historytext.json"), text.body());
        var response = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:4567/history?words=cat,dog&startYear=1900&endYear=1950")).GET().build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) { throw new AssertionError(response); }
        byte[] png = Base64.getDecoder().decode(new Gson().fromJson(response.body(), String.class));
        var image = ImageIO.read(new ByteArrayInputStream(png));
        if (image == null || image.getWidth() != 800 || image.getHeight() != 600) { throw new AssertionError("Invalid PNG"); }
        Files.write(Path.of("evidence/http-cat-dog.png"), png);
        System.out.println("Real Javalin historytext exact JSON/newline values: PASS");
        System.out.println("Real Javalin history PNG 800x600: PASS");
    }
}
