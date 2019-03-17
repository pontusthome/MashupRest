package MashupRest.network;

import java.util.List;

public class MashupResponse {
	
	public final String mbid;
	private String description;
	private List<MashupAlbum> albums;
	
	public MashupResponse(String mbid) {
		this.mbid = mbid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<MashupAlbum> getAlbums() {
		return albums;
	}

	public void setAlbums(List<MashupAlbum> albums) {
		this.albums = albums;
	}
}

//{
//"mbid" : "5b11f4ce-a62d-471e-81fc-a69a8278c7da",
//"description" : "<p><b>Nirvana</b> was an American rock band that was
//
//formed ... osv osv ... ",
//"albums" : [
//{
//"title" : "Nevermind",
//"id": "1b022e01-4da6-387b-8658-8678046e4cef",
//"image":
//
//"http://coverartarchive.org/release/a146429a-cedc-3ab0-9e41-1aaf5f6cdc2d/30124
//95605.jpg"
//},
//{
//... more albums...
//}
//]
//}