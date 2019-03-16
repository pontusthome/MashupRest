package MashupRest.network.musicBrainz;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MusicBrainzServiceInterface {
    @GET("artist/{artist}")
    Call<MusicBrainzArtistResponse> getArtist(
    		@Path("artist") String artistMBID,
    		@Query("fmt") String responseFormat,
    		@Query("inc") String include);

}
