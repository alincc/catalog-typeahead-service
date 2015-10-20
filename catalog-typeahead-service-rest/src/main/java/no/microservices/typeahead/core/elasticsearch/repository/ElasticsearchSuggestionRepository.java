package no.microservices.typeahead.core.elasticsearch.repository;

import no.microservices.typeahead.config.ElasticsearchSettings;
import no.microservices.typeahead.model.SuggestionResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by andreasb on 16.10.15.
 */
@Repository
public class ElasticsearchSuggestionRepository implements SuggestionRepository {

    @Autowired
    private Client esClient;

    @Autowired
    private ElasticsearchSettings esSettings;

    @Override
    public void addSuggestion(String sentence, String mediaType) throws Exception {
        String sentenceId = sentence.hashCode() + "";
        String mediaTypeFixed = mediaType.toLowerCase().trim();

        IndexRequest indexRequest = new IndexRequest(esSettings.getIndex(), "suggestion", sentenceId)
                .source(jsonBuilder()
                        .startObject()
                        .field("sentence", sentence)
                        .field("type_" + mediaTypeFixed, 0)
                        .endObject());

        UpdateRequest updateRequest = new UpdateRequest(esSettings.getIndex(), "suggestion", sentenceId)
                .script("ctx._source.type_" + mediaTypeFixed + " = ctx._source.type_" + mediaTypeFixed + " == null ? 1 : ctx._source.type_" + mediaTypeFixed + "+1", ScriptService.ScriptType.INLINE)
                .addScriptParam("count", 1)
                .upsert(indexRequest);

        esClient.update(updateRequest).get();
    }

    @Override
    public List<SuggestionResponse> getSuggestions(String query, String mediaType, int size) {
        String mediaTypeFixed = mediaType.toLowerCase().trim();

        SearchResponse response = esClient.prepareSearch(esSettings.getIndex())
                .setTypes("suggestion")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchPhrasePrefixQuery("sentence", query))
                .setFrom(0).setSize(size).setExplain(true)
                .execute()
                .actionGet();

        List<SuggestionResponse> suggestions = new ArrayList<>();

        for (SearchHit hit : response.getHits().getHits()) {
            int count = (hit.getSource().get("type_" + mediaTypeFixed) != null) ? (int)hit.getSource().get("type_" + mediaTypeFixed) : 0;
            suggestions.add(new SuggestionResponse(hit.getSource().get("sentence").toString(), count));
        }

        return suggestions;
    }
}
