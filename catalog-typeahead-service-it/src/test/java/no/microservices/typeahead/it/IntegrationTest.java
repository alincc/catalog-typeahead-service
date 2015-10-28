package no.microservices.typeahead.it;

import no.microservices.typeahead.Application;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRoot;
import no.microservices.typeahead.model.field.SuggestionFieldRoot;
import org.elasticsearch.client.Client;
import org.junit.After;
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

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, ElasticSearchTestConfig.class })
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

    @After
    public void tearDown() throws Exception {
        embeddedElasticsearch.reinitializeDatabase();
    }

    @Test
    public void suggestionQueryTest() {
        String uri = "http://localhost:" + port + "/catalog/typeahead/suggestions?query={query}&mediaType={mediaType}";
        ResponseEntity<SuggestionRoot> response = rest.getForEntity(uri, SuggestionRoot.class, "Knu", "all");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Knut Lippestad", response.getBody().getItems().get(0).getSentence());
        assertEquals(165, response.getBody().getItems().get(0).getCount());
    }

    @Test
    public void suggestionInsertTest() {
        String postUri = "http://localhost:" + port + "/catalog/typeahead/suggestions";
        SuggestionQuery request1 = new SuggestionQuery("Knut Hamsund", "bøker");
        SuggestionQuery request2 = new SuggestionQuery("Knut Lippestad", "bøker");
        SuggestionQuery request3 = new SuggestionQuery("Knut Limstrand", "bøker");
        ResponseEntity response1 = rest.postForEntity(postUri, request1, String.class);
        ResponseEntity response2 = rest.postForEntity(postUri, request2, String.class);
        ResponseEntity response3 = rest.postForEntity(postUri, request3, String.class);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        String uri = "http://localhost:" + port + "/catalog/typeahead/suggestions?query={query}&mediaType={mediaType}";
        ResponseEntity<SuggestionRoot> response = rest.getForEntity(uri, SuggestionRoot.class, "Knut", "bøker");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void suggestionFieldsTest() {
        String uri = "http://localhost:" + port + "/catalog/typeahead/suggestions/fields/namecreators/?query={query}";
        ResponseEntity<SuggestionFieldRoot> response = rest.getForEntity(uri, SuggestionFieldRoot.class, "K");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Koerner, Steen", response.getBody().getItems().get(0).getSentence());
    }
}