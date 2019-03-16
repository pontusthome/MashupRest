package MashupRest.network.musicBrainz.model;

import java.util.List;

public class MusicBrainzArtistResponse {
	
	private String id;
	private String name;
	private String country;
	private List<MusicBrainzRelation> relations;
	
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
}
