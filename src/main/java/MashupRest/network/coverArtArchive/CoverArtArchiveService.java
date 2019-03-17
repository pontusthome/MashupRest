package MashupRest.network.coverArtArchive;

import java.io.IOException;

import org.springframework.stereotype.Service;

import MashupRest.network.coverArtArchive.model.CoverArtArchiveResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Service
public class CoverArtArchiveService implements CoverArtArchiveConfiguration {
	
    private CoverArtArchiveServiceInterface service;

    public CoverArtArchiveService() {
    	
    	HttpLoggingInterceptor logging = new HttpLoggingInterceptor();  
    	logging.setLevel(Level.BODY);

    	OkHttpClient.Builder httpClient = new OkHttpClient.Builder();  
    	httpClient.addInterceptor(logging); 

        Retrofit retrofit = new Retrofit.Builder()
        		.client(httpClient.build())
                .baseUrl(COVER_ART_ARCHIVE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(CoverArtArchiveServiceInterface.class);
    }

    	public CoverArtArchiveResponse getCoverArt(String artistMBID) throws IOException {
        Call<CoverArtArchiveResponse> retrofitCall = service.getCoverArt(artistMBID);

        Response<CoverArtArchiveResponse> response = retrofitCall.execute();

        if (!response.isSuccessful()) {
            throw new IOException(response.errorBody() != null
                    ? response.errorBody().string() : "Unknown error");
        }
        
        return response.body();
    }
}
