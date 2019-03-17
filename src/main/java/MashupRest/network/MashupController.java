package MashupRest.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import MashupRest.network.coverArtArchive.CoverArtArchiveService;
import MashupRest.network.coverArtArchive.model.CoverArtArchiveResponse;
import MashupRest.network.musicBrainz.MusicBrainzService;
import MashupRest.network.musicBrainz.model.MusicBrainzArtistReleaseGroup;
import MashupRest.network.musicBrainz.model.MusicBrainzArtistResponse;
import MashupRest.network.musicBrainz.model.MusicBrainzRelation;
import MashupRest.network.wikidata.WikidataService;
import MashupRest.network.wikidata.model.WikidataEntity;
import MashupRest.network.wikidata.model.WikidataResponse;
import MashupRest.network.wikipedia.WikipediaService;
import MashupRest.network.wikipedia.model.WikipediaPage;
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
    
    @Autowired
    private CoverArtArchiveService coverArtService;

    @GetMapping("/artist/{artist}")
    public MashupResponse getArtist(@PathVariable("artist") String artistMBID) throws IOException {
     	
    	MashupResponse mashupResponse = new MashupResponse(artistMBID);
    	
    	MusicBrainzArtistResponse musicBrainzArtist = musicBrainzService.getArtist(artistMBID);
    	
    	String wikidataArtistId = findWikidataArtistId(musicBrainzArtist);
 
        WikidataResponse wikidataResponse = fetchWikidata(wikidataArtistId);
        String enWikiTitle = wikidataResponse != null ? wikidataResponse.getSiteLinkTitle(wikidataArtistId, WIKIPEDIA_EN_SITELINK) : null;        
        
        WikipediaPage wikipediaPage = fetchWikipediaPage(enWikiTitle);
        if (wikipediaPage != null) {
        	mashupResponse.setDescription(wikipediaPage.getExtract());
        }
        
        List<MusicBrainzArtistReleaseGroup> albums = musicBrainzArtist.getAlbums();
        addAlbumsToResponse(mashupResponse, albums);
        
        fetchAndAddCoverArtToAlbums(mashupResponse.getAlbums());
        
        return mashupResponse;
    }

	private String findWikidataArtistId(MusicBrainzArtistResponse musicBrainzArtist) {
       	MusicBrainzRelation musicBrainzRelation = musicBrainzArtist.findRealtion(WIKIDATA_RELATION_TYPE);
       	if (musicBrainzRelation != null) {
    		String url = musicBrainzRelation.getUrl().getResource();
    		return url.replace(WIKIDATA_RELATION_BASE_URL, "");
       	}
 
        return null;
    }

    private WikidataResponse fetchWikidata(String wikidataArtistId) {
        if (wikidataArtistId != null) {
        	try {
			    return wikidataService.getArtist(wikidataArtistId);			    
        	} catch (IOException e) {
        		System.out.println("Unable to get Wikidata");
        	}
        }
        else {
    		System.out.println("Found no wikidata Artist Id");
        }
        
        return null;
	}

	private WikipediaPage fetchWikipediaPage(String enWikiTitle) {
        if (enWikiTitle != null) {
        	try {
				WikipediaResponse wikipediaResponse = wikipediaService.getWikiTitle(enWikiTitle);
				return wikipediaResponse.getFirstPage();
        	} catch (IOException e) {
        		System.out.println("Unable to get Wikipedia");
        	} 
        }
        else {
    		System.out.println("Found no Wikipedia title");
        }

  		return null;
    }

	private void addAlbumsToResponse(MashupResponse mashupResponse, List<MusicBrainzArtistReleaseGroup> albums) {
		List<MashupAlbum> mashupAlbums = new ArrayList<>();
		for (MusicBrainzArtistReleaseGroup album: albums) {
			mashupAlbums.add(new MashupAlbum(album.getTitle(), album.getId()));
		}
		
        mashupResponse.setAlbums(mashupAlbums);
	}

	private void fetchAndAddCoverArtToAlbums(List<MashupAlbum> albums) {
		List<CoverArtArchiveResponse> coverArtResponses = new ArrayList<>();
		for (MashupAlbum album: albums) {
	    	try {
	    		CoverArtArchiveResponse coverArt = coverArtService.getCoverArt(album.id);	
	    		if (coverArt.getImages() != null && !coverArt.getImages().isEmpty()) {
	    			album.setImage(coverArt.getImages().get(0).getImage());
	    		}
	    	} catch (IOException e) {
	    		System.out.println("Unable to get a Cover Art for albumId: " + album.id);
	    	}   
		}
	}
}
