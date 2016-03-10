package no.microservices.typeahead.core.elasticsearch.service;

import no.microservices.typeahead.core.elasticsearch.exception.SuggestionException;
import no.microservices.typeahead.core.elasticsearch.repository.SuggestionRepository;
import no.microservices.typeahead.core.model.SuggestionRoot;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        Pageable pageRequest = new PageRequest(0, 10);
        SuggestionResponse suggestionResponse = new SuggestionResponse("Knut Hamsun", "Knut Hamsun", 12);
        when(suggestionRepository.getSuggestions(suggestionRequest, new PageRequest(0, 10))).thenReturn(new PageImpl<>(Arrays.asList(suggestionResponse)));

        SuggestionRoot suggestions = suggestionService.getSuggestions(suggestionRequest, pageRequest);

        assertThat(suggestions.getPage().getContent().get(0).getLabel(), is("Knut Hamsun"));

        verify(suggestionRepository).getSuggestions(suggestionRequest, pageRequest);
        verifyNoMoreInteractions(suggestionRepository);
    }

    @Test
    public void suggestionField() {
        SuggestionRequest suggestionRequest = new SuggestionRequest();
        suggestionRequest.setQ("Knut");
        String field = "namecreator";
        Pageable pageRequest = new PageRequest(0, 10);
        SuggestionResponse suggestionFieldResponse = new SuggestionResponse("Knut Hamsun","Knut Hamsun",0);

        when(suggestionRepository.getSuggestionsField(suggestionRequest, field, pageRequest)).thenReturn(new PageImpl<>(Arrays.asList(suggestionFieldResponse)));

        SuggestionRoot suggestionFieldRoot = suggestionService.getSuggestionsField(suggestionRequest, field, pageRequest);

        assertThat("Should return Knut Hamsun", suggestionFieldRoot.getPage().getContent().get(0).getLabel(), is("Knut Hamsun"));

        verify(suggestionRepository).getSuggestionsField(suggestionRequest, field, pageRequest);
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
