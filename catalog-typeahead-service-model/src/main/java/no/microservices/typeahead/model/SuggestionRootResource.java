package no.microservices.typeahead.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by andreasb on 15.10.15.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuggestionRootResource extends ResourceSupport {

    @JsonProperty("page")
    private PagedResources.PageMetadata metadata;

    @JsonProperty("_embedded")
    private EmbeddedWrapper embedded;

    public SuggestionRootResource() {
    }

    public SuggestionRootResource(EmbeddedWrapper embedded, PagedResources.PageMetadata metadata) {
        this.embedded = embedded;
        this.metadata = metadata;
    }

    @JsonIgnore
    public EmbeddedWrapper getEmbedded() {
        return embedded;
    }

    @JsonIgnore
    public PagedResources.PageMetadata getMetadata() {
        return metadata;
    }
}
