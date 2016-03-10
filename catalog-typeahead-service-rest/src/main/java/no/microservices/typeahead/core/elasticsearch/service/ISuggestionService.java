package no.microservices.typeahead.core.elasticsearch.service;

import no.microservices.typeahead.core.model.SuggestionRoot;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import org.springframework.data.domain.Pageable;

/**
 * Created by andreasb on 15.10.15.
 */
public interface ISuggestionService {
    SuggestionRoot getSuggestions(SuggestionRequest suggestionRequest, Pageable pageable);

    SuggestionRoot getSuggestionsField(SuggestionRequest suggestionRequest, String field, Pageable pageable);

    void saveSuggestion(SuggestionQuery suggestionQuery);
}
