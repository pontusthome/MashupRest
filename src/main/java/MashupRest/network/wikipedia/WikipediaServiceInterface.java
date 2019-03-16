package MashupRest.network.wikipedia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikipediaServiceInterface {
	@GET("api.php")
    Call<String> getTitleQuery(
    		@Query("titles") String titles,
    		@Query("action") String action,
    		@Query("format") String format,
			@Query("prop") String prop,
			@Query("exintro") boolean exintro,
			@Query("redirects") boolean redirects);
}
