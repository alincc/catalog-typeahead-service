package no.microservices.typeahead.core.elasticsearch.repository;

import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by andreasb on 19.10.15.
 */
public interface SuggestionRepository {
    void addSuggestion(SuggestionQuery suggestionQuery) throws Exception;
    Page<SuggestionResponse> getSuggestions(SuggestionRequest suggestionRequest, Pageable pageable);

    Page<SuggestionResponse> getSuggestionsField(SuggestionRequest suggestionRequest, String field, Pageable pageable);
}
