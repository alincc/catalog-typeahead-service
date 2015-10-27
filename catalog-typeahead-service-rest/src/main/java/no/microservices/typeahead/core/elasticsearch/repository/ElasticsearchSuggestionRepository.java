package no.microservices.typeahead.core.elasticsearch.repository;

import no.microservices.typeahead.config.ElasticsearchSettings;
import no.microservices.typeahead.model.SuggestionResponse;
import no.microservices.typeahead.model.field.SuggestionFieldResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by andreasb on 16.10.15.
 */
@Repository
public class ElasticsearchSuggestionRepository implements SuggestionRepository {

    private static final Logger LOG = LoggerFactory.getLogger(SuggestionRepository.class);


    @Autowired
    private Client esClient;

    @Autowired
    private ElasticsearchSettings esSettings;

    @Override
    public void addSuggestion(String sentence, String mediaType) throws Exception {
        String sentenceId = sentence.hashCode() + "";
        String mediaTypeFixed = mediaType.toLowerCase().trim();

        IndexRequest indexRequest = new IndexRequest(esSettings.getSuggestionIndex(), "suggestion", sentenceId)
                .source(jsonBuilder()
                        .startObject()
                        .field("sentence", sentence)
                        .field("type_" + mediaTypeFixed, 0)
                        .endObject());

        UpdateRequest updateRequest = new UpdateRequest(esSettings.getSuggestionIndex(), "suggestion", sentenceId)
                .script("ctx._source.type_" + mediaTypeFixed + " = ctx._source.type_" + mediaTypeFixed + " == null ? 1 : ctx._source.type_" + mediaTypeFixed + "+1", ScriptService.ScriptType.INLINE)
                .addScriptParam("count", 1)
                .upsert(indexRequest);

        esClient.update(updateRequest).get();
    }

    @Override
    public List<SuggestionResponse> getSuggestions(String query, String mediaType, int size) {
        String mediaTypeFixed = mediaType.toLowerCase().trim();
        List<SuggestionResponse> suggestions = new ArrayList<>();

        SearchResponse response = esClient.prepareSearch(esSettings.getSuggestionIndex())
                .setTypes("suggestion")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchPhrasePrefixQuery("sentence", query))
                .setFrom(0).setSize(size).setExplain(true)
                .addSort("type_" + mediaTypeFixed, SortOrder.DESC)
                .addSort("sentence", SortOrder.ASC)
                .execute()
                .actionGet();

        for (SearchHit hit : response.getHits().getHits()) {
            int count = (hit.getSource().get("type_" + mediaTypeFixed) != null) ? (int)hit.getSource().get("type_" + mediaTypeFixed) : 0;
            suggestions.add(new SuggestionResponse(hit.getSource().get("sentence").toString(), count));
        }

        return suggestions;
    }

    @Override
    public List<SuggestionFieldResponse> getSuggestionsField(String query, String field, String mediaType, int size) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!"ALL".equalsIgnoreCase(mediaType) && !StringUtils.isBlank(mediaType)) {
            queryBuilder.must(QueryBuilders.queryStringQuery(mediaType).field("mediatype"));
        }
        queryBuilder.must(QueryBuilders.queryStringQuery(query + "*").field(field));

        TermsBuilder termsBuilder = AggregationBuilders.terms("field").field(field + ".untouched").size(size);
        termsBuilder.include(buildUntouchedRegex(query), Pattern.CASE_INSENSITIVE);

        SearchResponse response = esClient.prepareSearch(esSettings.getExpressionIndex())
                .setTypes("expressionrecord")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder)
                .addAggregation(termsBuilder)
                .setFrom(0).setSize(size)
                .setExplain(true)
                .execute()
                .actionGet();

        List<SuggestionFieldResponse> suggestions = new ArrayList<>();

        Terms terms = response.getAggregations().get("field");
        suggestions.addAll(terms.getBuckets().stream().map(entry -> new SuggestionFieldResponse(entry.getKey())).collect(Collectors.toList()));

        return suggestions;
    }

    private String buildUntouchedRegex(String searchString) {
        String[] tokens = searchString.split("[\\s,.\"-]");
        StringBuilder result = new StringBuilder("((");
        for (int i = 0; i < tokens.length; i++) {
            if (i > 0) {
                result.append("|");
            }
            result.append("(").append(tokens[i]).append(")");
        }
        result.append(").*){").append(tokens.length).append("}");
        return result.toString();
    }
}
