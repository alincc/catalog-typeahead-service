package no.microservices.typeahead.core.elasticsearch.service;

import no.microservices.typeahead.model.SuggestionRoot;
import no.microservices.typeahead.model.field.SuggestionFieldRoot;

/**
 * Created by andreasb on 15.10.15.
 */
public interface ISuggestionService {
    SuggestionRoot getSuggestions(String query, String mediaType, int size);

    SuggestionFieldRoot getSuggestionsField(String query, String field, String mediaType, int size);

    void saveSuggestion(String query, String mediaType);
}
