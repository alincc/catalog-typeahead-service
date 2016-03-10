package no.microservices.typeahead.rest.assembler;

import no.microservices.typeahead.core.model.SuggestionRoot;
import no.microservices.typeahead.model.EmbeddedWrapper;
import no.microservices.typeahead.model.SuggestionResponse;
import no.microservices.typeahead.model.SuggestionRootResource;
import no.microservices.typeahead.rest.controller.SuggestionController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

public class SuggestionRootResourceAssembler extends ResourceAssemblerSupport<SuggestionRoot, SuggestionRootResource> {

    private final HateoasPageableHandlerMethodArgumentResolver pageableResolver = new HateoasPageableHandlerMethodArgumentResolver();
    public SuggestionRootResourceAssembler() {
        super(SuggestionController.class, SuggestionRootResource.class);
    }

    @Override
    public SuggestionRootResource toResource(SuggestionRoot entity) {
        EmbeddedWrapper embeddedWrapper = new EmbeddedWrapper(entity.getPage().getContent());
        SuggestionRootResource suggestionRootResource = new SuggestionRootResource(embeddedWrapper, asPageMetadata(entity.getPage()));
        suggestionRootResource.add(createPaginationLinks(entity.getPage()));
        return suggestionRootResource;
    }

    private static <T> PagedResources.PageMetadata asPageMetadata(Page<T> page) {
        return new PagedResources.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
    }

    private Links createPaginationLinks(Page<SuggestionResponse> page) {
        List<Link> links = new ArrayList<>();
        UriTemplate base = new UriTemplate(ServletUriComponentsBuilder.fromCurrentRequest().build().toString());

        if(page.hasPrevious()) {
            links.add(createLink(base, new PageRequest(0, page.getSize(), page.getSort()), Link.REL_FIRST));
        }

        if(page.hasPrevious()) {
            links.add(createLink(base, page.previousPageable(), Link.REL_PREVIOUS));
        }

        links.add(createLink(base, null, Link.REL_SELF));

        if(page.hasNext()) {
            links.add(createLink(base, page.nextPageable(), Link.REL_NEXT));
        }

        if(page.hasNext()) {
            int lastIndex = page.getTotalPages() == 0 ? 0 : page.getTotalPages() - 1;
            links.add(createLink(base, new PageRequest(lastIndex, page.getSize(), page.getSort()), Link.REL_LAST));
        }
        return new Links(links);
    }

    private Link createLink(UriTemplate base, Pageable pageable, String rel) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(base.expand());
        pageableResolver.enhance(builder, null, pageable);

        return new Link(new UriTemplate(builder.build().toString()), rel);
    }
}
