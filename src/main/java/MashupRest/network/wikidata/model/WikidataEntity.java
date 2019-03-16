package MashupRest.network.wikidata.model;

import java.util.Map;

public class WikidataEntity {
	
	private String type;
	private String id;
	private Map<String, WikidataSiteLink> sitelinks;
	
	public String getType() {
		return type;
	}
	
	public String getId() {
		return id;
	}
	
	public Map<String, WikidataSiteLink> getSitelinks() {
		return sitelinks;
	}
}
