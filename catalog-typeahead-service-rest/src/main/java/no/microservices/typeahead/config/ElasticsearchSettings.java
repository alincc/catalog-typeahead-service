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
    private String index;

    public List<String> getHosts() {
        return hosts;
    }

    public String getIndex() {
        return index;
    }


    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public void setIndex(String index) {
        this.index = index;
    }

}