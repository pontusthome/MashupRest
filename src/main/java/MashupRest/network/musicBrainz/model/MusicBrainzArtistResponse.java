package MashupRest.network.musicBrainz.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MusicBrainzArtistResponse {
	
	private String id;
	private String name;
	private String country;
	private List<MusicBrainzRelation> relations;
	@SerializedName("release-groups") private List<MusicBrainzArtistReleaseGroup> releaseGroups;
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCountry() {
		return country;
	}
	
	public List<MusicBrainzRelation> getRelations() {
		return relations;
	}
	
	public MusicBrainzRelation findRealtion(String relationType) {
        for (MusicBrainzRelation relation: relations) {
        	if (relation.getType().equals(relationType)) {
        		 return relation;
        	}
        }
 
        return null;
	}
	
	public List<MusicBrainzArtistReleaseGroup> getAlbums() {
		List<MusicBrainzArtistReleaseGroup> albums = new ArrayList<>();
		for (MusicBrainzArtistReleaseGroup releaseGroup: releaseGroups) {
			if (releaseGroup.isAlbum()) {
				albums.add(releaseGroup);
			}
		}
		
		return albums;
	}
}
