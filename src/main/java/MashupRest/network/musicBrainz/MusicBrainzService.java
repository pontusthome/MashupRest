package MashupRest.network.musicBrainz;

import java.io.IOException;

import org.springframework.stereotype.Service;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Service
public class MusicBrainzService implements MusicBrainzConfiguration {

    private MusicBrainzServiceInterface service;

    public MusicBrainzService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MUSIC_BRAINZ_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MusicBrainzServiceInterface.class);
    }

    public MusicBrainzArtistResponse getArtist(String artistMBID) throws IOException {
        Call<MusicBrainzArtistResponse> retrofitCall = service.getArtist(artistMBID, RESPONSE_FORMAT, ARTIST_INCLUDE);

        Response<MusicBrainzArtistResponse> response = retrofitCall.execute();

        if (!response.isSuccessful()) {
            throw new IOException(response.errorBody() != null
                    ? response.errorBody().string() : "Unknown error");
        }

        return response.body();
    }

}
