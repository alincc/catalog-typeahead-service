package no.microservices.typeahead.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EmbeddedWrapper {
    private List<SuggestionResponse> items;

    public EmbeddedWrapper() {
    }

    public EmbeddedWrapper(List<SuggestionResponse> items) {
        this.items = items;
    }

    public List<SuggestionResponse> getItems() {
        return items;
    }

    public void setItems(List<SuggestionResponse> items) {
        this.items = items;
    }
}
