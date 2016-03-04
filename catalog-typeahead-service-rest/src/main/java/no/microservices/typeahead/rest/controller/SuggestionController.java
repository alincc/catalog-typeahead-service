package no.microservices.typeahead.rest.controller;

import no.microservices.typeahead.core.elasticsearch.service.ISuggestionService;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalog/v1/typeahead")
public class SuggestionController {
    private static final Logger LOG = LoggerFactory.getLogger(SuggestionController.class);

    private final ISuggestionService suggestionService;

    @Autowired
    public SuggestionController(ISuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @RequestMapping(value = "/suggestions", method = RequestMethod.GET)
    public ResponseEntity<SuggestionRoot> getSuggestion(SuggestionRequest suggestionRequest) {
        SuggestionRoot suggestionRoot = suggestionService.getSuggestions(suggestionRequest);
        return new ResponseEntity<>(suggestionRoot, HttpStatus.OK);
    }

    @RequestMapping(value = "/suggestions/fields/{field}", method = RequestMethod.GET)
    public ResponseEntity<SuggestionRoot> getSuggestionField(@PathVariable("field") String field, SuggestionRequest suggestionRequest) {
        SuggestionRoot suggestionField = suggestionService.getSuggestionsField(suggestionRequest, field);
        return new ResponseEntity<>(suggestionField, HttpStatus.OK);
    }

    @RequestMapping(value = "/suggestions", method = RequestMethod.POST)
    public ResponseEntity saveSuggestion(@RequestBody SuggestionQuery suggestionQuery) {
        suggestionService.saveSuggestion(suggestionQuery);
        return new ResponseEntity(HttpStatus.OK);
    }
}
