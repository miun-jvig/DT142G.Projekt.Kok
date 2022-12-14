package miun.fl.dt142g.projektkok;

import androidx.annotation.NonNull;
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
import miun.fl.dt142g.projektkok.json.Order;
import miun.fl.dt142g.projektkok.json.OrderAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverviewActivity extends AppCompatActivity {
    private final CombinedOrdersAPI COMBINED_ORDERS_API = APIClient.getClient().create(CombinedOrdersAPI.class);
    private final OrderAPI ORDER_API = APIClient.getClient().create(OrderAPI.class);
    private final String NON_SUCCESSFUL_RESPONSE = "Something went wrong.";
    private final String FAILED_DB_CONNECTION = "Network error, cannot reach database.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO - Fix so updateAllViews() runs every 10th second or so.
        updateAllViews();
    }

    /**
     * updateAllViews() retrieves all orders from the DB and creates layouts
     * getAllOrdersCreateLayouts() and then creates all all side buttons via createSideButtons().
     */
    public void updateAllViews(){
        getAllOrdersCreateLayouts();
        createSideButtons();
    }

    /**
     * Connects to database via COMBINED_ORDERS_API and retrieves information from:
     * http://89.233.229.182:8080/antons-skafferi-db-1.0-SNAPSHOT/api/orders/kitchen-and-ready
     * Note that these orders have the following attributes:
     * status = false/true (i.e. dish is not cooked yet or dish is cooked)
     * served = false (i.e. dish is not served yet)
     * The information is then sent to function createOrders to create buttons in the main view.
     */
    public void getAllOrdersCreateLayouts() {
        Call<List<CombinedOrders>> call = COMBINED_ORDERS_API.getAllNotServedOrders();

        call.enqueue(new Callback<List<CombinedOrders>>() {
            @Override
            public void onResponse(@NonNull Call<List<CombinedOrders>> call, @NonNull Response<List<CombinedOrders>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),NON_SUCCESSFUL_RESPONSE, Toast.LENGTH_LONG).show();
                    return;
                }
                List<CombinedOrders> combinedOrders = response.body();
                if(!Objects.requireNonNull(combinedOrders).isEmpty()) {
                    createOrderLayouts((ArrayList<CombinedOrders>) combinedOrders);
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<CombinedOrders>> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), FAILED_DB_CONNECTION, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Connects to database via COMBINED_ORDERS_API and retrieves information from:
     * http://89.233.229.182:8080/antons-skafferi-db-1.0-SNAPSHOT/api/orders/served
     * Note that these orders have the following attributes:
     * status = true (i.e. dish is cooked)
     * served = true (i.e. dish is served)
     * The information from the database will then create buttons for all the tables. An
     * onClickListener is also implemented for dishes on the same table.
     */
    public void createSideButtons(){
        // CALL TO DB
        Call<List<CombinedOrders>> callServed = COMBINED_ORDERS_API.getOrdersServed();
        callServed.enqueue(new Callback<List<CombinedOrders>>() {
            @Override
            public void onResponse(@NonNull Call<List<CombinedOrders>> call, @NonNull Response<List<CombinedOrders>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),NON_SUCCESSFUL_RESPONSE, Toast.LENGTH_LONG).show();
                    return;
                }
                List<CombinedOrders> combinedOrders = response.body();
                if(!Objects.requireNonNull(combinedOrders).isEmpty()) {
                    // VIEW FOR BUTTONS TO BE CREATED IN
                    LinearLayout linearLayout = findViewById(R.id.LinearLayoutMain);
                    linearLayout.removeAllViews();
                    // PARAMETERS FOR THE BUTTON
                    final int HEIGHT = 200;
                    final int MARGIN = 5;
                    final int SIZE_COMBINED_ORDERS = combinedOrders.size();
                    int orderCounter = 0;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, HEIGHT);
                    params.setMargins(0, MARGIN, 0, MARGIN);

                    // CREATES BUTTON FOR EACH TABLE DEPENDING ON NUMBER AND TIME FOR ORDER
                    for(int i = 0; i < SIZE_COMBINED_ORDERS; i++) {
                        // IF-STATEMENT TO AVOID combinedOrders.get(orderCounter) TO GO OUTSIDE BOUNDS
                        if(orderCounter < SIZE_COMBINED_ORDERS) {
                            // CURRENT ORDER
                            ArrayList<CombinedOrders> ordersOnCurrentTable = new ArrayList<>();
                            CombinedOrders currentOrder = combinedOrders.get(orderCounter);
                            // CREATE BUTTON OF CURRENT ORDER, NOTE THAT orderCounter IS INCREMENTED IF tableNumber & getTime IS SAME
                            Button button = new Button(OverviewActivity.this);
                            button.setText(String.valueOf(currentOrder.getBooking().getTableNumber()));
                            button.setLayoutParams(params);
                            linearLayout.addView(button);

                            for (CombinedOrders otherOrder : combinedOrders) {
                                if (currentOrder.getBooking().getTableNumber() == otherOrder.getBooking().getTableNumber() && currentOrder.getTime().equals(otherOrder.getTime())) {
                                    ordersOnCurrentTable.add(otherOrder);
                                    // INCREMENT orderCounter TO SKIP IN MAIN LOOP IF SEVERAL ORDERS ON SAME TABLE
                                    orderCounter++;
                                }
                            }

                            button.setOnClickListener(v -> {
                                linearLayout.removeView(button);
                                // SET STATUS + SERVED AS FALSE ON CLICK, WILL MAKE IT APPEAR ON MAIN WINDOW
                                setOrderStatusAndServed(button, ordersOnCurrentTable, false, false);
                            });
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<CombinedOrders>> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), FAILED_DB_CONNECTION, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Retrieves all orders from database via allOrders and creates layouts in the main window of
     * the application. The layouts will be clickable and will contain information about each
     * dish on the same tableNumber.
     * @param allOrders a list containing all orders in db (api/orders/kitchen + api/orders/ready)
     */
    public void createOrderLayouts(ArrayList<CombinedOrders> allOrders){
        // REMOVES ALL VIEWS TO CREATE NEW ONES
        TableLayout tableLayout = findViewById(R.id.TableLayout);
        tableLayout.removeAllViews();

        // VARIABLES
        final int ROW_SIZE = 4;
        int orderCounter = 0;
        double temp = (double) allOrders.size() / ROW_SIZE;
        /* columnSize uses Math.ceil in case value of temp is a non-integer. In this case will
         *  round up to create an additional row.
         */
        final double COLUMN_SIZE = Math.ceil(temp);
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
                    ArrayList<CombinedOrders> ordersOnCurrentTable = new ArrayList<>();
                    LinearLayout linearLayout = orderView.findViewById(R.id.LinearLayoutOrder);

                    // VARIABLES
                    CombinedOrders order = allOrders.get(orderCounter);
                    TextView title_view = orderView.findViewById(R.id.titleView);
                    String title = "Bord: " + order.getBooking().getTableNumber();

                    // SET TEXT/COLOR
                    title_view.setText(title);
                    orderView.setBackgroundColor(getResources().getColor(R.color.appGray));

                    // WRITE OUT ORDER AND NOTES
                    for(CombinedOrders currentOrder : allOrders){
                        if(currentOrder.getBooking().getTableNumber() == order.getBooking().getTableNumber() && currentOrder.getTime().equals(order.getTime())) {
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
                            ordersOnCurrentTable.add(currentOrder);
                            orderCounter++;
                        }
                    }

                    // ADD TO ROW
                    tableRow.addView(orderView);

                    // ON FIRST BUTTON PRESS, IF STATUS = TRUE, TO RIGHT MENU, IF STATUS = FALSE, MARK AS GREEN
                    if(order.getStatus()) {
                        orderView.setBackgroundColor(getResources().getColor(R.color.appGreen));
                        orderView.setOnClickListener(v -> setOrderStatusAndServed(orderView, ordersOnCurrentTable, true, true));
                    }
                    else{
                        orderView.setOnClickListener(v -> setOrderStatusAndServed(orderView, ordersOnCurrentTable, true, false));
                    }
                }
            }
            // ADD VIEW TO PARENT
            tableLayout.addView(tableRow);
        }
    }

    /**
     * Connects to database via COMBINED_ORDERS_API and puts the two booleans according to parameters.
     * http://89.233.229.182:8080/antons-skafferi-db-1.0-SNAPSHOT/api/update-status-and-served
     * @param orderView The view of the current object. Only used in main window (not side menu).
     * @param ordersOnCurrentTable A list containing orders that are on the same table where a dish has been ordered at same time.
     * @param status The status that should be set on boolean "status" in database.
     * @param served The status that should be set on boolean "served" in database.
     */
    public void setOrderStatusAndServed(View orderView, ArrayList<CombinedOrders> ordersOnCurrentTable, boolean status, boolean served){
        // PARSE INTO ORDER AND SET STATUS = TRUE
        ArrayList<Order> orderList = createOrdersFromCombinedOrders(ordersOnCurrentTable);
        for(Order tmp : orderList){
            tmp.setStatus(status);
            tmp.setServed(served);
        }

        // SEND NOTIFICATION TO DATABASE
        for(Order order : orderList) {
            Call<Order> callStatusAndServed = ORDER_API.putStatusAndServed(order);

            // CALL TO CHANGE SERVED
            callStatusAndServed.enqueue(new Callback<Order>() {
                @Override
                public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), NON_SUCCESSFUL_RESPONSE, Toast.LENGTH_LONG).show();
                    }
                    if(order == orderList.get(0)) {
                        updateAllViews();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), FAILED_DB_CONNECTION, Toast.LENGTH_LONG).show();
                }
            });
        }
        // ON SECOND BUTTON PRESS
        if(ordersOnCurrentTable.get(0).getStatus()) {
            orderView.setOnClickListener(v -> setOrderStatusAndServed(orderView, ordersOnCurrentTable, true, true));
        }
    }

    /**
     * Creates a TextView from parameters.
     * @param textView A new TextView
     * @param text The text to put into the TextView
     * @param textSize The size of the text to be put into the TextView
     */
    public void createTextView(TextView textView, String text, int textSize){
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTextColor(getResources().getColor(R.color.black));
    }

    /**
     * Creates an ArrayList of Order's from an ArrayList of CombinedOrders.
     * @param orderList An ArrayList of CombinedOrders to be created into Order's.
     * @return An ArrayList of Order's created from an ArrayList of CombinedOrders.
     */
    public ArrayList<Order> createOrdersFromCombinedOrders(ArrayList<CombinedOrders> orderList){
        ArrayList<Order> orders = new ArrayList<>();
        for(CombinedOrders combinedOrder : orderList){
            Order order = new Order();
            order.setBooking(combinedOrder.getBooking());
            order.setDish(combinedOrder.getDish());
            order.setEmployee(combinedOrder.getEmployee());
            order.setId(combinedOrder.getId());
            order.setNotes(combinedOrder.getNotes());
            order.setTime(combinedOrder.getTime());
            order.setServed(false);
            order.setStatus(false);
            orders.add(order);
        }
        return orders;
    }
}