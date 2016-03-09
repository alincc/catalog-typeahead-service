package no.microservices.typeahead.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by andreasb on 15.10.15.
 */
public class SuggestionRootResource extends ResourceSupport {

    @JsonProperty("_embedded")
    private EmbeddedWrapper embedded;

    public SuggestionRootResource() {
    }

    public SuggestionRootResource(EmbeddedWrapper embedded) {
        this.embedded = embedded;
    }

    @JsonIgnore
    public EmbeddedWrapper getEmbedded() {
        return embedded;
    }
}
