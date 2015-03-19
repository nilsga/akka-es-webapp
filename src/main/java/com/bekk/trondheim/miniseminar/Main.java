package com.bekk.trondheim.miniseminar;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.bekk.trondheim.miniseminar.akka.BlogActor;
import com.bekk.trondheim.miniseminar.akka.PatternsExt;
import com.bekk.trondheim.miniseminar.repository.BlogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

@SpringBootApplication
public class Main {


    @Bean
    public ActorSystem system() {
        return ActorSystem.create("MiniSeminar");
    }

    @Bean
    public ExecutionContext implicitExecutionContext() {
        return system().dispatcher();
    }

    @Bean
    public Timeout implicitTimeout() {
        return Timeout.apply(Duration.create(5, TimeUnit.SECONDS));
    }

    @Bean
    public PatternsExt implicitPatternExt() {
        return new PatternsExt(implicitExecutionContext(), implicitTimeout());
    }

    @Bean
    public Client esClient() {
        return nodeBuilder().local(true).node().client();
    }

    @Bean
    public BlogRepository blogRepository() {
        return new BlogRepository(esClient(), objectMapper());
    }

    @Bean
    public ActorRef blogActor() {
        return system().actorOf(BlogActor.mkProps(blogRepository()));
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
