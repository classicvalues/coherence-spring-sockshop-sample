/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.orders;

import java.net.URI;

import com.oracle.coherence.examples.sockshop.spring.orders.controller.support.NewOrderRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration test to ensure Coherence metrics are properly exposed when
 * property {@code coherence.metrics.http.enabled} is set to {@code true}.
 *
 * @author Gunnar Hillert
 */
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"coherence.metrics.http.enabled=true"
		}
)
@AutoConfigureWebTestClient
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdersMetricsTests {

	public static final int COHERENCE_METRICS_PORT = 9612;
	public static final String COHERENCE_METRICS_URL = "http://localhost:" + COHERENCE_METRICS_PORT + "/metrics";
	public static final String ORDERS_URL = "/orders";

	@Autowired
	protected WebTestClient webTestClient;

	@Test
	@Order(1)
	void createOrder() {
		String baseUri = "";
		NewOrderRequest req = NewOrderRequest.builder()
				.customer(URI.create(baseUri + "/customers/homer"))
				.address(URI.create(baseUri + "/addresses/homer:1"))
				.card(URI.create(baseUri + "/cards/homer:1234"))
				.items(URI.create(baseUri + "/carts/homer/items"))
				.build();

		webTestClient.post()
				.uri(ORDERS_URL)
				.bodyValue(req)
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
					.jsonPath("total").isEqualTo(14.0f)
					.jsonPath("status").isEqualTo("CREATED");
	}

	@Test
	@Order(2)
	void verifyCoherenceMetrics() {
		this.webTestClient.get()
				.uri(COHERENCE_METRICS_URL + "/Coherence.Cache.Size?name=orders&tier=back")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
					.consumeWith(System.out::println)
					.jsonPath("$.length()").isEqualTo(1)
					.jsonPath("$[0].tags.name").isEqualTo("orders")
					.jsonPath("$[0].value").isEqualTo(1);
	}
}