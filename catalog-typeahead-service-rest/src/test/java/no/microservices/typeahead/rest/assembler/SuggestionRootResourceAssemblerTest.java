package no.microservices.typeahead.rest.assembler;

import no.microservices.typeahead.core.model.SuggestionRoot;
import no.microservices.typeahead.model.SuggestionResponse;
import no.microservices.typeahead.model.SuggestionRootResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
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
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(createSuggestionRoot());
        assertThat(suggestionRootResource.getId().getHref(), is("http://localhost/catalog/v1/typeahead/search?q=Ra&mediaType=all"));
    }

    @Test
    public void whenConvertingModelToResourceThenResourceShouldHaveEmbeddedWithItems() throws Exception {
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(createSuggestionRoot());
        assertThat(suggestionRootResource.getEmbedded().getItems(), hasSize(1));
    }

    private SuggestionRoot createSuggestionRoot() {
        List<SuggestionResponse> suggestionResponses = new ArrayList<>();
        suggestionResponses.add(new SuggestionResponse("Ramona Kakestein", "Ramona Kakestein", 2));
        return new SuggestionRoot(suggestionResponses);
    }
}
