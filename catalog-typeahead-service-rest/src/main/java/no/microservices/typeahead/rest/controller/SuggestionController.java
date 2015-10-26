package no.microservices.typeahead.rest.controller;

import no.microservices.typeahead.core.elasticsearch.service.ISuggestionService;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionRoot;
import no.microservices.typeahead.model.field.SuggestionFieldRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SuggestionController {

    private static final Logger LOG = LoggerFactory.getLogger(SuggestionController.class);

    @Autowired
    private ISuggestionService suggestionService;

    @RequestMapping(value = "/suggestions", method = RequestMethod.GET)
    public ResponseEntity<SuggestionRoot> getSuggestion(SuggestionRequest suggestionRequest) {
        SuggestionRoot suggestionRoot = suggestionService.getSuggestions(suggestionRequest.getQuery(), suggestionRequest.getMediaType(), suggestionRequest.getSize());
        return new ResponseEntity<>(suggestionRoot, HttpStatus.OK);
    }

    @RequestMapping(value = "/suggestions/fields/{field}", method = RequestMethod.GET)
    public ResponseEntity<SuggestionFieldRoot> getSuggestionField(@PathVariable("field") String field, SuggestionRequest suggestionRequest) {
        SuggestionFieldRoot suggestionFieldRoot = suggestionService.getSuggestionsField(suggestionRequest.getQuery(), field, suggestionRequest.getMediaType(), suggestionRequest.getSize());
        return new ResponseEntity<>(suggestionFieldRoot, HttpStatus.OK);
    }

    @RequestMapping(value = "/suggestions", method = RequestMethod.POST)
    public ResponseEntity saveSuggestion(@RequestBody SuggestionQuery suggestionQuery) {
        suggestionService.saveSuggestion(suggestionQuery.getSentence(), suggestionQuery.getMediaType());
        return new ResponseEntity(HttpStatus.OK);
    }
}
