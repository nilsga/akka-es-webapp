package com.bekk.trondheim.miniseminar.repository;


import com.bekk.trondheim.miniseminar.domain.BlogPost;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class BlogRepository {

    private Client client;
    private ObjectMapper objectMapper;

    public BlogRepository(Client client, ObjectMapper objectMapper) {this.client = client;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<String> create(BlogPost blogPost) throws JsonProcessingException {
        ListenableActionFuture<IndexResponse> postFuture =
                client.prepareIndex("blog", "post").setSource(objectMapper.writeValueAsBytes(blogPost)).execute();
        CompletableFuture<String> convertedFuture = new CompletableFuture<>();
        postFuture.addListener(new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                convertedFuture.complete(indexResponse.getId());
            }

            @Override
            public void onFailure(Throwable throwable) {
                convertedFuture.completeExceptionally(throwable);
            }
        });
        return convertedFuture;

    }

    public CompletableFuture<List<BlogPost>> findAll() {
        ListenableActionFuture<SearchResponse> query = client.prepareSearch("blog").setTypes("post").setQuery(matchAllQuery()).execute();
        CompletableFuture<List<BlogPost>> result = new CompletableFuture<>();
        query.addListener(new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {
                List<BlogPost> posts = Stream.of(searchResponse.getHits().hits()).map(hit -> {
                    try {
                        return objectMapper.readValue(hit.source(), BlogPost.class);
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(toList());
                result.complete(posts);
            }

            @Override
            public void onFailure(Throwable throwable) {
                result.completeExceptionally(throwable);
            }
        });
        return result;
    }

}
