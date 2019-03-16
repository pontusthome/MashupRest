package MashupRest.network;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import MashupRest.network.musicBrainz.MusicBrainzService;
import MashupRest.network.musicBrainz.model.MusicBrainzArtistResponse;
import MashupRest.network.musicBrainz.model.MusicBrainzRelations;
import MashupRest.network.wikidata.WikidataService;

@RestController
public class MashupController {
	
    static final String WIKIDATA_RELATION_TYPE = "wikidata";
    static final String WIKIDATA_RELATION_BASE_URL = "https://www.wikidata.org/wiki/";
	
    @Autowired
    private MusicBrainzService musicBrainzService;
	
    @Autowired
    private WikidataService wikidataService;

    @GetMapping("/artist/{artist}")
    public String getArtist(@PathVariable("artist") String artistMBID) throws IOException {
    	MusicBrainzArtistResponse musicBrainzArtist = musicBrainzService.getArtist(artistMBID);
        
        String url = "";
        for (MusicBrainzRelations relation: musicBrainzArtist.getRelations()) {
        	if (relation.getType().equals(WIKIDATA_RELATION_TYPE)) {
        		url = relation.getUrl().getResource();
        		break;
        	}
        }
        
        String wikidataArtistId = url.replace(WIKIDATA_RELATION_BASE_URL, "");
        
        String wikidataJsonStr = wikidataService.getArtist(wikidataArtistId);
        
		try {
			JSONObject wikidataJson = new JSONObject(wikidataJsonStr);
	        JSONObject wikidataEntities = wikidataJson.getJSONObject("entities");
	        JSONObject wikidataEntity = wikidataEntities.getJSONObject(wikidataArtistId);
	        
	        return wikidataEntity == null ? "" : wikidataEntity.toString();
	        
		} catch (JSONException e) {
	        throw new IOException("Unable to parse response from Wikidata");
		}
    }
}
