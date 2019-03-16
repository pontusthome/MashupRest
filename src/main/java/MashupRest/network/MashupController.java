package MashupRest.network;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import MashupRest.network.musicBrainz.MusicBrainzService;
import MashupRest.network.musicBrainz.model.MusicBrainzArtistResponse;
import MashupRest.network.musicBrainz.model.MusicBrainzRelation;
import MashupRest.network.wikidata.WikidataService;
import MashupRest.network.wikipedia.WikipediaService;

@RestController
public class MashupController {
	
    static final String WIKIDATA_RELATION_TYPE = "wikidata";
    static final String WIKIDATA_RELATION_BASE_URL = "https://www.wikidata.org/wiki/";
	
    @Autowired
    private MusicBrainzService musicBrainzService;
	
    @Autowired
    private WikidataService wikidataService;

    @Autowired
    private WikipediaService wikipediaService;

    @GetMapping("/artist/{artist}")
    public String getArtist(@PathVariable("artist") String artistMBID) throws IOException {
    	
    	MusicBrainzArtistResponse musicBrainzArtist = musicBrainzService.getArtist(artistMBID);
        
        String wikidataArtistId = findWikidataArtistId(musicBrainzArtist.getRelations());
        if (wikidataArtistId == null) {
	        throw new IOException("Cannot find Wikidata artist Id from Music Brainz response");
        }
        
        String wikidataJsonStr = wikidataService.getArtist(wikidataArtistId);
        
        String enWikiTitle;
        try {
            enWikiTitle = findEnWikiTitle(wikidataJsonStr, wikidataArtistId);
		} catch (JSONException e) {
	        throw new IOException("Unable to parse response from Wikidata");
		}
        
        return wikipediaService.getWikiTitle(enWikiTitle);
    }
    
    private String findWikidataArtistId(List<MusicBrainzRelation> relations) {
        for (MusicBrainzRelation relation: relations) {
        	if (relation.getType().equals(WIKIDATA_RELATION_TYPE)) {
        		 String url = relation.getUrl().getResource();
        		 return url.replace(WIKIDATA_RELATION_BASE_URL, "");
        	}
        }
 
        return null;
    }
    
    private String findEnWikiTitle(String wikidataJsonStr, String wikidataArtistId) throws JSONException {
        JSONObject wikidataJson = new JSONObject(wikidataJsonStr);
        JSONObject wikidataEntities = wikidataJson.getJSONObject("entities");
        JSONObject wikidataEntity = wikidataEntities.getJSONObject(wikidataArtistId);
        JSONObject wikidataSiteLinks = wikidataEntity.getJSONObject("sitelinks");
        JSONObject enWiki = wikidataSiteLinks.getJSONObject("enwiki");
        
        return enWiki.getString("title");
    }
}
