package no.microservices.typeahead.model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;

/**
 * Created by andreasb on 15.10.15.
 */
public class SuggestionRequest {
    @Length(max = 1024)
    private String q;

    @Length(max = 32)
    private String mediatype = "ALL";

    @Max(100)
    private int size = 10;

    private boolean highlight;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getMediatype() {
        return mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }
}
