package miun.fl.dt142g.projektkok.json;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public interface OrderAPI {
    @PUT("orders/kitchen")
    Call<Order> putStatus(@Body Order order);

    @PUT("orders/served")
    Call<Order> putServed(@Body Order order);

    @PUT("orders/update-status-and-served")
    Call<Order> putStatusAndServed(@Body Order order);
}