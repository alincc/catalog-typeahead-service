package no.microservices.typeahead.core.elasticsearch.service;

import no.microservices.typeahead.core.elasticsearch.exception.SuggestionException;
import no.microservices.typeahead.core.elasticsearch.repository.SuggestionRepository;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
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
    private final SuggestionRepository suggestionRepository;

    @Autowired
    public SuggestionService(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    @Override
    public SuggestionRoot getSuggestions(SuggestionRequest suggestionRequest) {
        List<SuggestionResponse> suggestionElements = suggestionRepository.getSuggestions(suggestionRequest);
        return new SuggestionRoot(suggestionElements);
    }

    @Override
    public SuggestionFieldRoot getSuggestionsField(SuggestionRequest suggestionRequest, String field) {
        List<SuggestionFieldResponse> suggestionElements = suggestionRepository.getSuggestionsField(suggestionRequest, field);
        return new SuggestionFieldRoot(suggestionElements);
    }

    @Override
    public void saveSuggestion(SuggestionQuery suggestionQuery) {
        try {
            suggestionRepository.addSuggestion(suggestionQuery);
        } catch (Exception e) {
            throw new SuggestionException("Failed to save suggestion '" + suggestionQuery.getSentence() + "' with mediatype '" + suggestionQuery.getMediatype() + "'", e);
        }
    }
}
