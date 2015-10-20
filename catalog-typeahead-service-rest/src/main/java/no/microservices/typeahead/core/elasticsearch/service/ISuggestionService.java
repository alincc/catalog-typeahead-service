package no.microservices.typeahead.core.elasticsearch.service;

import no.microservices.typeahead.model.SuggestionRoot;

/**
 * Created by andreasb on 15.10.15.
 */
public interface ISuggestionService {
    SuggestionRoot getSuggestions(String query, String mediaType, int size);

    void saveSuggestion(String query, String mediaType);
}
