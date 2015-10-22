package no.microservices.typeahead.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by andreasb on 19.10.15.
 */
public class SuggestionQuery {

    @NotEmpty
    @Length(max = 1024)
    private String sentence;

    @Length(max = 32)
    private String mediaType = "ALL";

    public SuggestionQuery() {
    }

    public SuggestionQuery(String sentence, String mediaType) {
        this.sentence = sentence;
        this.mediaType = mediaType;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
