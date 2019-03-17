package MashupRest.network.coverArtArchive.model;

import java.util.List;

public class CoverArtArchiveResponse {
	
	private List<CoverArtArchiveImage> images;
	private String release;
	
	public List<CoverArtArchiveImage> getImages() {
		return images;
	}
	
	public String getRelease() {
		return release;
	}
}
