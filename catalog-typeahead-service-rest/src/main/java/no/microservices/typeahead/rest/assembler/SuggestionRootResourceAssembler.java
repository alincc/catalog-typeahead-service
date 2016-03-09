package no.microservices.typeahead.rest.assembler;

import no.microservices.typeahead.core.model.SuggestionRoot;
import no.microservices.typeahead.model.SuggestionRootResource;
import no.microservices.typeahead.model.EmbeddedWrapper;
import no.microservices.typeahead.rest.controller.SuggestionController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class SuggestionRootResourceAssembler extends ResourceAssemblerSupport<SuggestionRoot, SuggestionRootResource> {

    public SuggestionRootResourceAssembler() {
        super(SuggestionController.class, SuggestionRootResource.class);
    }

    @Override
    public SuggestionRootResource toResource(SuggestionRoot entity) {
        EmbeddedWrapper embeddedWrapper = new EmbeddedWrapper(entity.getItems());
        SuggestionRootResource suggestionRootResource = new SuggestionRootResource(embeddedWrapper);
        suggestionRootResource.add(createSelfLink());
        return suggestionRootResource;
    }

    public Link createSelfLink() {
        return new Link(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString()).withSelfRel();
    }
}
