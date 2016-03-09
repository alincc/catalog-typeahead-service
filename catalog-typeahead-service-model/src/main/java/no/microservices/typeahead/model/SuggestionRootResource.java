package no.microservices.typeahead.model;

import org.springframework.hateoas.ResourceSupport;

import java.util.List;

/**
 * Created by andreasb on 15.10.15.
 */
public class SuggestionRootResource extends ResourceSupport {
    private List<SuggestionResponse> items;

    public SuggestionRootResource() {
    }

    public SuggestionRootResource(List<SuggestionResponse> items) {
        this.items = items;
    }

    public List<SuggestionResponse> getItems() {
        return items;
    }

    public void setItems(List<SuggestionResponse> items) {
        this.items = items;
    }
}
