package MashupRest.network;

import static MashupRest.network.AsynchConfiguration.ASYNC_EXECUTOR_BEAN_NAME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import MashupRest.network.coverArtArchive.CoverArtArchiveService;
import MashupRest.network.coverArtArchive.model.CoverArtArchiveResponse;
import MashupRest.network.error.ArtistNotFoundException;
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
    @ExceptionHandler({ArtistNotFoundException.class})
    public MashupResponse getArtist(@PathVariable("artist") String artistMBID) {
     	
    	MashupResponse mashupResponse = new MashupResponse(artistMBID);
    	
    	MusicBrainzArtistResponse musicBrainzArtist;
		try {
			musicBrainzArtist = musicBrainzService.getArtist(artistMBID);
		} catch (IOException e1) {
			throw new ArtistNotFoundException("Artist not found");
		}
    	
		String wikidataArtistId = findWikidataArtistId(musicBrainzArtist);

    	CompletableFuture<String> description = fetchArtistDescription(wikidataArtistId);
		CompletableFuture<List<MashupAlbum>> albums = fetchArtistAlbums(musicBrainzArtist);

    	CompletableFuture.allOf(description, albums).join();

		try {
			mashupResponse.setDescription(description.get());
			mashupResponse.setAlbums(albums.get());
		} catch (InterruptedException | ExecutionException e) {
			System.out.println("Could not get result from description or album future");
		}         
         
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
    
    // ======== Get Artist description from Wikidata and Wikipedia ===============
	@Async(ASYNC_EXECUTOR_BEAN_NAME)
	private CompletableFuture<String> fetchArtistDescription(String wikidataArtistId) {
		if (wikidataArtistId == null) {
			return CompletableFuture.completedFuture(null);
		}
		
		return wikidataService.getArtist(wikidataArtistId)
		.thenCompose(wikidataResponse -> fetchWikipediaPage(wikidataResponse, wikidataArtistId))
		.thenCompose(wikipediaPage -> extractDescription(wikipediaPage));
	}

	@Async(ASYNC_EXECUTOR_BEAN_NAME)
	private CompletableFuture<WikipediaPage> fetchWikipediaPage(WikidataResponse wikidataResponse, String wikidataArtistId) {	
		String enWikiTitle = wikidataResponse != null ? wikidataResponse.getSiteLinkTitle(wikidataArtistId, WIKIPEDIA_EN_SITELINK) : null;        
		
		if (enWikiTitle == null) {
			return CompletableFuture.completedFuture(null);
		}

		return wikipediaService.getWikiTitle(enWikiTitle)
		.thenCompose(wikipediaResponse -> fetchWikipediaPage(wikipediaResponse, enWikiTitle));
	}

	@Async(ASYNC_EXECUTOR_BEAN_NAME)
	private CompletableFuture<WikipediaPage> fetchWikipediaPage(WikipediaResponse wikipediaResponse, String enWikiTitle) {
       if (wikipediaResponse == null) {
			return CompletableFuture.completedFuture(null);
       }
       
       return CompletableFuture.completedFuture(wikipediaResponse.getFirstPage());      
	}

	@Async(ASYNC_EXECUTOR_BEAN_NAME)
	private CompletableFuture<String> extractDescription(WikipediaPage wikipediaPage) {
		if (wikipediaPage == null) {
			return CompletableFuture.completedFuture(null);
		}
			
		return CompletableFuture.completedFuture(wikipediaPage.getExtract());
	}


	// ======== Get Artist albums from Music Brainz response and cover art from Cover Art Archive ===============
	@Async(ASYNC_EXECUTOR_BEAN_NAME)
	private CompletableFuture<List<MashupAlbum>> fetchArtistAlbums(MusicBrainzArtistResponse musicBrainzArtist) {
       List<MusicBrainzArtistReleaseGroup> albums = musicBrainzArtist.getAlbums();
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
