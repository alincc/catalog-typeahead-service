package no.microservices.typeahead.core.elasticsearch.service;

import no.microservices.typeahead.core.elasticsearch.exception.SuggestionException;
import no.microservices.typeahead.core.elasticsearch.repository.SuggestionRepository;
import no.microservices.typeahead.model.SuggestionResponse;
import no.microservices.typeahead.model.SuggestionRoot;
import no.microservices.typeahead.model.field.SuggestionFieldResponse;
import no.microservices.typeahead.model.field.SuggestionFieldRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by andreasb on 15.10.15.
 */
@Service
public class SuggestionService implements ISuggestionService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Override
    public SuggestionRoot getSuggestions(String query, String mediaType, int size) {
        List<SuggestionResponse> suggestionElements = suggestionRepository.getSuggestions(query, mediaType, size);
        return new SuggestionRoot(suggestionElements);
    }

    @Override
    public SuggestionFieldRoot getSuggestionsField(String query, String field, String mediaType, int size) {
        List<SuggestionFieldResponse> suggestionElements = suggestionRepository.getSuggestionsField(query, field, mediaType, size);
        return new SuggestionFieldRoot(suggestionElements);
    }

    @Override
    public void saveSuggestion(String query, String mediaType) {
        try {
            suggestionRepository.addSuggestion(query, mediaType);
        } catch (Exception e) {
            throw new SuggestionException("Failed to save suggestion '" + query + "' with mediaType '" + mediaType + "'", e);
        }
    }
}
