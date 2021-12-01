/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.catalog.controller;

import com.oracle.coherence.examples.sockshop.spring.catalog.repository.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Implementation of the Catalog Service {@code /tags} API.
 */
@RequestMapping("/tags")
@RestController
public class TagsResource implements TagApi {

    @Autowired
    private CatalogRepository catalog;

    @Override
    public Tags getTags() {
        return new Tags(catalog.getTags());
    }

    public static class Tags {
        public Set<String> tags;
        public Object err;

        Tags(Set<String> tags) {
            this.tags = tags;
        }
    }
}
