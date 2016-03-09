package no.microservices.typeahead.rest.assembler;

import no.microservices.typeahead.core.model.SuggestionRoot;
import no.microservices.typeahead.model.SuggestionRootResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SuggestionRootResourceAssemblerTest {

    @Before
    public void setUp() {
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
        SuggestionRootResourceAssembler suggestionRootResourceAssembler = new SuggestionRootResourceAssembler();
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(createSuggestionRoot());
        assertThat(suggestionRootResource.getId().getHref(), is("http://localhost/catalog/v1/typeahead/search?q=Ra&mediaType=all"));
    }

    private SuggestionRoot createSuggestionRoot() {
        SuggestionRoot suggestionRoot = new SuggestionRoot();
        return suggestionRoot;
    }
}
