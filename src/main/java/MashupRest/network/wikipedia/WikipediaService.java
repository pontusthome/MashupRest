package MashupRest.network.wikipedia;

import java.io.IOException;

import org.springframework.stereotype.Service;

import MashupRest.network.wikidata.WikidataServiceInterface;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Service
public class WikipediaService implements WikipediaConfiguration {
	
    private WikipediaServiceInterface service;

	public WikipediaService() {
    	
    	HttpLoggingInterceptor logging = new HttpLoggingInterceptor();  
    	logging.setLevel(Level.BODY);

    	OkHttpClient.Builder httpClient = new OkHttpClient.Builder();  
    	httpClient.addInterceptor(logging); 

    	Retrofit retrofit = new Retrofit.Builder()
    			.client(httpClient.build())
                .baseUrl(WIKIPEDIA_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        service = retrofit.create(WikipediaServiceInterface.class);
    }

    public String getWikiTitle(String wikiTitle) throws IOException {
        Call<String> retrofitCall = service.getTitleQuery(wikiTitle, WIKIPEDIA_ACTION, WIKIPEDIA_RESPONSE_FORMAT, WIKIPEDIA_PROP, WIKIPEDIA_EXINTRO, WIKIPEDIA_REDIRECTS);

        Response<String> response = retrofitCall.execute();

        if (!response.isSuccessful()) {
            throw new IOException(response.errorBody() != null
                    ? response.errorBody().string() : "Unknown error");
        }

        return response.body();
    }
}
