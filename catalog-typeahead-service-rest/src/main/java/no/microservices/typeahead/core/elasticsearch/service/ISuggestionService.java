package no.microservices.typeahead.core.elasticsearch.service;

import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionRoot;
import no.microservices.typeahead.model.field.SuggestionFieldRoot;

/**
 * Created by andreasb on 15.10.15.
 */
public interface ISuggestionService {
    SuggestionRoot getSuggestions(SuggestionRequest suggestionRequest);

    SuggestionFieldRoot getSuggestionsField(SuggestionRequest suggestionRequest, String field);

    void saveSuggestion(SuggestionQuery suggestionQuery);
}
