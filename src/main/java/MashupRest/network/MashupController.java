package MashupRest.network;

import static MashupRest.network.AsynchConfiguration.ASYNC_EXECUTOR_BEAN_NAME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
    	
    	CompletableFuture<String> description = fetchArtistDescription(musicBrainzArtist);
		System.out.println("Sent requests for description");
		CompletableFuture<List<MashupAlbum>> albums = fetchArtistAlbums(musicBrainzArtist);
		System.out.println("Sent requests for albums");

    	CompletableFuture.allOf(description, albums).join();

		try {
			mashupResponse.setDescription(description.get());
			mashupResponse.setAlbums(albums.get());
		} catch (InterruptedException | ExecutionException e) {
			System.out.println("Could not get result from description or album future");
		}         
         
        return mashupResponse;
    }

    // ======== Get Artist description from Wikidata and Wikipedia ===============
	@Async(ASYNC_EXECUTOR_BEAN_NAME)
	private CompletableFuture<String> fetchArtistDescription(MusicBrainzArtistResponse musicBrainzArtist) {
		String wikidataArtistId = findWikidataArtistId(musicBrainzArtist);
		
		WikidataResponse wikidataResponse = fetchWikidata(wikidataArtistId);
		String enWikiTitle = wikidataResponse != null ? wikidataResponse.getSiteLinkTitle(wikidataArtistId, WIKIPEDIA_EN_SITELINK) : null;        
		  
		WikipediaPage wikipediaPage = fetchWikipediaPage(enWikiTitle);
		if (wikipediaPage != null) {
			return CompletableFuture.completedFuture(wikipediaPage.getExtract());
		}
		
		return CompletableFuture.completedFuture(null);
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
				System.out.println("Got response from Wikidata");
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
				System.out.println("Got response from Wikipedia");
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

    // ======== Get Artist albums from Music Brainz response and cover art from Cover Art Archive ===============
	@Async(ASYNC_EXECUTOR_BEAN_NAME)
	private CompletableFuture<List<MashupAlbum>> fetchArtistAlbums(MusicBrainzArtistResponse musicBrainzArtist) {
       List<MusicBrainzArtistReleaseGroup> albums = musicBrainzArtist.getAlbums();
       albums = new ArrayList<>();
       return fetchAndAddCoverArtToAlbums(albums);
	}

	@Async(ASYNC_EXECUTOR_BEAN_NAME)
	private CompletableFuture<List<MashupAlbum>> fetchAndAddCoverArtToAlbums(List<MusicBrainzArtistReleaseGroup> albums) {
		List<MashupAlbum> mashupAlbums = new ArrayList<>();
		
		CompletableFuture<?>[] completableFutures = new CompletableFuture[albums.size()];
		
		for (int index = 0; index < albums.size(); index++) {
			MusicBrainzArtistReleaseGroup album = albums.get(index);
			completableFutures[index] = coverArtService.getCoverArt(album.getId());
			
			mashupAlbums.add(new MashupAlbum(album.getTitle(), album.getId()));
		}

		CompletableFuture.allOf(completableFutures).join();
		
		System.out.println("Got all albums");
		for (int index = 0; index < completableFutures.length; index++) {
			try {
				CoverArtArchiveResponse coverArt = (CoverArtArchiveResponse) completableFutures[index].get();
				MashupAlbum mashupAlbum = mashupAlbums.get(index);
				
				if (coverArt != null && coverArt.getImages() != null && !coverArt.getImages().isEmpty()) {
					mashupAlbum.setImage(coverArt.getImages().get(0).getImage());
				}
			} catch (Exception e) {
				System.out.println("Unable to cast completable future result.");
			}
		}
		
		return CompletableFuture.completedFuture(mashupAlbums);
	}
}
