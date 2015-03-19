package com.bekk.trondheim.miniseminar.domain;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BlogPost {
    public String author;
    public String text;

    @JsonCreator
    public BlogPost(@JsonProperty("author") String author, @JsonProperty("text") String text) {this.author = author;
        this.text = text;
    }
}
