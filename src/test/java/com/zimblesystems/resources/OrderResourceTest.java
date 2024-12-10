package com.zimblesystems.resources;


import com.zimblesystems.dto.OrderRequest;
import com.zimblesystems.dto.OrderResponse;
import com.zimblesystems.services.OrderService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.core.MediaType;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@QuarkusTest
public class OrderResourceTest {

//    @InjectMock
//    OrderService orderService;


    @Test
    public void testCreateOrder_Success() {
        RestAssured.defaultParser = Parser.JSON;
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId("123SD");
        orderRequest.setQuantity(1);

        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setOrderId("order123");
        mockResponse.setStatus("CREATED");

//        when(orderService.createOrder(orderRequest)).thenReturn(Uni.createFrom().item(mockResponse));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequest)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(200)
                .body("status", is("CREATED"));
    }

    @Test
    public void test() {
        given()
                .when().get("/api/orders/test")
                .then()
                .statusCode(200)
                .body(CoreMatchers.is("Hello from RESTEasy Reactive"));
    }

    @Test
    public void testGetAllOrders_Success() {

        // Prepare mock response
        OrderResponse orderResponse1 = new OrderResponse();
        orderResponse1.setOrderId("1");
        orderResponse1.setStatus("FETCHED");

        OrderResponse orderResponse2 = new OrderResponse();
        orderResponse2.setOrderId("2");
        orderResponse2.setStatus("FETCHED");

        List<OrderResponse> mockOrderList = List.of(orderResponse1, orderResponse2);

//        // Mock the service method
//        when(orderService.getAllOrders()).thenReturn(Uni.createFrom().item(mockOrderList));

        // Perform the GET request
        Response response = given()
                .when().get("/api/orders")
                .then().statusCode(200)  // Check if status is OK
                .extract().response();

        List<OrderResponse> responses = response.getBody().as(new TypeRef<List<OrderResponse>>() {});
        assertNotNull(responses);
        assertEquals(2,responses.size());
        assertEquals("1",responses.get(0).getOrderId());
        assertEquals("FETCHED",responses.get(0).getStatus());

    }

    @Test
    public void testGetAllOrders_Failure(){

        // Mock the service method to throw an exception
//        when(orderService.getAllOrders()).thenReturn(Uni.createFrom().failure(new RuntimeException("Database error")));

        // Perform the GET request
        Response response = given()
                .when().get("/api/orders")
                .then().statusCode(500)  // Check if status is INTERNAL_SERVER_ERROR
                .extract().response();

        // Verify the response
        assertEquals(500, response.getStatusCode());
    }
}
