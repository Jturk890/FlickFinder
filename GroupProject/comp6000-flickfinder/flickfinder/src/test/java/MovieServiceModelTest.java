import flickfinder.Model.MovieService;
import flickfinder.Model.Movie;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Integration/unit hybrid tests for {@link MovieService}.
 *
 * - Calls that hit TMDb (search/trending/details/genres/genreDiscover) are true integration tests.
 *   They may fail offline or if the token is invalid. Mark with @Disabled if needed.
 * - Recommendation logic is isolated with a Mockito spy to avoid real HTTP calls.
 */
class MovieServiceModelTest {

    private MovieService service;

    @BeforeEach
    void setUp() {
        service = new MovieService(); // uses real HTTP under the hood
    }

    // ---------- searchMovies ----------

    @Test
    @DisplayName("searchMovies: valid query returns non-empty list")
    void searchMovies_validQuery_nonEmpty() {
        List<Movie> movies = service.searchMovies("Inception");
        assertNotNull(movies);
        assertFalse(movies.isEmpty()); // should find at least one
    }

    @Test
    @DisplayName("searchMovies: empty query returns empty list")
    void searchMovies_emptyQuery_emptyList() {
        List<Movie> movies = service.searchMovies("");
        assertNotNull(movies);
        assertTrue(movies.isEmpty());
    }

    @Test
    @DisplayName("searchMovies: null query handled gracefully")
    void searchMovies_nullQuery_emptyList() {
        List<Movie> movies = service.searchMovies(null);
        assertNotNull(movies);
        assertTrue(movies.isEmpty());
    }

    @Test
    @DisplayName("searchMovies: special characters")
    void searchMovies_specialChars_noCrash() {
        List<Movie> movies = service.searchMovies("@#$%^&*");
        assertNotNull(movies); // just ensure no exception
    }

    @Test
    @DisplayName("searchMovies: long query doesn't crash and is fast")
    void searchMovies_longQuery_timeoutOk() {
        assertTimeout(Duration.ofSeconds(5), () -> {
            String longQ = "A".repeat(400);
            List<Movie> movies = service.searchMovies(longQ);
            assertNotNull(movies);
        });
    }

    // ---------- getTrendingMovies ----------

    @Test
    @DisplayName("getTrendingMovies: returns list with at least 1 item")
    void getTrendingMovies_basic() {
        List<Movie> movies = service.getTrendingMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
    }

    @Test
    @DisplayName("getTrendingMovies: movies have essential fields populated")
    void getTrendingMovies_fieldsPresent() {
        List<Movie> movies = service.getTrendingMovies();
        assertFalse(movies.isEmpty());
        Movie m = movies.get(0);
        assertNotNull(m.getTitle());
        assertNotNull(m.getReleaseDate());
    }

    // ---------- getMovieDetails / getMovieDetailsRaw ----------

    @Test
    @DisplayName("getMovieDetails: valid ID returns formatted text")
    void getMovieDetails_validId_containsTitle() {
        String details = service.getMovieDetails(550); // Fight Club
        assertNotNull(details);
        assertTrue(details.contains("Title:"));
        assertTrue(details.contains("Release Date:"));
    }

    @Test
    @DisplayName("getMovieDetails: invalid ID returns error message")
    void getMovieDetails_invalidId_errorString() {
        String details = service.getMovieDetails(-1);
        assertEquals("Error fetching movie details.", details);
    }

    @Test
    @DisplayName("getMovieDetailsRaw: valid ID returns JSON string")
    void getMovieDetailsRaw_validId_jsonContainsTitle() {
        String raw = service.getMovieDetailsRaw(550);
        assertNotNull(raw);
        assertTrue(raw.contains("title"));
    }

    @Test
    @DisplayName("getMovieDetailsRaw: invalid ID returns empty JSON")
    void getMovieDetailsRaw_invalidId_returnsEmptyJson() {
        String raw = service.getMovieDetailsRaw(-123);
        assertEquals("{}", raw);
    }

    // ---------- getGenres ----------

    @Test
    @DisplayName("getGenres: returns non-empty map")
    void getGenres_nonEmpty() {
        Map<Integer, String> genres = service.getGenres();
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
    }

    @Test
    @DisplayName("getGenres: contains Action or Drama")
    void getGenres_containsCommonGenre() {
        Map<Integer, String> genres = service.getGenres();
        assertTrue(genres.containsValue("Action")
                || genres.containsValue("Drama")
                || genres.containsValue("Comedy"));
    }

    // ---------- getMoviesByGenre ----------

    @Test
    @DisplayName("getMoviesByGenre: valid genre returns list")
    void getMoviesByGenre_validId_nonEmpty() {
        // 28 is Action on TMDb
        List<Movie> movies = service.getMoviesByGenre(28);
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
    }

    @Test
    @DisplayName("getMoviesByGenre: invalid genre returns non-null list")
    void getMoviesByGenre_invalidId_noCrash() {
        List<Movie> movies = service.getMoviesByGenre(-9999);
        assertNotNull(movies); // may be empty but should not be null
    }

    // ---------- getRecommendations ----------

    @Test
    @DisplayName("getRecommendations: empty userGenres returns empty or non-null list")
    void getRecommendations_emptyGenres_noCrash() {
        List<Movie> recs = service.getRecommendations(Collections.emptySet());
        assertNotNull(recs); // likely empty, but not null
    }

