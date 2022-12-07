package miun.fl.dt142g.projektkok.json;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CarteAPI {
    @GET("carte")
    Call<List<Carte>> getAllCarte();
}
