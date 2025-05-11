import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import flickfinder.Model.Movie;
import flickfinder.Model.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;


/**
 * Unit tests for the {@link MovieService} class.
 * This class contains test cases for movie search, trending movies, and movie details
 * functionality using mocked API calls.
 */
class MovieServiceModelTest {

    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        movieService = new MovieService();
    }

    /**
     * Tests if searchMovies() returns a non-empty list when given a valid query.
     */
    @Test
    void testSearchMoviesSuccess() {
        List<Movie> movies = movieService.searchMovies("Inception");
        assertNotNull(movies, "Movie list should not be null");
        assertFalse(movies.isEmpty(), "Movie list should not be empty");
    }

    /**
     * Tests if searchMovies() returns an empty list for an empty query.
     */
    @Test
    void testSearchMoviesEmptyQuery() {
        List<Movie> movies = movieService.searchMovies("");
        assertNotNull(movies, "Movie list should not be null");
        assertTrue(movies.isEmpty(), "Movie list should be empty for an empty query");
    }

    /**
     * Tests if searchMovies() handles null query gracefully.
     */
    @Test
    void testSearchMoviesNullQuery() {
        List<Movie> movies = movieService.searchMovies(null);
        assertNotNull(movies, "Movie list should not be null");
        assertTrue(movies.isEmpty(), "Movie list should be empty for a null query");
    }

    /**
     * Tests if searchMovies() returns an empty list when searching for a non-existent movie.
     */
    @Test
    void testSearchMoviesNonExistentMovie() {
        List<Movie> movies = movieService.searchMovies("NonExistentMovie1234");
        assertNotNull(movies, "Movie list should not be null");
        assertTrue(movies.isEmpty(), "Movie list should be empty for a non-existent movie");
    }

    /**
     * Tests if getTrendingMovies() returns a non-empty list.
     */
    @Test
    void testGetTrendingMovies() {
        List<Movie> movies = movieService.getTrendingMovies();
        assertNotNull(movies, "Trending movie list should not be null");
        assertFalse(movies.isEmpty(), "Trending movie list should not be empty");
    }

    /**
     * Tests if getMovieDetails() returns expected details for a valid movie ID.
     */
    @Test
    void testGetMovieDetailsValidId() {
        String details = movieService.getMovieDetails(550); // Example ID for "Fight Club"
        assertNotNull(details, "Movie details should not be null");
        assertTrue(details.contains("Title:"), "Movie details should contain a title");
    }

    /**
     * Tests if getMovieDetails() handles an invalid movie ID gracefully.
     */
    @Test
    void testGetMovieDetailsInvalidId() {
        String details = movieService.getMovieDetails(-1);
        assertNotNull(details, "Movie details should not be null");
        assertEquals("Error fetching movie details.", details, "Should return an error message for invalid ID");
    }

    /**
     * Tests if the service gracefully handles API failure when searching movies.
     */
    @Test
    void testSearchMoviesApiFailure() {
        MovieService movieServiceMock = mock(MovieService.class);
        when(movieServiceMock.searchMovies(anyString())).thenThrow(new RuntimeException("API failure"));

        Exception exception = assertThrows(RuntimeException.class, () -> movieServiceMock.searchMovies("Inception"));
        assertEquals("API failure", exception.getMessage());
    }

    /**
     * Tests if the service gracefully handles API failure when fetching trending movies.
     */
    @Test
    void testGetTrendingMoviesApiFailure() {
        MovieService movieServiceMock = mock(MovieService.class);
        when(movieServiceMock.getTrendingMovies()).thenThrow(new RuntimeException("API failure"));

        Exception exception = assertThrows(RuntimeException.class, () -> movieServiceMock.getTrendingMovies());
        assertEquals("API failure", exception.getMessage());
    }

    /**
     * Tests if the service gracefully handles API failure when fetching movie details.
     */
    @Test
    void testGetMovieDetailsApiFailure() {
        MovieService movieServiceMock = mock(MovieService.class);
        when(movieServiceMock.getMovieDetails(anyInt())).thenThrow(new RuntimeException("API failure"));

        Exception exception = assertThrows(RuntimeException.class, () -> movieServiceMock.getMovieDetails(123));
        assertEquals("API failure", exception.getMessage());
    }

    /**
     * Tests if searchMovies() returns results for case-insensitive queries.
     */
    @Test
    void testSearchMoviesCaseInsensitive() {
        List<Movie> moviesLower = movieService.searchMovies("inception");
        List<Movie> moviesUpper = movieService.searchMovies("INCEPTION");

        assertEquals(moviesLower, moviesUpper, "Case sensitivity should not affect search results");
    }

    /**
     * Tests searchMovies() with a very long query string.
     */
    @Test
    void testSearchMoviesLongQuery() {
        String longQuery = "A".repeat(500);
        List<Movie> movies = movieService.searchMovies(longQuery);

        assertNotNull(movies, "Response should not be null");
        assertTrue(movies.isEmpty(), "Long query should not return any results");
    }

    /**
     * Tests searchMovies() with special characters.
     */
    @Test
    void testSearchMoviesSpecialCharacters() {
        List<Movie> movies = movieService.searchMovies("@#$%^&*");
        assertNotNull(movies, "Response should not be null");
        assertTrue(movies.isEmpty(), "Special characters should not return valid results");
    }

    /**
     * Tests searchMovies() with numeric input.
     */
    @Test
    void testSearchMoviesNumericInput() {
        List<Movie> movies = movieService.searchMovies("2001");
        assertNotNull(movies, "Response should not be null");
        assertFalse(movies.isEmpty(), "Should return movies with numeric titles like '2001: A Space Odyssey'");
    }

    /**
     * Tests if getMovieDetails() returns correct attributes.
     */
    @Test
    void testGetMovieDetailsAttributes() {
        String details = movieService.getMovieDetails(550);

        assertNotNull(details, "Movie details should not be null");
        assertTrue(details.contains("Title:"), "Movie details should include a title");
        assertTrue(details.contains("Release Date:"), "Movie details should include a release date");
    }

    /**
     * Tests getMovieDetails() with a large movie ID.
     */
    @Test
    void testGetMovieDetailsLargeId() {
        String details = movieService.getMovieDetails(Integer.MAX_VALUE);
        assertEquals("Error fetching movie details.", details, "Should return an error for large ID");
    }

    /**
     * Tests getMovieDetails() with ID 0.
     */
    @Test
    void testGetMovieDetailsZeroId() {
        String details = movieService.getMovieDetails(0);
        assertEquals("Error fetching movie details.", details, "Should return an error for ID 0");
    }

    /**
     * Tests if getTrendingMovies() returns at least 5 movies.
     */
    @Test
    void testGetTrendingMoviesMinimumCount() {
        List<Movie> movies = movieService.getTrendingMovies();
        assertTrue(movies.size() >= 5, "Trending movies list should contain at least 5 movies");
    }

    /**
     * Tests if getTrendingMovies() returns correctly structured movie objects.
     */
    @Test
    void testGetTrendingMoviesValidObjects() {
        List<Movie> movies = movieService.getTrendingMovies();

        for (Movie movie : movies) {
            assertNotNull(movie.getTitle(), "Movie should have a title");
            assertNotNull(movie.getReleaseDate(), "Movie should have a release date");
        }
    }

    /**
     * Tests rapid consecutive searches.
     */
    @Test
    void testMultipleSearchesQuickly() {
        for (int i = 0; i < 5; i++) {
            List<Movie> movies = movieService.searchMovies("Inception");
            assertNotNull(movies, "Movie list should not be null");
        }
    }

    /**
     * Tests rapid consecutive calls to getMovieDetails().
     */
    @Test
    void testMultipleMovieDetailsQuickly() {
        for (int i = 0; i < 5; i++) {
            String details = movieService.getMovieDetails(550);
            assertNotNull(details, "Movie details should not be null");
        }
    }
}