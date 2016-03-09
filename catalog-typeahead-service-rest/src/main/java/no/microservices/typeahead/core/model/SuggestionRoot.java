package no.microservices.typeahead.core.model;

import no.microservices.typeahead.model.SuggestionResponse;

import java.util.List;

public class SuggestionRoot {
    private List<SuggestionResponse> items;

    public SuggestionRoot() {
    }

    public SuggestionRoot(List<SuggestionResponse> items) {
        this.items = items;
    }

    public List<SuggestionResponse> getItems() {
        return items;
    }

    public void setItems(List<SuggestionResponse> items) {
        this.items = items;
    }
}
