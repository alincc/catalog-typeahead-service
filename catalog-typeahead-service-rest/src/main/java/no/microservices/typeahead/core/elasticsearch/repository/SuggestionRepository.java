package no.microservices.typeahead.core.elasticsearch.repository;

import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionResponse;
import no.microservices.typeahead.model.field.SuggestionFieldResponse;

import java.util.List;

/**
 * Created by andreasb on 19.10.15.
 */
public interface SuggestionRepository {
    void addSuggestion(SuggestionQuery suggestionQuery) throws Exception;
    List<SuggestionResponse> getSuggestions(SuggestionRequest suggestionRequest);

    List<SuggestionFieldResponse> getSuggestionsField(SuggestionRequest suggestionRequest, String field);
}
