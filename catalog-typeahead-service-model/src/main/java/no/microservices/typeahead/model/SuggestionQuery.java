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
    private String mediatype = "ALL";

    public SuggestionQuery() {
    }

    public SuggestionQuery(String sentence, String mediatype) {
        this.sentence = sentence;
        this.mediatype = mediatype;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getMediatype() {
        return mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }
}
