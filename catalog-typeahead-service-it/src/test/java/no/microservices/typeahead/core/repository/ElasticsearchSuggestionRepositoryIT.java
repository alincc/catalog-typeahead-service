package no.microservices.typeahead.core.repository;

import no.microservices.typeahead.Application;
import no.microservices.typeahead.core.elasticsearch.repository.SuggestionRepository;
import no.microservices.typeahead.it.ElasticSearchTestConfig;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, ElasticSearchTestConfig.class})
@WebIntegrationTest("server.port: 0")
public class ElasticsearchSuggestionRepositoryIT {

    @Autowired
    private SuggestionRepository suggestionRepository;
    private SuggestionRequest suggestionRequest;

    @Before
    public void setUp() throws Exception {
        suggestionRequest = new SuggestionRequest();
        suggestionRequest.setQ("");
    }

    @Test
    public void whenAskingForPageOfSuggestionsThenGetTotalHits() throws Exception {
        suggestionRequest.setQ("Kn");
        Page<SuggestionResponse> suggestions = suggestionRepository.getSuggestions(suggestionRequest, new PageRequest(0, 10));
        assertThat(suggestions.getTotalElements(), is(1L));
    }

    @Test
    public void whenAskingForPageOfSuggestionsForFieldThenGetTotalHits() throws Exception {
        Page<SuggestionResponse> suggestions = suggestionRepository.getSuggestionsField(suggestionRequest, "namecreators", new PageRequest(0, 1));
        assertThat(suggestions.getTotalElements(), is(3L));
    }

    @Test
    public void whenAskingForSuggestionsForFieldThenReturnRequestedNumberOfSuggestions() throws Exception {
        Page<SuggestionResponse> suggestions = suggestionRepository.getSuggestionsField(suggestionRequest, "namecreators", new PageRequest(0, 2));
        assertThat(suggestions.getContent(), hasSize(2));
    }

    @Test
    public void whenAskingForSuggestionsForFieldThenItShouldIgnorePageProperty() throws Exception {
        Page<SuggestionResponse> suggestions = suggestionRepository.getSuggestionsField(suggestionRequest, "namecreators", new PageRequest(20, 2));
        assertThat(suggestions.getContent(), hasSize(2));
    }
}
