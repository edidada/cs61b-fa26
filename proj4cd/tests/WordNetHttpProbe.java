import com.google.gson.Gson;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;

public class WordNetHttpProbe {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String[][] queries = {
            {"food,cake&startYear=1950&endYear=1990&k=5", "[cake, cookie, kiss, snap, wafer]"},
            {"video,recording", "[video, video_recording, videocassette, videotape]"},
            {"pastry,tart", "[apple_tart, lobster_tart, quiche, quiche_Lorraine, tart, tartlet]"},
            {"missing-no-such-word", "[]"},
            {"video,recording,missing-no-such-word", "[]"}
        };
        StringBuilder evidence = new StringBuilder();
        for (String[] query : queries) {
            var response = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:4567/hyponyms?words=" + query[0] + (query[0].contains("&k=") ? "" : "&startYear=2100&endYear=1400&k=0"))).GET().build(), HttpResponse.BodyHandlers.ofString());
            String decoded = new Gson().fromJson(response.body(), String.class);
            if (response.statusCode() != 200 || !query[1].equals(decoded)) { throw new AssertionError(response.statusCode() + ": " + decoded); }
            evidence.append(query[0]).append(": ").append(decoded).append(" PASS\n");
        }
        Files.writeString(Path.of("evidence/http-results.txt"), evidence);
        System.out.print(evidence);
    }
}
