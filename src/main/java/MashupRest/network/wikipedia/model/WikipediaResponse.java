package MashupRest.network.wikipedia.model;

import java.util.Map;
import java.util.Map.Entry;

public class WikipediaResponse {

	private Query query;
	
	public Map<String, WikipediaPage> getPageMap() {
		return query.pages;
	}
	
	class Query {
	    Map<String, WikipediaPage> pages;
	}

	public WikipediaPage getFirstPage() {
	    for (Entry<String, WikipediaPage> entry : getPageMap().entrySet()) {
	        return entry.getValue();
	    }
	    
		return null;
	}
}
