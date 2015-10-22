package no.microservices.typeahead.it;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import no.microservices.typeahead.Application;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionRoot;
import okio.Buffer;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, RibbonClientConfiguration.class, ElasticsearchConfiguration.class})
@WebIntegrationTest("server.port: 0")
public class IntegrationTest {

    @Value("${local.server.port}")
    int port;

    @Autowired
    ILoadBalancer loadBalancer;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;
    MockWebServer mockWebServer;
    RestTemplate rest;

    @Autowired
    Client esClient;

    static final String INDEX_NAME = "suggestioncounters";

    @Before
    public void setup() throws Exception {
        setupElasticsearch();
        rest = new TestRestTemplate();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockWebServer = new MockWebServer();

        // Read mock data
        String mock1 = IOUtils.toString(new ClassPathResource("mock1.json").getInputStream());

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                if (request.getPath().equals("/service1/method1")) {
                    return new MockResponse().setBody(mock1).setHeader("Content-Type", "application/hal+json; charset=utf-8");
                }
                else if (request.getPath().equals("/service1/method2")) {
                    return new MockResponse().setBody(mock1).setHeader("Content-Type", "application/hal+json; charset=utf-8");
                }
                else if (request.getPath().equals("/service2/method1")) {
                    return new MockResponse().setBody(mock1).setHeader("Content-Type", "application/hal+json; charset=utf-8");
                }
                else {
                    return new MockResponse().setResponseCode(404);
                }
            }
        };
        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start();

        BaseLoadBalancer baseLoadBalancer = (BaseLoadBalancer) loadBalancer;
        baseLoadBalancer.setServersList(Arrays.asList(new Server(mockWebServer.getHostName(), mockWebServer.getPort())));
    }

    @After
    public void tearDown() throws Exception {
        tearDownElasticsearch();
        mockWebServer.shutdown();
    }

    @Test
    public void testEmptyBase() {
        String uri = "http://localhost:" + port + "/suggestions?query={query}&mediaType={mediaType}";
        ResponseEntity<SuggestionRoot> response = rest.getForEntity(uri, SuggestionRoot.class, "Knu", "all");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void testInsertAndGet() {
        String postUri = "http://localhost:" + port + "/suggestions";
        SuggestionQuery request1 = new SuggestionQuery("Knut Hamsund", "bøker");
        SuggestionQuery request2 = new SuggestionQuery("Knut Lippestad", "bøker");
        SuggestionQuery request3 = new SuggestionQuery("Knut Limstrand", "bøker");
        ResponseEntity response1 = rest.postForEntity(postUri, request1, String.class);
        ResponseEntity response2 = rest.postForEntity(postUri, request2, String.class);
        ResponseEntity response3 = rest.postForEntity(postUri, request3, String.class);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        String uri = "http://localhost:" + port + "/suggestions?query={query}&mediaType={mediaType}";
        ResponseEntity<SuggestionRoot> response = rest.getForEntity(uri, SuggestionRoot.class, "Knut", "bøker");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void setupElasticsearch() throws IOException {
        Settings indexSettings = ImmutableSettings.settingsBuilder()
                .put("number_of_shards", 1)
                .put("number_of_replicas", 1)
                .build();
        CreateIndexRequest indexRequest = new CreateIndexRequest(INDEX_NAME, indexSettings);
        esClient.admin().indices().create(indexRequest).actionGet();

        String mapping = "{\n" +
                "  \"suggestion\": {\n" +
                "    \"date_formats\": [\n" +
                "      \"dateOptionalTime\",\n" +
                "      \"yyyy/MM/dd HH:mm:ss||yyyy/MM/dd\"\n" +
                "    ],\n" +
                "    \"_source\": {\n" +
                "      \"enabled\": true\n" +
                "    },\n" +
                "    \"_all\": {\n" +
                "      \"enabled\": false,\n" +
                "      \"store\": \"no\",\n" +
                "      \"term_vector\": \"no\"\n" +
                "    },\n" +
                "    \"properties\": {\n" +
                "      \"sentence\": {\n" +
                "        \"type\": \"string\",\n" +
                "        \"index\": \"analyzed\",\n" +
                "        \"term_vector\": \"with_positions_offsets\",\n" +
                "        \"fields\": {\n" +
                "          \"untouched\": {\n" +
                "            \"type\": \"string\",\n" +
                "            \"index\": \"not_analyzed\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"dynamic_templates\": [\n" +
                "      {\n" +
                "        \"counter_template\": {\n" +
                "          \"match\": \"type_*\",\n" +
                "          \"mapping\": {\n" +
                "            \"type\": \"long\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        PutMappingResponse response = esClient.admin().indices().preparePutMapping(INDEX_NAME)
                .setSource(mapping).setType("suggestion").execute().actionGet();
    }

    private void tearDownElasticsearch() {
        DeleteIndexRequest indexRequest = new DeleteIndexRequest(INDEX_NAME);
        esClient.admin().indices().delete(indexRequest).actionGet();
        esClient.close();
    }
}

@Configuration
class RibbonClientConfiguration {
    @Bean
    public ILoadBalancer ribbonLoadBalancer() {
        return new BaseLoadBalancer();
    }
}

@Configuration
class ElasticsearchConfiguration {

    @Bean
    public Client esClient() {
        Settings settings = ImmutableSettings.settingsBuilder().put("script.disable_dynamic", false).build();
        Node node = NodeBuilder.nodeBuilder().settings(settings).node();
        Client client = node.client();

        return client;
    }
}