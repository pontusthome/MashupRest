package MashupRest.network.musicBrainz.model;

import com.google.gson.annotations.SerializedName;

public class MusicBrainzArtistReleaseGroup {

	public final static String MUSIC_BRAINZ_ARTIST_RELEASE_GROUP_ALBUM_TYPE = "Album";
	
	private String title;
	private String id;
	@SerializedName("primary-type") private String primaryType;

	public String getTitle() {
		return title;
	}
	
	public String getId() {
		return id;
	}
	
	public String getPrimaryType() {
		return primaryType;
	}
	
	public boolean isAlbum() {
		return primaryType.equals(MUSIC_BRAINZ_ARTIST_RELEASE_GROUP_ALBUM_TYPE);
	}
}
