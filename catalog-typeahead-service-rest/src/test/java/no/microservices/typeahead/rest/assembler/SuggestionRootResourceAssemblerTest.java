package no.microservices.typeahead.rest.assembler;

import no.microservices.typeahead.core.model.SuggestionRoot;
import no.microservices.typeahead.model.SuggestionResponse;
import no.microservices.typeahead.model.SuggestionRootResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class SuggestionRootResourceAssemblerTest {

    private SuggestionRootResourceAssembler suggestionRootResourceAssembler;

    @Before
    public void setUp() {
        suggestionRootResourceAssembler = new SuggestionRootResourceAssembler();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/catalog/v1/typeahead/search?q=Ra&mediaType=all");
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @After
    public void cleanUp() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void whenConvertingModelToResourceThenAddSelfLink() throws Exception {
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(createSuggestionRoot(new PageRequest(0, 10), 100));
        assertThat(suggestionRootResource.getId().getHref(), is("http://localhost/catalog/v1/typeahead/search?q=Ra&mediaType=all"));
    }

    @Test
    public void whenConvertingModelToResourceThenResourceShouldHaveEmbeddedWithItems() throws Exception {
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(createSuggestionRoot(new PageRequest(0, 10), 100));
        assertThat(suggestionRootResource.getEmbedded().getItems(), hasSize(1));
    }

    @Test
    public void whenConvertingModelToResourceThenResourceShouldHavePageMetadata() throws Exception {
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(createSuggestionRoot(new PageRequest(0, 10), 100));
        assertThat(suggestionRootResource.getMetadata(), is(not(nullValue())));
    }

    @Test
    public void whenOnFirstPageThenThereShouldBeNoLinkToPrevPage() throws Exception {
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(createSuggestionRoot(new PageRequest(0, 10), 100));
        assertThat(suggestionRootResource.getLink(Link.REL_PREVIOUS), is(nullValue()));
    }

    @Test
    public void whenOnSecondPageThenThereShouldBeLinkToPrevPage() throws Exception {
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(createSuggestionRoot(new PageRequest(1, 10), 100));
        assertThat(suggestionRootResource.getLink(Link.REL_PREVIOUS).getHref(), is("http://localhost/catalog/v1/typeahead/search?q=Ra&mediaType=all&page=0&size=10"));
    }

    @Test
    public void whenOnFirstPageAndThereExistASecondPageThenThereShouldBeALinkToNextPage() throws Exception {
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(createSuggestionRoot(new PageRequest(0, 10), 100));
        assertThat(suggestionRootResource.getLink(Link.REL_NEXT).getHref(), is("http://localhost/catalog/v1/typeahead/search?q=Ra&mediaType=all&page=1&size=10"));
    }

    @Test
    public void whenOnLastPageThenThereShouldBeNoNextPageLink() throws Exception {
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(createSuggestionRoot(new PageRequest(9, 10), 100));
        assertThat(suggestionRootResource.getLink(Link.REL_NEXT), is(nullValue()));
    }

    @Test
    public void whenNotOnLastPageThenThereShouldBeLinkToLastPage() throws Exception {
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(createSuggestionRoot(new PageRequest(0, 10), 100));
        assertThat(suggestionRootResource.getLink(Link.REL_LAST).getHref(), is("http://localhost/catalog/v1/typeahead/search?q=Ra&mediaType=all&page=9&size=10"));
    }

    @Test
    public void whenNotOnFirstPageThenThereShouldBeLinkToFirstPage() throws Exception {
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(createSuggestionRoot(new PageRequest(1, 10), 100));
        assertThat(suggestionRootResource.getLink(Link.REL_FIRST).getHref(), is("http://localhost/catalog/v1/typeahead/search?q=Ra&mediaType=all&page=0&size=10"));
    }

    private SuggestionRoot createSuggestionRoot(Pageable pageable, int total) {
        List<SuggestionResponse> suggestionResponses = new ArrayList<>();
        suggestionResponses.add(new SuggestionResponse("Ramona Kakestein", "Ramona Kakestein", 2));
        Page<SuggestionResponse> page = new PageImpl<>(suggestionResponses, pageable, total);
        return new SuggestionRoot(page);
    }
}
