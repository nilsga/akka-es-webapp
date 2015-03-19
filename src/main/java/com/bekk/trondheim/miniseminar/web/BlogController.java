package com.bekk.trondheim.miniseminar.web;


import akka.actor.ActorRef;
import com.bekk.trondheim.miniseminar.akka.BlogActor;
import com.bekk.trondheim.miniseminar.akka.PatternsExt;
import com.bekk.trondheim.miniseminar.domain.BlogPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    ActorRef blogActor;

    @Autowired
    PatternsExt pattern;

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public CompletableFuture<String> createPost(@RequestParam("author") String author, @RequestParam("text") String text) {
        return pattern.ask(blogActor, new BlogActor.New(new BlogPost(author, text)));
    }

    @RequestMapping("")
    public CompletableFuture<List<BlogPost>> listPosts() {
        return pattern.ask(blogActor, new BlogActor.GetAll());
    }

}
