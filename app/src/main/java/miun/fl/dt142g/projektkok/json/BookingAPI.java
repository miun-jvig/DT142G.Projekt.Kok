package miun.fl.dt142g.projektkok.json;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface BookingAPI {
    @GET("booking")
    Call<List<Booking>> getAllBooking();
}
