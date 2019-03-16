package MashupRest.network.wikidata;

import java.io.IOException;

import org.springframework.stereotype.Service;

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

    public WikidataResponse getArtist(String artistId) throws IOException {
        Call<WikidataResponse> retrofitCall = service.getArtist(artistId, WIKIDATA_ARTIST_ACTION, WIKIDATA_RESPONSE_FORMAT, WIKIDATA_PROPS);

        Response<WikidataResponse> response = retrofitCall.execute();

        if (!response.isSuccessful()) {
            throw new IOException(response.errorBody() != null
                    ? response.errorBody().string() : "Unknown error");
        }

        return response.body();
    }
}
