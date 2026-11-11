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
        // The tiny 3-word fixture has different counts from the 14377-word file.
        // Build the expected HTTP result directly from raw CSV, without NGramMap.
        java.util.Map<Integer, Double> totals = new java.util.HashMap<>();
        try (var lines = Files.lines(Path.of("data/year_history.csv"))) {
            lines.forEach(line -> {
                String[] fields = line.split(",");
                totals.put(Integer.parseInt(fields[0]), Double.parseDouble(fields[1]));
            });
        }
        java.util.Map<String, java.util.TreeMap<Integer, Double>> counts = new java.util.HashMap<>();
        counts.put("request", new java.util.TreeMap<>());
        counts.put("airport", new java.util.TreeMap<>());
        try (var lines = Files.lines(Path.of("data/word_history_size14377.csv"))) {
            lines.forEach(line -> {
                int tab = line.indexOf('\t');
                String word = line.substring(0, tab);
                if (counts.containsKey(word)) {
                    String[] fields = line.split("\t");
                    int year = Integer.parseInt(fields[1]);
                    if (year >= 2006 && year <= 2007) {
                        counts.get(word).put(year, Double.parseDouble(fields[2]) / totals.get(year));
                    }
                }
            });
        }
        String expected = "request: " + counts.get("request") + "\nairport: " + counts.get("airport") + "\n";
        Files.writeString(Path.of("evidence/http-raw-csv-expected.txt"), expected);
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
