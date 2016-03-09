package no.microservices.typeahead.rest.controller;

import no.microservices.typeahead.core.elasticsearch.service.ISuggestionService;
import no.microservices.typeahead.core.model.SuggestionRoot;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionRootResource;
import no.microservices.typeahead.rest.assembler.SuggestionRootResourceAssembler;
import no.microservices.typeahead.rest.validator.FieldValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<SuggestionRootResource> getSuggestion(SuggestionRequest suggestionRequest) {
        SuggestionRoot suggestionRoot = suggestionService.getSuggestions(suggestionRequest);
        SuggestionRootResourceAssembler suggestionRootResourceAssembler = new SuggestionRootResourceAssembler();
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(suggestionRoot);
        return new ResponseEntity<>(suggestionRootResource, HttpStatus.OK);
    }

    @RequestMapping(value = "/search", method = RequestMethod.PUT)
    public ResponseEntity saveSuggestion(@RequestBody SuggestionQuery suggestionQuery) {
        suggestionService.saveSuggestion(suggestionQuery);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{field}", method = RequestMethod.GET)
    public ResponseEntity<SuggestionRootResource> getSuggestionField(@PathVariable("field") String field, SuggestionRequest suggestionRequest) {
        FieldValidator.validate(field);
        SuggestionRoot suggestionField = suggestionService.getSuggestionsField(suggestionRequest, field);
        SuggestionRootResourceAssembler suggestionRootResourceAssembler = new SuggestionRootResourceAssembler();
        SuggestionRootResource suggestionRootResource = suggestionRootResourceAssembler.toResource(suggestionField);
        return new ResponseEntity<>(suggestionRootResource, HttpStatus.OK);
    }

}
