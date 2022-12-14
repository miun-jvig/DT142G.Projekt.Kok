package miun.fl.dt142g.projektkok.json;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CombinedOrdersAPI {
    @GET("orders/kitchen-and-ready")
    Call<List<CombinedOrders>> getAllNotServedOrders();

    @GET("orders/kitchen")
    Call<List<CombinedOrders>> getOrdersKitchen();

    @GET("orders/ready")
    Call<List<CombinedOrders>> getOrdersReady();

    @GET("orders/served")
    Call<List<CombinedOrders>> getOrdersServed();
}
