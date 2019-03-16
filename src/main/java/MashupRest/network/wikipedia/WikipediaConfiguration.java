package MashupRest.network.wikipedia;

public interface WikipediaConfiguration {
	public final static String WIKIPEDIA_BASE_URL = "https://en.wikipedia.org/w/";
	public final static String WIKIPEDIA_ACTION = "query";
	public final static String WIKIPEDIA_RESPONSE_FORMAT = "json";
	public final static String WIKIPEDIA_PROP = "extracts";
	public final static boolean WIKIPEDIA_EXINTRO = true;
	public final static boolean WIKIPEDIA_REDIRECTS = true;
}
