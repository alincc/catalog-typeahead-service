package no.microservices.typeahead.core.elasticsearch.repository;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import no.microservices.typeahead.config.ElasticsearchSettings;
import no.microservices.typeahead.model.SuggestionQuery;
import no.microservices.typeahead.model.SuggestionRequest;
import no.microservices.typeahead.model.SuggestionResponse;
import no.nb.htrace.annotation.Traceable;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.common.text.Text;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Repository
public class ElasticsearchSuggestionRepository implements SuggestionRepository {
    private static final Logger LOG = LoggerFactory.getLogger(SuggestionRepository.class);

    private final Client esClient;
    private final ElasticsearchSettings esSettings;

    @Autowired
    public ElasticsearchSuggestionRepository(Client esClient, ElasticsearchSettings esSettings) {
        this.esClient = esClient;
        this.esSettings = esSettings;
    }


    @Override
    public void addSuggestion(SuggestionQuery suggestionQuery) throws Exception {
        String sentenceId = suggestionQuery.getSentence().hashCode() + "";
        String mediaTypeFixed = suggestionQuery.getMediatype().toLowerCase().trim();

        IndexRequest indexRequest = new IndexRequest(esSettings.getSuggestionIndex(), "suggestion", sentenceId)
                .source(jsonBuilder()
                        .startObject()
                        .field("sentence", suggestionQuery.getSentence())
                        .field("type_" + mediaTypeFixed, 0)
                        .endObject());

        UpdateRequest updateRequest = new UpdateRequest(esSettings.getSuggestionIndex(), "suggestion", sentenceId)
                .script("ctx._source.type_" + mediaTypeFixed + " = ctx._source.type_" + mediaTypeFixed + " == null ? 1 : ctx._source.type_" + mediaTypeFixed + "+1", ScriptService.ScriptType.INLINE)
                .addScriptParam("count", 1)
                .upsert(indexRequest);

        esClient.update(updateRequest).get();
    }

    @Override
    @Traceable(description = "es suggestion")
    @HystrixCommand(fallbackMethod = "getSuggestionsFallback")
    public Page<SuggestionResponse> getSuggestions(SuggestionRequest suggestionRequest, Pageable pageable) {
        String mediaTypeFixed = suggestionRequest.getMediaType().toLowerCase().trim();
        List<SuggestionResponse> suggestions = new ArrayList<>();

        SearchRequestBuilder searchRequestBuilder = esClient.prepareSearch(esSettings.getSuggestionIndex())
                .setTypes("suggestion")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchPhrasePrefixQuery("sentence", suggestionRequest.getQ()))
                .setFrom(pageable.getOffset()).setSize(pageable.getPageSize()).setExplain(true)
                .addSort("type_" + mediaTypeFixed, SortOrder.DESC)
                .addSort("sentence", SortOrder.ASC);

        if (suggestionRequest.isHighlight()) {
            searchRequestBuilder.addHighlightedField("sentence", 0, 0);
        }

        SearchResponse response = searchRequestBuilder.execute()
                .actionGet();

        for (SearchHit hit : response.getHits().getHits()) {
            int count = (hit.getSource().get("type_" + mediaTypeFixed) != null) ? (int)hit.getSource().get("type_" + mediaTypeFixed) : 0;
            String value = hit.getSource().get("sentence").toString();
            String label = (hit.getHighlightFields().get("sentence") != null) ? highlightedText(hit.getHighlightFields().get("sentence").fragments()) : value;
            suggestions.add(new SuggestionResponse(label, value, count));
        }

        return new PageImpl<>(suggestions, pageable, response.getHits().getTotalHits());
    }

    @Override
    @Traceable(description = "es suggestionField")
    @HystrixCommand(fallbackMethod = "getSuggestionsFieldFallback")
    public Page<SuggestionResponse> getSuggestionsField(SuggestionRequest suggestionRequest, String field, Pageable pageable) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!"ALL".equalsIgnoreCase(suggestionRequest.getMediaType()) && !StringUtils.isBlank(suggestionRequest.getMediaType())) {
            queryBuilder.must(QueryBuilders.queryStringQuery(suggestionRequest.getMediaType()).field("mediatype"));
        }
        queryBuilder.must(QueryBuilders.queryStringQuery(suggestionRequest.getQ() + "*").field(field));

        TermsBuilder termsBuilder = AggregationBuilders.terms("field").field(field + ".untouched").size(pageable.getPageSize());
        termsBuilder.include(buildUntouchedRegex(suggestionRequest.getQ()), Pattern.CASE_INSENSITIVE);

        SearchResponse response = esClient.prepareSearch(esSettings.getExpressionIndex())
                .setTypes("expressionrecord")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder)
                .addAggregation(termsBuilder)
                .setFrom(0).setSize(pageable.getPageSize())
                .execute()
                .actionGet();

        List<SuggestionResponse> suggestions = new ArrayList<>();

        Terms terms = response.getAggregations().get("field");
        suggestions.addAll(terms.getBuckets().stream().map(entry -> new SuggestionResponse(entry.getKey(),entry.getKey(),0)).collect(Collectors.toList()));

        return new PageImpl<>(suggestions, pageable, response.getHits().getTotalHits());
    }

    private String highlightedText(Text[] fragments) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Text fragment : fragments) {
            stringBuilder.append(fragment.string());
        }
        return stringBuilder.toString();
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

    private Page<SuggestionResponse> getSuggestionsFallback(SuggestionRequest suggestionRequest, Pageable pageable) {
        return new PageImpl<>(Collections.EMPTY_LIST);
    }

    private Page<SuggestionResponse> getSuggestionsFieldFallback(SuggestionRequest suggestionRequest, String field, Pageable pageable) {
        return new PageImpl<>(Collections.EMPTY_LIST);
    }
}
