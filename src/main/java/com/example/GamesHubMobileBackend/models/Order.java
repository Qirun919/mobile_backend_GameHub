package com.example.GamesHubMobileBackend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private List<String> gameId;
    private double totalPrice;
    private String paymentStatus;
    private String stripePaymentIntentId;
    private String checkoutUrl;
}