package MashupRest.network.wikipedia.model;

public class WikipediaPage {
    private int pageid;
    private int ns;
    private String title;
    private String extract;
    
	public int getPageid() {
		return pageid;
	}
	
	public int getNs() {
		return ns;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getExtract() {
		return extract;
	}    
}
