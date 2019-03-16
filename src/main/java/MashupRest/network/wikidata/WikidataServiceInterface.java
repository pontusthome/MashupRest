package MashupRest.network.wikidata;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikidataServiceInterface {
    @GET("api.php")
    Call<String> getArtist(
    		@Query("ids") String artistId,
    		@Query("action") String action,
    		@Query("format") String format,
			@Query("props") String props);
}