    @Test
    @DisplayName("getRecommendations: matching genre returns subset of trending")
    void getRecommendations_withGenres_usesTrendingAndDetails() {
        // Spy: reuse real object but stub some calls
        MovieService spy = Mockito.spy(service);

        // Dummy trending list
        Movie m1 = new Movie(1, "Movie1", "p1", "2020-01-01", "ov", 0, 0, "", new ArrayList<>());
        Movie m2 = new Movie(2, "Movie2", "p2", "2020-02-02", "ov", 0, 0, "", new ArrayList<>());
        doReturn(List.of(m1, m2)).when(spy).getTrendingMovies();

        // Stub detail JSONs to include genres
        String actionJson = "{\"genres\":[{\"name\":\"Action\"}]}";
        String dramaJson  = "{\"genres\":[{\"name\":\"Drama\"}]}";
        doReturn(actionJson).when(spy).getMovieDetailsRaw(1);
        doReturn(dramaJson).when(spy).getMovieDetailsRaw(2);

        List<Movie> recs = spy.getRecommendations(Set.of("Action"));
        assertEquals(1, recs.size());
        assertEquals(1, recs.get(0).getId());
    }

    @Test
    @DisplayName("getRecommendations: ignores movies without genres or errors")
    void getRecommendations_ignoresErrors() {
        MovieService spy = Mockito.spy(service);
        Movie m1 = new Movie(1, "Movie1", "p1", "2020-01-01", "ov", 0, 0, "", new ArrayList<>());
        Movie m2 = new Movie(2, "Movie2", "p2", "2020-02-02", "ov", 0, 0, "", new ArrayList<>());
        doReturn(List.of(m1, m2)).when(spy).getTrendingMovies();

        // One malformed, one unrelated genre
        doReturn("{}").when(spy).getMovieDetailsRaw(1);
        doReturn("{\"genres\":[{\"name\":\"Comedy\"}]}")
                .when(spy).getMovieDetailsRaw(2);

        List<Movie> recs = spy.getRecommendations(Set.of("Action"));
        assertTrue(recs.isEmpty());
    }

    // --- Extra tests for getRecommendations --------------------------------------

    @Test
    @DisplayName("getRecommendations: returns empty list when trending is empty")
    void recommendations_emptyTrending_returnsEmpty() {
        MovieService spy = Mockito.spy(service);
        doReturn(Collections.emptyList()).when(spy).getTrendingMovies();

        List<Movie> recs = spy.getRecommendations(Set.of("Action"));
        assertNotNull(recs);
        assertTrue(recs.isEmpty());
    }

    @Test
    @DisplayName("getRecommendations: case-insensitive genre matching")
    void recommendations_caseInsensitiveMatch() {
        MovieService spy = Mockito.spy(service);
        Movie m = new Movie(10, "Case Movie", "", "", "", 0, 0, "", new ArrayList<>());
        doReturn(List.of(m)).when(spy).getTrendingMovies();
        doReturn("{\"genres\":[{\"name\":\"AcTiOn\"}]}").when(spy).getMovieDetailsRaw(10);

        List<Movie> recs = spy.getRecommendations(Set.of("action"));
        assertEquals(1, recs.size());
        assertEquals(10, recs.get(0).getId());
    }

    @Test
    @DisplayName("getRecommendations: movie with multiple matching genres is added only once")
    void recommendations_movieAddedOnce() {
        MovieService spy = Mockito.spy(service);
        Movie m = new Movie(11, "Multi Genre", "", "", "", 0, 0, "", new ArrayList<>());
        doReturn(List.of(m)).when(spy).getTrendingMovies();
        // Two genres match user set
        doReturn("{\"genres\":[{\"name\":\"Action\"},{\"name\":\"Drama\"}]}")
                .when(spy).getMovieDetailsRaw(11);

        List<Movie> recs = spy.getRecommendations(Set.of("Action", "Drama"));
        assertEquals(1, recs.size());
        assertEquals(11, recs.get(0).getId());
    }

    @Test
    @DisplayName("getRecommendations: null userGenres -> NPE (documents current behavior)")
    void recommendations_nullGenres_throwsNpe() {
        MovieService spy = Mockito.spy(service);
        doReturn(Collections.emptyList()).when(spy).getTrendingMovies();
        assertThrows(NullPointerException.class, () -> spy.getRecommendations(null));
        // If you prefer safe behavior, fix the service (return empty list when userGenres == null)
    }

    @Test
    @DisplayName("getRecommendations: detail call exception is swallowed and movie skipped")
    void recommendations_detailException_skipsMovie() {
        MovieService spy = Mockito.spy(service);
        Movie m = new Movie(12, "Error Movie", "", "", "", 0, 0, "", new ArrayList<>());
        doReturn(List.of(m)).when(spy).getTrendingMovies();

        // Simulate exception during details fetch
        doThrow(new RuntimeException("boom")).when(spy).getMovieDetailsRaw(12);

        List<Movie> recs = spy.getRecommendations(Set.of("Action"));
        assertTrue(recs.isEmpty());
    }


    // ---------- Performance / repeated calls ----------

    @Test
    @DisplayName("Multiple rapid calls to movie details")
    void multipleMovieDetailsRapid_noCrash() {
        for (int i = 0; i < 3; i++) {
            String d = service.getMovieDetails(550);
            assertNotNull(d);
        }
    }

    @Test
    @DisplayName("Multiple rapid searches")
    void multipleSearchRapid_noCrash() {
        for (int i = 0; i < 3; i++) {
            List<Movie> list = service.searchMovies("Matrix");
            assertNotNull(list);
        }
    }
}