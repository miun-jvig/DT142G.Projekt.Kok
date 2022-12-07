package miun.fl.dt142g.projektkok.json;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CombinedOrdersAPI {
    @GET("orders/kitchen")
    Call<List<CombinedOrders>> getOrders();
}
