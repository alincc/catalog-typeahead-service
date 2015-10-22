package no.microservices.typeahead.model;

import java.util.List;

/**
 * Created by andreasb on 15.10.15.
 */
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
