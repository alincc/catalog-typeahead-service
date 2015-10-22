package no.microservices.typeahead.model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;

/**
 * Created by andreasb on 15.10.15.
 */
public class SuggestionRequest {
    @Length(max = 1024)
    private String query;

    @Length(max = 32)
    private String mediaType = "ALL";

    @Max(100)
    private int maxResults = 10;

    private boolean addHighlight;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public boolean isAddHighlight() {
        return addHighlight;
    }

    public void setAddHighlight(boolean addHighlight) {
        this.addHighlight = addHighlight;
    }
}
