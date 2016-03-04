package no.microservices.typeahead.rest.controller;

import no.microservices.typeahead.core.elasticsearch.service.ISuggestionService;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionResponse;
import no.microservices.typeahead.model.SuggestionRoot;
import no.microservices.typeahead.model.field.SuggestionFieldResponse;
import no.microservices.typeahead.model.field.SuggestionFieldRoot;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SuggestionControllerTest {
    @Mock
    private ISuggestionService suggestionService;
    private SuggestionController suggestionController;

    @Before
    public void setup() {
        suggestionController = new SuggestionController(suggestionService);
    }

    @Test
    public void suggestions() {
        SuggestionRequest suggestionRequest = new SuggestionRequest();
        suggestionRequest.setQ("Ra");

        SuggestionResponse suggestionResponse = new SuggestionResponse("Ramona Kakestein","Ramona Kakestein",2);
        SuggestionRoot suggestionRoot = new SuggestionRoot(Arrays.asList(suggestionResponse));

        when(suggestionService.getSuggestions(suggestionRequest)).thenReturn(suggestionRoot);

        ResponseEntity<SuggestionRoot> entity = suggestionController.getSuggestion(suggestionRequest);
        assertThat("Should return Ramona Kakestein", entity.getBody().getItems().get(0).getLabel(), is("Ramona Kakestein"));

        verify(suggestionService).getSuggestions(suggestionRequest);
        verifyNoMoreInteractions(suggestionService);
    }

    @Test
    public void suggestionField() {
        SuggestionRequest suggestionRequest = new SuggestionRequest();
        suggestionRequest.setQ("Alf");
        String field = "namecreator";
        SuggestionFieldResponse suggestionFieldResponse = new SuggestionFieldResponse("Alfred Bræle");
        SuggestionFieldRoot suggestionFieldRoot = new SuggestionFieldRoot(Arrays.asList(suggestionFieldResponse));

        when(suggestionService.getSuggestionsField(suggestionRequest, field)).thenReturn(suggestionFieldRoot);

        ResponseEntity<SuggestionFieldRoot> entity = suggestionController.getSuggestionField(field, suggestionRequest);

        assertThat("Alfred Bræle should be returned", entity.getBody().getItems().get(0).getSentence(), is("Alfred Bræle"));

        verify(suggestionService).getSuggestionsField(suggestionRequest, field);
        verifyNoMoreInteractions(suggestionService);
    }

    @Test
    public void saveSuggestion() {
        SuggestionQuery suggestionQuery = new SuggestionQuery("The cake is a lie", "ALL");
        suggestionController.saveSuggestion(suggestionQuery);

        verify(suggestionService).saveSuggestion(suggestionQuery);
        verifyNoMoreInteractions(suggestionService);
    }
}
