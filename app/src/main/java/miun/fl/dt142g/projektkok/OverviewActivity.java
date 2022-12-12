package miun.fl.dt142g.projektkok;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import miun.fl.dt142g.projektkok.json.APIClient;
import miun.fl.dt142g.projektkok.json.CombinedOrders;
import miun.fl.dt142g.projektkok.json.CombinedOrdersAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverviewActivity extends AppCompatActivity {
    private final ArrayList<CombinedOrders> allOrders = new ArrayList<>();
    private final ArrayList<CombinedOrders> finishedOrders = new ArrayList<>();

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
                    allOrders.addAll(combinedOrders);
                    createOrders(allOrders);
                }
            }
            @Override
            public void onFailure(Call<List<CombinedOrders>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Network error, cannot reach DB." , Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createOrders(ArrayList<CombinedOrders> allOrders) {
        TableLayout tableLayout = findViewById(R.id.TableLayout);
        // REMOVES ALL BUTTONS ON CALL TO MAKE DROPDOWN MENU WORK CORRECT
        tableLayout.removeAllViews();

        final int ROW_SIZE = 4;
        double temp = (double) allOrders.size() / ROW_SIZE;
        /* columnSize uses Math.ceil in case value of temp is a non-integer. In this case will
         *  round up to create an additional row.
         */
        final double COLUMN_SIZE = Math.ceil(temp);
        int orderCounter = 0;
        // WIDTH = FOUR ITEMS
        final int MARGIN = (int) getResources().getDimension(R.dimen.margin);
        final int MARGIN_SIZE = MARGIN * ROW_SIZE * 2;
        final int SIDEBAR_SIZE = (int) getResources().getDimension(R.dimen.sidebar);
        final int HEIGHT = (getResources().getDisplayMetrics().widthPixels - SIDEBAR_SIZE - MARGIN_SIZE) / ROW_SIZE;
        final int WIDTH = (int) (HEIGHT * 1.3);
        // PARAMETERS FOR THE Button
        TableRow.LayoutParams params = new TableRow.LayoutParams(HEIGHT, WIDTH);
        params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);

        // CREATE ROW
        for(int i = 0; i < COLUMN_SIZE; i++){
            TableRow tableRow = new TableRow(this);
            // CREATE LAYOUT IN ROW
            for(int j = 0; j < ROW_SIZE; j++) {
                if(orderCounter < allOrders.size()) {
                    // INFLATER + VIEW
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    @SuppressLint("InflateParams") View orderView = inflater.inflate(R.layout.activity_order, null);
                    orderView.setLayoutParams(params);
                    LinearLayout linearLayout = orderView.findViewById(R.id.LinearLayoutOrder);

                    // VARIABLES
                    CombinedOrders order = allOrders.get(orderCounter++);
                    TextView title_view = orderView.findViewById(R.id.titleView);
                    String title = "Bord: " + order.getBooking().getTableNumber();

                    // SET
                    title_view.setText(title);
                    orderView.setBackgroundColor(getResources().getColor(R.color.appGray));

                    // WRITE OUT ORDER AND NOTES
                    for(CombinedOrders currentOrder : allOrders){
                        if(currentOrder.getBooking().getTableNumber() == order.getBooking().getTableNumber()) {
                            TextView orderInfo = new TextView(this);
                            String textOrder = currentOrder.getDish().getName();
                            createTextView(orderInfo, textOrder, 16);
                            linearLayout.addView(orderInfo);

                            if (currentOrder.getNotes() != null) {
                                TextView orderNote = new TextView(this);
                                String textNote = "- " + currentOrder.getNotes();
                                createTextView(orderNote, textNote, 12);
                                linearLayout.addView(orderNote);
                            }
                        }
                    }

                    // ADD TO ROW
                    tableRow.addView(orderView);

                    // ON FIRST BUTTON PRESS
                    orderView.setOnClickListener(v -> onOrderMarkStatusAsTrue(orderView, order));
                }
            }
            tableLayout.addView(tableRow);
        }
    }

    public void createTextView(TextView textView, String text, int textSize){
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTextColor(getResources().getColor(R.color.black));
    }

    public void onOrderMarkStatusAsTrue(View orderView, CombinedOrders order){
        // SET BACKGROUND COLOR
        orderView.setBackgroundColor(getResources().getColor(R.color.appGreen));

        // SEND NOTIFICATION
        // to be created

        // ON SECOND BUTTON PRESS
        orderView.setOnClickListener(v -> onOrderButtonPress(order));
    }

    public void onOrderButtonPress(CombinedOrders order){
        finishedOrders.add(order);
        allOrders.remove(order);
        createOrders(allOrders);
        LinearLayout linearLayout = findViewById(R.id.LinearLayoutMain);
        // REMOVES ALL BUTTONS ON CALL TO MAKE DROPDOWN MENU WORK CORRECT
        linearLayout.removeAllViews();

        // PARAMETERS FOR THE Button
        final int HEIGHT = 200;
        final int MARGIN = 5;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, HEIGHT);
        params.setMargins(0, MARGIN, 0, MARGIN);

        for(CombinedOrders currentOrder : finishedOrders){
            Button button = new Button(this);
            button.setText(String.valueOf(currentOrder.getBooking().getTableNumber()));
            button.setLayoutParams(params);
            linearLayout.addView(button);
            button.setOnClickListener(v -> {
                linearLayout.removeView(button);
                finishedOrders.remove(currentOrder);
                allOrders.add(0, currentOrder);
                createOrders(allOrders);
            });
        }
    }
}