package MashupRest.network.wikidata;

import static MashupRest.network.AsynchConfiguration.ASYNC_EXECUTOR_BEAN_NAME;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import MashupRest.network.coverArtArchive.model.CoverArtArchiveResponse;
import MashupRest.network.wikidata.model.WikidataResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Service
public class WikidataService implements WikidataConfiguration {
	
    private WikidataServiceInterface service;

	public WikidataService() {
    	
    	HttpLoggingInterceptor logging = new HttpLoggingInterceptor();  
    	logging.setLevel(Level.BODY);

    	OkHttpClient.Builder httpClient = new OkHttpClient.Builder();  
    	httpClient.addInterceptor(logging); 

    	Retrofit retrofit = new Retrofit.Builder()
    			.client(httpClient.build())
                .baseUrl(WIKIDATA_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(WikidataServiceInterface.class);
    }

	@Async(ASYNC_EXECUTOR_BEAN_NAME)
	public CompletableFuture<WikidataResponse> getArtist(String artistId) {
        Call<WikidataResponse> retrofitCall = service.getArtist(artistId, WIKIDATA_ARTIST_ACTION, WIKIDATA_RESPONSE_FORMAT, WIKIDATA_PROPS);
        
        try {
            Response<WikidataResponse> response = retrofitCall.execute();
	
	        if (!response.isSuccessful()) {
	            throw new IOException(response.errorBody() != null
	                    ? response.errorBody().string() : "Unknown error");
	        }
	        
	        return CompletableFuture.completedFuture(response.body());
        } catch (IOException e) {
        	// ToDo: Error handling
        	System.out.print("Failed to get Wikidata for artist: " + artistId);
            return CompletableFuture.completedFuture(null);
        }
    }
}
