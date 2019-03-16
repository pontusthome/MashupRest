package MashupRest.network.wikipedia.model;

import java.util.Map;

public class WikipediaResponse {

	private Query query;
	
	public Map<String, WikipediaPage> getPageMap() {
		return query.pages;
	}
	
	class Query {
	    Map<String, WikipediaPage> pages;
	}
}
