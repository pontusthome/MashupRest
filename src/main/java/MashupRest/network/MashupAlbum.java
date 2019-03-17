package MashupRest.network;

public class MashupAlbum {
	
	public final String title;
	public final String id;
	private String image;
	
	public MashupAlbum(String title, String id) {
		this.title = title;
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}	
}

//"title" : "Nevermind",
//"id": "1b022e01-4da6-387b-8658-8678046e4cef",
//"image":
//
//"http://coverartarchive.org/release/a146429a-cedc-3ab0-9e41-1aaf5f6cdc2d/30124
//95605.jpg"