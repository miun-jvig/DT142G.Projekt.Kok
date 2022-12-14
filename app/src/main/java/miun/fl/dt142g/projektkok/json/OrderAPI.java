package miun.fl.dt142g.projektkok.json;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public interface OrderAPI {
    @PUT("orders/update-status-and-served")
    Call<Order> putStatusAndServed(@Body Order order);
}