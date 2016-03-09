package no.microservices.typeahead.it;

import no.microservices.typeahead.Application;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRootResource;
import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, ElasticSearchTestConfig.class})
@WebIntegrationTest("server.port: 0")
public class IntegrationTest {

    @Value("${local.server.port}")
    int port;

    @Autowired
    WebApplicationContext context;

    RestTemplate rest;

    @Autowired
    Client esClient;

    private static EmbeddedElasticsearch embeddedElasticsearch;

    @BeforeClass
    public static void init() throws IOException {
        embeddedElasticsearch = EmbeddedElasticsearch.getInstance();
    }

    @Before
    public void setup() throws Exception {
        rest = new TestRestTemplate();
    }

    @Test
    public void suggestionQueryTest() {
        String uri = "http://localhost:" + port + "/catalog/v1/typeahead/search?q={query}&mediatype={mediatype}";
        ResponseEntity<SuggestionRootResource> response = rest.getForEntity(uri, SuggestionRootResource.class, "Knu", "all");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Knut Lippestad", response.getBody().getEmbedded().getItems().get(0).getValue());
        assertEquals(165, response.getBody().getEmbedded().getItems().get(0).getCount());
    }

    @Test
    public void testHighlight() {
        String uri = "http://localhost:" + port + "/catalog/v1/typeahead/search?q={query}&mediatype={mediatype}&highlight=true";
        ResponseEntity<SuggestionRootResource> entity = rest.getForEntity(uri, SuggestionRootResource.class, "Knut", "all");
        assertThat("Text is highlighted", entity.getBody().getEmbedded().getItems().get(0).getLabel(), is("<em>Knut</em> Lippestad"));
    }

    @Test
    public void suggestionInsertTest() {
        String putUri = "http://localhost:" + port + "/catalog/v1/typeahead/search";
        SuggestionQuery request1 = new SuggestionQuery("Kjeks Luthor", "bøker");
        rest.put(putUri, request1);

        embeddedElasticsearch.refreshIndices();

        String uri = "http://localhost:" + port + "/catalog/v1/typeahead/search?q={query}&mediatype={mediatype}";
        ResponseEntity<SuggestionRootResource> response = rest.getForEntity(uri, SuggestionRootResource.class, "Kjeks", "bøker");

        assertThat("Response should contain 1 hit", response.getBody().getEmbedded().getItems().size(), is(1));
        assertThat("Should return Kjeks Luthor", response.getBody().getEmbedded().getItems().get(0).getLabel(), is("Kjeks Luthor"));
    }

    @Test
    public void suggestionFieldsTest() {
        String uri = "http://localhost:" + port + "/catalog/v1/typeahead/namecreators?q={query}";
        ResponseEntity<SuggestionRootResource> response = rest.getForEntity(uri, SuggestionRootResource.class, "K");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Koerner, Steen", response.getBody().getEmbedded().getItems().get(0).getLabel());
    }
}