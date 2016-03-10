package no.microservices.typeahead.core.model;

import no.microservices.typeahead.model.SuggestionResponse;
import org.springframework.data.domain.Page;

public class SuggestionRoot {
    private Page<SuggestionResponse> page;

    public SuggestionRoot(Page<SuggestionResponse> page) {
        this.page = page;
    }

    public Page<SuggestionResponse> getPage() {
        return page;
    }

    public void setPage(Page<SuggestionResponse> page) {
        this.page = page;
    }
}
