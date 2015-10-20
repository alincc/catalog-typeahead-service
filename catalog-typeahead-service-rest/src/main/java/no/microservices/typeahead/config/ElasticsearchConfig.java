package no.microservices.typeahead.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by andreasb on 15.10.15.
 */
@Configuration
public class ElasticsearchConfig {

    @Autowired
    private ElasticsearchSettings config;

    @Bean
    public Client esClient() {
        TransportClient transportClient = new TransportClient();
        for (String host : config.getHosts()) {
            transportClient.addTransportAddress(new InetSocketTransportAddress(host, 9300));
        }

        return transportClient;
    }
}
