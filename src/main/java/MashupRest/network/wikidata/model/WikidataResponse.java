package MashupRest.network.wikidata.model;

import java.util.Map;

public class WikidataResponse {
	
	private Map<String, WikidataEntity> entities;

	public Map<String, WikidataEntity> getEntities() {
		return entities;
	}

	public String getSiteLinkTitle(String wikidataArtistId, String siteLinkTitle) {
		WikidataEntity wikidataEntity = getEntity(wikidataArtistId);
		if (wikidataEntity != null) {
			return wikidataEntity.getSiteLink(siteLinkTitle);
		}	
		
		return null;
	}
	
	public WikidataEntity getEntity(String wikidataArtistId) {
		if (entities.containsKey(wikidataArtistId)) {	
			return entities.get(wikidataArtistId);
		}
		
		return null;
	}
}
