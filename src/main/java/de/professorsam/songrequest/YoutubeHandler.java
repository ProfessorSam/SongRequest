package de.professorsam.songrequest;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class YoutubeHandler {

    private final String apiKey;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public YoutubeHandler(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getIDFromLink(String link) {
        try {
            URI uri = new URI(link);
            String host = uri.getHost();
            String path = uri.getPath();
            String query = uri.getRawQuery();

            // Handle youtu.be/dQw4w9WgXcQ
            if (host != null && host.contains("youtu.be")) {
                return path.substring(1); // skip leading '/'
            }

            // Handle youtube.com/watch?v=ID
            if (host != null && host.contains("youtube.com")) {
                if (path.equals("/watch") && query != null) {
                    Map<String, String> params = parseQuery(query);
                    return params.get("v");
                }

                // Handle /embed/ID or /v/ID or /shorts/ID
                String[] segments = path.split("/");
                if (segments.length >= 2 && (
                        segments[1].equals("embed") ||
                                segments[1].equals("v") ||
                                segments[1].equals("shorts")
                )) {
                    return segments[2];
                }
            }
        } catch (Exception e) {
            // Handle invalid URLs gracefully
            System.err.println("Invalid YouTube URL: " + e.getMessage());
        }
        return null; // ID not found
    }

    private static Map<String, String> parseQuery(String query) throws Exception {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(entry[1], StandardCharsets.UTF_8));
            }
        }
        return result;
    }

    public Duration getYouTubeVideoDuration(String videoId)
            throws GeneralSecurityException, IOException {
        YouTube youtubeService = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                null
        ).setApplicationName("youtube-video-duration-checker").build();

        YouTube.Videos.List request = youtubeService.videos()
                .list("contentDetails")
                .setId(videoId)
                .setKey(apiKey);

        VideoListResponse response = request.execute();
        if (response.getItems().isEmpty()) {
            throw new IllegalArgumentException("Invalid video ID or video not found.");
        }

        Video video = response.getItems().getFirst();
        String durationISO8601 = video.getContentDetails().getDuration();

        return Duration.parse(durationISO8601);
    }
}
