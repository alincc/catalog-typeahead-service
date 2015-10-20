package no.microservices.typeahead.core.elasticsearch.exception;

/**
 * Created by andreasb on 15.10.15.
 */
public class SuggestionException extends RuntimeException {
    public SuggestionException(String message) {
        super(message);
    }

    public SuggestionException(String message, Throwable cause) {
        super(message, cause);
    }
}
