package com.zimblesystems.resources;

import com.zimblesystems.dto.OrderRequest;
import com.zimblesystems.services.OrderService;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/orders")
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderService orderService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> createOrder(@Valid OrderRequest orderRequest) {

        try{
            return orderService.createOrder(orderRequest).onItem()
                    .transform(order ->  Response.ok(order)
                            .build());
        }catch (Exception e) {
            Log.error("Error in POST /api/orders e: " + e.getMessage());
            return Uni.createFrom().item(Response.status(Response.Status.INTERNAL_SERVER_ERROR)

                    .build());
        }
    }

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getAllOrders() {

        try {
            return orderService.getAllOrders().onItem()
                    .transform(response -> Response.ok(response).build());
        }catch (Exception e) {
            Log.error("Error in GET /api/orders e: " + e.getMessage());
            return Uni.createFrom().item(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }
}
