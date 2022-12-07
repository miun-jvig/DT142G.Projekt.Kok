package miun.fl.dt142g.projektkok.json;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * APIClient class for database.
 *
 * Typical usage.
 * <pre>
 *     ClassAPI = APIClient.getClient().create(ClassAPI.class);
 *     Call<List<Class>> call = employeeAPI.APIFunction();
 *     call.enqueue(new Callback<List<Class>>() { }...
 * </pre>
 */
public class APIClient {
    private static final String DB = "89.233.229.182";

    public static Retrofit getClient(){
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

        final Retrofit build = new Retrofit.Builder()
                .baseUrl("http://" + DB + ":8080/antons-skafferi-db-1.0-SNAPSHOT/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return build;
    }
}
