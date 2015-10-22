package no.microservices.typeahead.core.elasticsearch.repository;

import no.microservices.typeahead.model.SuggestionResponse;

import java.util.List;

/**
 * Created by andreasb on 19.10.15.
 */
public interface SuggestionRepository {
    void addSuggestion(String sentence, String mediaType) throws Exception;
    List<SuggestionResponse> getSuggestions(String query, String mediaType, int size);
}
