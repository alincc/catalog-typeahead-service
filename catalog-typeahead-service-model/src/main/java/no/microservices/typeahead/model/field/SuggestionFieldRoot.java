package no.microservices.typeahead.model.field;

import java.util.List;

/**
 * Created by andreasb on 15.10.15.
 */
public class SuggestionFieldRoot {
    private List<SuggestionFieldResponse> items;

    public SuggestionFieldRoot() {
    }

    public SuggestionFieldRoot(List<SuggestionFieldResponse> items) {
        this.items = items;
    }

    public List<SuggestionFieldResponse> getItems() {
        return items;
    }

    public void setItems(List<SuggestionFieldResponse> items) {
        this.items = items;
    }
}
