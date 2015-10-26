package no.microservices.typeahead.it;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Created by alfredw on 9/14/15.
 */
public class EmbeddedElasticsearch {

    private static final String ES_DATA_DIRECTORY = "target/es-data";
    private static final String EXPRESSION_SCHEMA_NAME = "expressionrecords";
    private static final String EXPRESSION_TYPE_NAME = "expressionrecord";
    private static final String SUGGESTION_SCHEMA_NAME = "suggestioncounters";
    private static final String SUGGESTION_TYPE_NAME = "suggestion";
    private static EmbeddedElasticsearch embeddedElasticsearch;
    private Node node;
    private Client client;

    public static EmbeddedElasticsearch getInstance() throws IOException {
        if (embeddedElasticsearch == null) {
            synchronized (EmbeddedElasticsearch.class) {
                embeddedElasticsearch = new EmbeddedElasticsearch();
            }
        }
        return embeddedElasticsearch;
    }

    private EmbeddedElasticsearch() throws IOException {
        initializeNode();
        initializeIndex();
        initializeSettings();
        initializeMappings();
        initializeData();
    }

    private void initializeNode() {
        ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder()
                .put("http.enabled", false)
                .put("path.data", ES_DATA_DIRECTORY)
                .put("script.disable_dynamic", false);

        node = nodeBuilder()
                .settings(settings)
                .local(true)
                .node();

        client = node.client();
    }

    private void initializeIndex() {
        Settings indexSettings = ImmutableSettings.settingsBuilder()
                .put("number_of_shards", 1)
                .put("number_of_replicas", 0)
                .build();
        CreateIndexRequest createIndexRequestExpression = new CreateIndexRequest(EXPRESSION_SCHEMA_NAME, indexSettings);
        CreateIndexRequest createIndexRequestSuggestion = new CreateIndexRequest(SUGGESTION_SCHEMA_NAME, indexSettings);
        client.admin().indices().create(createIndexRequestExpression).actionGet();
        client.admin().indices().create(createIndexRequestSuggestion).actionGet();
        client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
    }

    private void initializeSettings() throws IOException {
        String file = getClass().getClassLoader().getResource("expressionrecords_settings.json").getFile();
        if(file != null) {
            client.admin().indices().prepareClose(EXPRESSION_SCHEMA_NAME).execute().actionGet();
            String settings = new String(Files.readAllBytes(Paths.get(file)));
            client.admin().indices().prepareUpdateSettings(EXPRESSION_SCHEMA_NAME).setSettings(settings).execute().actionGet();
            client.admin().indices().prepareOpen(EXPRESSION_SCHEMA_NAME).execute().actionGet();
        }
    }

    private void initializeMappings() throws IOException {
        String expressionFile = getClass().getClassLoader().getResource("expressionrecords_mapping.json").getFile();
        if(expressionFile != null) {
            String mapping = new String(Files.readAllBytes(Paths.get(expressionFile)));
            client.admin().indices()
                    .preparePutMapping(EXPRESSION_SCHEMA_NAME).setSource(mapping)
                    .setType(EXPRESSION_TYPE_NAME).execute().actionGet();
        }

        String suggestionFile = getClass().getClassLoader().getResource("suggestioncounters_mapping.json").getFile();
        if(suggestionFile != null) {
            String mapping = new String(Files.readAllBytes(Paths.get(suggestionFile)));
            client.admin().indices()
                    .preparePutMapping(SUGGESTION_SCHEMA_NAME).setSource(mapping)
                    .setType(SUGGESTION_TYPE_NAME).execute().actionGet();
        }
    }

    private void clearDatabase() {
        client.prepareDelete().setType(SUGGESTION_TYPE_NAME).execute();
        client.prepareDelete().setType(EXPRESSION_TYPE_NAME).execute();
    }

    private void initializeData() throws IOException {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        bulkRequest.add(client.prepareIndex(EXPRESSION_SCHEMA_NAME, EXPRESSION_TYPE_NAME, "0b8501b8e2b822c8ec13558de82aaef9").setSource(getDataSource("0b8501b8e2b822c8ec13558de82aaef9")));
        bulkRequest.add(client.prepareIndex(EXPRESSION_SCHEMA_NAME, EXPRESSION_TYPE_NAME, "41a7fb4e94aab9a88be23745a1504a92").setSource(getDataSource("41a7fb4e94aab9a88be23745a1504a92")));
        bulkRequest.add(client.prepareIndex(EXPRESSION_SCHEMA_NAME, EXPRESSION_TYPE_NAME, "92eb4d381bf7004de77337800654f610").setSource(getDataSource("92eb4d381bf7004de77337800654f610")));
        bulkRequest.add(client.prepareIndex(SUGGESTION_SCHEMA_NAME, SUGGESTION_TYPE_NAME, "-1232015698").setSource(getDataSource("-1232015698")));
        bulkRequest.execute().actionGet();

        RefreshRequest refreshRequest = new RefreshRequest().indices(EXPRESSION_SCHEMA_NAME, SUGGESTION_SCHEMA_NAME);
        client.admin().indices().refresh(refreshRequest).actionGet();
    }

    public void reinitializeDatabase() throws IOException {
        clearDatabase();
        initializeData();
    }

    private String getDataSource(String id) {
        try {
            String file = getClass().getClassLoader().getResource("esdata/" + id + ".json").getFile();
            if(file != null) {
                return new String(Files.readAllBytes(Paths.get(file)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    public Client getClient() {
        return node.client();
    }

    public void shutdown() throws IOException {
        client.close();
        node.close();
        FileUtils.deleteDirectory(new File(ES_DATA_DIRECTORY));
    }
}
