package no.microservices.typeahead.core.elasticsearch.service;

import no.microservices.typeahead.core.elasticsearch.exception.SuggestionException;
import no.microservices.typeahead.core.elasticsearch.repository.SuggestionRepository;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionResponse;
import no.microservices.typeahead.model.SuggestionRoot;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SuggestionServiceTest {
    @Mock
    private SuggestionRepository suggestionRepository;
    private ISuggestionService suggestionService;

    @Before
    public void setup() {
        suggestionService = new SuggestionService(suggestionRepository);
    }

    @Test
    public void suggestions() {
        SuggestionRequest suggestionRequest = new SuggestionRequest();
        suggestionRequest.setQ("Knut");

        SuggestionResponse suggestionResponse = new SuggestionResponse("Knut Hamsun", "Knut Hamsun", 12);
        when(suggestionRepository.getSuggestions(suggestionRequest)).thenReturn(Arrays.asList(suggestionResponse));

        SuggestionRoot suggestions = suggestionService.getSuggestions(suggestionRequest);

        verify(suggestionRepository).getSuggestions(suggestionRequest);
        verifyNoMoreInteractions(suggestionRepository);
    }

    @Test
    public void suggestionField() {
        SuggestionRequest suggestionRequest = new SuggestionRequest();
        suggestionRequest.setQ("Knut");
        String field = "namecreator";
        SuggestionResponse suggestionFieldResponse = new SuggestionResponse("Knut Hamsun","Knut Hamsun",0);

        when(suggestionRepository.getSuggestionsField(suggestionRequest, field)).thenReturn(Arrays.asList(suggestionFieldResponse));

        SuggestionRoot suggestionFieldRoot = suggestionService.getSuggestionsField(suggestionRequest, field);

        assertThat("Should return Knut Hamsun", suggestionFieldRoot.getItems().get(0).getLabel(), is("Knut Hamsun"));

        verify(suggestionRepository).getSuggestionsField(suggestionRequest, field);
        verifyNoMoreInteractions(suggestionRepository);
    }

    @Test
    public void saveSuggestion() throws Exception {
        SuggestionQuery suggestionQuery = new SuggestionQuery("Hei hå","ALL");

        suggestionService.saveSuggestion(suggestionQuery);

        verify(suggestionRepository).addSuggestion(suggestionQuery);
        verifyNoMoreInteractions(suggestionRepository);
    }

    @Test(expected = SuggestionException.class)
    public void whenSaveSuggestionFailsExceptionShouldBeThrown() throws Exception {
        SuggestionQuery suggestionQuery = new SuggestionQuery();
        doThrow(new Exception()).when(suggestionRepository).addSuggestion(suggestionQuery);

        suggestionService.saveSuggestion(suggestionQuery);
    }
}
