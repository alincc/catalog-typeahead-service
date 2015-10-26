package no.microservices.typeahead.core.elasticsearch.repository;

import no.microservices.typeahead.model.SuggestionResponse;
import no.microservices.typeahead.model.field.SuggestionFieldResponse;

import java.util.List;

/**
 * Created by andreasb on 19.10.15.
 */
public interface SuggestionRepository {
    void addSuggestion(String sentence, String mediaType) throws Exception;
    List<SuggestionResponse> getSuggestions(String query, String mediaType, int size);

    List<SuggestionFieldResponse> getSuggestionsField(String query, String field, String mediaType, int size);
}
