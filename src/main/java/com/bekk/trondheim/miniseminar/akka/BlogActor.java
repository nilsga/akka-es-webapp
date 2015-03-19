package com.bekk.trondheim.miniseminar.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import com.bekk.trondheim.miniseminar.domain.BlogPost;
import com.bekk.trondheim.miniseminar.repository.BlogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

import static akka.japi.pf.ReceiveBuilder.match;

public class BlogActor extends AbstractActor {

    private BlogRepository blogRepository;

    public static class New {
        public BlogPost post;
        public New(BlogPost post) {this.post = post;}
    }
    public static class GetAll {}
    public static class AllPosts {
        public List<BlogPost> posts;
        public AllPosts(List<BlogPost> posts) {this.posts = posts;}
    }
    public static class Get {}
    public static class Created {
        public String id;
        public Created(String id) {this.id = id;}
    }
    public static Props mkProps(BlogRepository blogRepository) {
        return Props.create(BlogActor.class, () -> new BlogActor(blogRepository));
    }

    public BlogActor(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
        receive(match(New.class, this::handleNewPost)
                .match(GetAll.class, this::handleGetAllPosts)
                .build());
    }

    private void handleGetAllPosts(GetAll msg) {
        ActorRef sender = sender();
        blogRepository.findAll().whenComplete((posts, err) -> {
            if (err != null) {
                sender.tell(new Status.Failure(err), self());
            }
            else {
                sender.tell(new AllPosts(posts), self());
            }
        });
    }

    private void handleNewPost(New msg) throws JsonProcessingException {ActorRef sender = sender();
        blogRepository.create(msg.post).whenComplete((id, err) -> {
            if(err != null) {
                sender.tell(new Status.Failure(err), self());
            }
            else {
                sender.tell(new Created(id), self());
            }
        });
    }

}
