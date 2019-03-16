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
import MashupRest.network.wikidata.model.WikidataEntity;
import MashupRest.network.wikidata.model.WikidataResponse;
import MashupRest.network.wikipedia.WikipediaService;
import MashupRest.network.wikipedia.model.WikipediaResponse;

@RestController
public class MashupController {
	
    static final String WIKIDATA_RELATION_TYPE = "wikidata";
    static final String WIKIDATA_RELATION_BASE_URL = "https://www.wikidata.org/wiki/";
    static final String WIKIPEDIA_EN_SITELINK = "enwiki";

    @Autowired
    private MusicBrainzService musicBrainzService;
	
    @Autowired
    private WikidataService wikidataService;

    @Autowired
    private WikipediaService wikipediaService;

    @GetMapping("/artist/{artist}")
    public WikipediaResponse getArtist(@PathVariable("artist") String artistMBID) throws IOException {
    	
    	MusicBrainzArtistResponse musicBrainzArtist = musicBrainzService.getArtist(artistMBID);
        
        String wikidataArtistId = findWikidataArtistId(musicBrainzArtist.getRelations());
        if (wikidataArtistId == null) {
	        throw new IOException("Cannot find Wikidata artist Id from Music Brainz response");
        }
        
        WikidataResponse wikidataResponse = wikidataService.getArtist(wikidataArtistId);
        
		if (!wikidataResponse.getEntities().containsKey(wikidataArtistId)) {
	        throw new IOException("Cannot find Wikidata artist Id in Music Brainz response");
		}
        WikidataEntity wikidataEntity = wikidataResponse.getEntities().get(wikidataArtistId);
         
		if (!wikidataEntity.getSitelinks().containsKey(WIKIPEDIA_EN_SITELINK)) {
	        throw new IOException("Cannot find EN Wikipedia sitelink in Music Brainz response");
		}
		String enWikiTitle = wikidataEntity.getSitelinks().get(WIKIPEDIA_EN_SITELINK)
				.getTitle();
        
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
}
