package MashupRest.network.wikidata;

import MashupRest.network.wikidata.model.WikidataResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikidataServiceInterface {
    @GET("api.php")
    Call<WikidataResponse> getArtist(
    		@Query("ids") String artistId,
    		@Query("action") String action,
    		@Query("format") String format,
			@Query("props") String props);
}
