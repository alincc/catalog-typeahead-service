package no.microservices.typeahead.model;

import java.util.List;

/**
 * Created by andreasb on 15.10.15.
 */
public class SuggestionRoot {
    private List<SuggestionResponse> items;

    public SuggestionRoot() {
    }

    public SuggestionRoot(List<SuggestionResponse> suggestionElements) {
        this.items = suggestionElements;
    }

    public List<SuggestionResponse> getSuggestionElements() {
        return items;
    }

    public void setSuggestionElements(List<SuggestionResponse> suggestionElements) {
        this.items = suggestionElements;
    }
}
