package controller.service;

import com.google.gson.Gson;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TmdbService {

    private static final String API_KEY = "1d2e053cc499a6686eb780d42bb4bff8";

    private static final String SEARCH_URL = "https://api.themoviedb.org/3/search/movie";
    private static final String POPULAR_URL = "https://api.themoviedb.org/3/movie/popular";

    public List<TmdbMovie> searchMovies(String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return new ArrayList<>();
            }
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            String url = SEARCH_URL + "?api_key=" + API_KEY + "&query=" + encodedQuery + "&language=it-IT";

            return callApi(url);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<TmdbMovie> getPopularMovies() {
        try {
            String url = POPULAR_URL + "?api_key=" + API_KEY + "&language=it-IT&page=1";
            return callApi(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public TmdbMovie getMovieById(String id) {
        try {
            String url = "https://api.themoviedb.org/3/movie/" + id + "?api_key=" + API_KEY + "&language=it-IT";
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder().uri(java.net.URI.create(url)).GET().build();
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return new com.google.gson.Gson().fromJson(response.body(), TmdbMovie.class);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private List<TmdbMovie> callApi(String urlString) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Gson gson = new Gson();
                TmdbResponse tmdbResponse = gson.fromJson(response.body(), TmdbResponse.class);

                if (tmdbResponse != null && tmdbResponse.results != null) {
                    return tmdbResponse.results;
                }
            } else {
                System.out.println("Errore API: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public TmdbMovie getMovieDetails(int idTmdb) {
        try {
            String url = "https://api.themoviedb.org/3/movie/" + idTmdb + "?api_key=" + API_KEY + "&language=it-IT";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return new com.google.gson.Gson().fromJson(response.body(), TmdbMovie.class);
            } else {
                System.out.println("Errore API Dettagli: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}