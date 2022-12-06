package miun.fl.dt142g.projektkok;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import java.util.List;
import java.util.Objects;

import miun.fl.dt142g.projektkok.json.APIClient;
import miun.fl.dt142g.projektkok.json.CombinedOrders;
import miun.fl.dt142g.projektkok.json.CombinedOrdersAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CombinedOrdersAPI combinedOrdersAPI = APIClient.getClient().create(CombinedOrdersAPI.class);
        Call<List<CombinedOrders>> call = combinedOrdersAPI.getOrders();
        call.enqueue(new Callback<List<CombinedOrders>>() {
            @Override
            public void onResponse(Call<List<CombinedOrders>> call, Response<List<CombinedOrders>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"Helvete!" , Toast.LENGTH_LONG).show();
                    return;
                }
                List<CombinedOrders> combinedOrders = response.body();
                if(!Objects.requireNonNull(combinedOrders).isEmpty()) {
                    createOrders();
                }
            }
            @Override
            public void onFailure(Call<List<CombinedOrders>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Network error, cannot reach DB." , Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createOrders() {

    }
}