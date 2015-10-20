package no.microservices.typeahead.model;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by andreasb on 19.10.15.
 */
public class SuggestionQuery {

    @NotEmpty
    private String sentence;

    private String mediaType = "ALL";

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
