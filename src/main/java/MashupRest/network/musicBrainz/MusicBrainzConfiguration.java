package MashupRest.network.musicBrainz;

public interface MusicBrainzConfiguration {
    static final String MUSIC_BRAINZ_BASE_URL = "https://musicbrainz.org/ws/2/";
    static final String RESPONSE_FORMAT = "json";
    static final String ARTIST_INCLUDE = "url-rels+release-groups";
}
