package no.microservices.typeahead.core.elasticsearch.service;

import no.microservices.typeahead.core.elasticsearch.exception.SuggestionException;
import no.microservices.typeahead.core.elasticsearch.repository.SuggestionRepository;
import no.microservices.typeahead.core.model.SuggestionRoot;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SuggestionService implements ISuggestionService {
    private final SuggestionRepository suggestionRepository;

    @Autowired
    public SuggestionService(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    @Override
    public SuggestionRoot getSuggestions(SuggestionRequest suggestionRequest, Pageable pageable) {
        Page<SuggestionResponse> suggestionElements = suggestionRepository.getSuggestions(suggestionRequest, pageable);
        return new SuggestionRoot(suggestionElements);
    }

    @Override
    public SuggestionRoot getSuggestionsField(SuggestionRequest suggestionRequest, String field, Pageable pageable) {
        Page<SuggestionResponse> suggestionElements = suggestionRepository.getSuggestionsField(suggestionRequest, field, pageable);
        return new SuggestionRoot(suggestionElements);
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
