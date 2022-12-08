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
import miun.fl.dt142g.projektkok.json.Booking;
import miun.fl.dt142g.projektkok.json.CombinedOrders;
import miun.fl.dt142g.projektkok.json.CombinedOrdersAPI;
import miun.fl.dt142g.projektkok.json.Dish;
import miun.fl.dt142g.projektkok.json.Employee;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<CombinedOrders> allOrders = new ArrayList<>();
    private final ArrayList<CombinedOrders> finishedOrders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// -------------- TEMP --------------- ///
        Booking booking = new Booking();
        Booking booking2 = new Booking();
        Dish dish = new Dish();
        Dish dish2 = new Dish();
        Dish dish3 = new Dish();
        Employee employee = new Employee();
        CombinedOrders combinedOrder = new CombinedOrders();
        CombinedOrders combinedOrder2 = new CombinedOrders();
        CombinedOrders combinedOrder3 = new CombinedOrders();
            // BOOKING
            booking.setDate("2022-12-07");
            booking.setId(1);
            booking.setFirstName("Joel");
            booking.setLastName("Vigge");
            booking.setPhoneNumber("0703911803");
            booking.setNumberOfPeople(4);
            booking.setTableNumber(2);
            booking.setTime("11:30:00");
        booking2.setDate("2022-12-07");
        booking2.setId(2);
        booking2.setFirstName("Nikki");
        booking2.setLastName("Volvo");
        booking2.setPhoneNumber("0703911803");
        booking2.setNumberOfPeople(3);
        booking2.setTableNumber(4);
        booking2.setTime("12:30:00");
            // DISH
            dish.setId(231);
            dish.setName("6-pack nuggets");
            dish2.setId(205);
            dish2.setName("Reuben");
            dish3.setId(555);
            dish3.setName("Chocolate Mousse with Cherry Truffles");
            // EMPLOYEE
            employee.setEmail("alfri2005@student.miun.se");
            employee.setFirstName("Alexander");
            employee.setLastName("Frid");
            employee.setSsn("200109189933");
            // COMBINED
            combinedOrder.setBooking(booking);
            combinedOrder.setDish(dish);
            combinedOrder.setEmployee(employee);
            combinedOrder.setPrice(95);
            combinedOrder.setCategory("Efterrätt");
            combinedOrder.setDescription("A mouse dunked in chocolate, with a little cherry up top!");
            combinedOrder.setId(booking.getTableNumber());
            combinedOrder.setStatus(false);
            combinedOrder.setNotes("Notering från servitör som är lite onödigt lång");
        combinedOrder2.setBooking(booking2);
        combinedOrder2.setDish(dish2);
        combinedOrder2.setEmployee(employee);
        combinedOrder2.setPrice(100);
        combinedOrder2.setCategory("Varmrätt");
        combinedOrder2.setDescription("A pile of corned beef, slices of Swiss cheese, Franks Kraut and Thousand Island dressing grilled between two slices of bread!");
        combinedOrder2.setId(booking.getTableNumber());
        combinedOrder2.setStatus(false);
        combinedOrder2.setNotes("Alex är sur ikväll!");
        combinedOrder3.setBooking(booking2);
        combinedOrder3.setDish(dish3);
        combinedOrder3.setEmployee(employee);
        combinedOrder3.setPrice(100);
        combinedOrder3.setCategory("Varmrätt");
        combinedOrder3.setDescription("A pile of corned beef, slices of Swiss cheese, Franks Kraut and Thousand Island dressing grilled between two slices of bread!");
        combinedOrder3.setId(booking.getTableNumber());
        combinedOrder3.setStatus(false);
        combinedOrder3.setNotes("Kvällen är ung! - Joel, klockan 01:09");

        allOrders.add(combinedOrder);
        allOrders.add(combinedOrder2);
        allOrders.add(combinedOrder3);
        createOrders(allOrders);
        /// -------------- TEMP --------------- ///

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
        final int SIZE = (getResources().getDisplayMetrics().widthPixels - SIDEBAR_SIZE - MARGIN_SIZE) / ROW_SIZE;
        // PARAMETERS FOR THE Button
        TableRow.LayoutParams params = new TableRow.LayoutParams(SIZE, SIZE);
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
                    Button button = orderView.findViewById(R.id.buttonDone);
                    String title = "Bord: " + order.getBooking().getTableNumber();
                    String done = "Klar";

                    // SET
                    title_view.setText(title);
                    button.setText(done);
                    orderView.setBackgroundColor(getResources().getColor(R.color.appOrange));

                    // WRITE OUT ORDER AND NOTES
                    for(CombinedOrders orders : allOrders){
                        if(orders.getBooking().getTableNumber() == order.getBooking().getTableNumber()) {
                            TextView orderInfo = new TextView(this);
                            String textOrder = orders.getDish().getName();
                            createTextView(orderInfo, textOrder, 16);
                            linearLayout.addView(orderInfo);

                            if (orders.getNotes() != null) {
                                TextView orderNote = new TextView(this);
                                String textNote = "- " + orders.getNotes();
                                createTextView(orderNote, textNote, 12);
                                linearLayout.addView(orderNote);
                            }
                        }
                    }

                    // ADD TO ROW
                    tableRow.addView(orderView);

                    // ON BUTTON PRESS
                    button.setOnClickListener(v -> onOrderButtonPress(order));
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

        for(CombinedOrders orders : finishedOrders){
            Button button = new Button(this);
            button.setText(String.valueOf(orders.getBooking().getTableNumber()));
            button.setLayoutParams(params);
            linearLayout.addView(button);
            button.setOnClickListener(v -> {
                linearLayout.removeView(button);
                finishedOrders.remove(orders);
                allOrders.add(orders);
                createOrders(allOrders);
            });
        }
    }
}