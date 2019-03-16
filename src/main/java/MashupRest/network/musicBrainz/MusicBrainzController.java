package MashupRest.network.musicBrainz;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MusicBrainzController {
	
    @Autowired
    private MusicBrainzService apiService;

    @GetMapping("/artist/{artist}")
    public MusicBrainzArtistResponse getArtist(@PathVariable("artist") String artistMBID) throws IOException {
        return apiService.getArtist(artistMBID);
    }
}
