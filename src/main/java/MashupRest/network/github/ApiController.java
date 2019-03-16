package MashupRest.network.github;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
	
    @Autowired
    private ApiService apiService;

    @GetMapping("/repos")
    public List<Repository> getRepos() throws IOException {
        return apiService.getRepositories();
    }

    @PostMapping("/repos")
    public Repository createRepo(@RequestBody Repository newRepo) throws IOException {
        return apiService.createRepository(newRepo);
    }
}
