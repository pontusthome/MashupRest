package MashupRest.network.coverArtArchive;

import MashupRest.network.coverArtArchive.model.CoverArtArchiveResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CoverArtArchiveServiceInterface {
	@GET("release-group/{MBID}")
    Call<CoverArtArchiveResponse> getCoverArt(
    		@Path("MBID") String artistMBID);
}
