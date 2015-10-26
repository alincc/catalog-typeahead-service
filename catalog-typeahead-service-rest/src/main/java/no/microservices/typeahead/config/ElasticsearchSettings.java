package no.microservices.typeahead.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andreasb on 15.10.15.
 */
@Configuration
@ConfigurationProperties(prefix = "elastic")
public class ElasticsearchSettings {

    private List<String> hosts = new ArrayList<>();
    private String suggestionIndex;
    private String expressionIndex;

    public List<String> getHosts() {
        return hosts;
    }

    public String getSuggestionIndex() {
        return suggestionIndex;
    }

    public String getExpressionIndex() {
        return expressionIndex;
    }

    public void setExpressionIndex(String expressionIndex) {
        this.expressionIndex = expressionIndex;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public void setSuggestionIndex(String suggestionIndex) {
        this.suggestionIndex = suggestionIndex;
    }

}