package no.microservices.typeahead.model;

import org.hibernate.validator.constraints.Length;


/**
 * Created by andreasb on 15.10.15.
 */
public class SuggestionRequest {
    @Length(max = 1024)
    private String q;

    @Length(max = 32)
    private String mediaType = "ALL";

    private boolean highlight;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }
}
