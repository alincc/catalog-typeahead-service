package no.microservices.typeahead.rest.controller;

import no.microservices.typeahead.core.elasticsearch.service.ISuggestionService;
import no.microservices.typeahead.core.model.SuggestionRoot;
import no.microservices.typeahead.model.EmbeddedWrapper;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionRootResource;
import no.microservices.typeahead.rest.assembler.SuggestionRootResourceAssembler;
import no.microservices.typeahead.rest.validator.FieldValidator;
import no.nb.htrace.annotation.Traceable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/catalog/v1/typeahead")
public class SuggestionController {
    private static final Logger LOG = LoggerFactory.getLogger(SuggestionController.class);

    private final ISuggestionService suggestionService;

    @Autowired
    public SuggestionController(ISuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @Traceable(description = "search")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<SuggestionRootResource> getSuggestion(SuggestionRequest suggestionRequest, @PageableDefault Pageable pageable) {
        SuggestionRoot suggestionRoot = suggestionService.getSuggestions(suggestionRequest, pageable);
        SuggestionRootResourceAssembler suggestionRootResourceAssembler = new SuggestionRootResourceAssembler();
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(suggestionRoot);
        return new ResponseEntity<>(suggestionRootResource, HttpStatus.OK);
    }

    @RequestMapping(value = "/search", method = RequestMethod.PUT)
    public ResponseEntity saveSuggestion(@RequestBody SuggestionQuery suggestionQuery) {
        suggestionService.saveSuggestion(suggestionQuery);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Traceable(description = "field")
    @RequestMapping(value = "/{field}", method = RequestMethod.GET)
    public ResponseEntity<SuggestionRootResource> getSuggestionField(@PathVariable("field") String field,
                                                                     SuggestionRequest suggestionRequest,
                                                                     @PageableDefault Pageable pageable) {
        FieldValidator.validate(field);
        SuggestionRoot suggestionField = suggestionService.getSuggestionsField(suggestionRequest, field, pageable);
        EmbeddedWrapper embedded = new EmbeddedWrapper(suggestionField.getPage().getContent());
        SuggestionRootResource suggestionRootResource = new SuggestionRootResource(embedded, null);
        suggestionRootResource.add(new Link(ServletUriComponentsBuilder.fromCurrentRequest().build().toString()).withSelfRel());
        return new ResponseEntity<>(suggestionRootResource, HttpStatus.OK);
    }

}
