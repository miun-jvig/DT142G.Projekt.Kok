package miun.fl.dt142g.projektkok;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// -------------- TEMP --------------- ///
        Booking booking = new Booking();
        Dish dish = new Dish();
        Employee employee = new Employee();
        CombinedOrders combinedOrder = new CombinedOrders();
            // BOOKING
            booking.setDate("2022-12-07");
            booking.setId(1);
            booking.setFirstName("Joel");
            booking.setLastName("Vigge");
            booking.setPhoneNumber("0703911803");
            booking.setNumberOfPeople(4);
            booking.setTableNumber(2);
            booking.setTime("11:30:00");
            // DISH
            dish.setId(231);
            dish.setName("6-pack nuggets");
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
            combinedOrder.setNotes("Notering från servitör");
        allOrders.add(combinedOrder);
        allOrders.add(combinedOrder);
        allOrders.add(combinedOrder);
        allOrders.add(combinedOrder);
        allOrders.add(combinedOrder);
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
        final int SIDEBAR_SIZE = (int) getResources().getDimension(R.dimen.sidebar);
        final int SIZE = (getResources().getDisplayMetrics().widthPixels - SIDEBAR_SIZE) / ROW_SIZE;
        final int TEXT_SIZE = 12;
        final int TEXT_PADDING = SIZE/ROW_SIZE;
        // PARAMETERS FOR THE Button
        TableRow.LayoutParams params = new TableRow.LayoutParams(SIZE, SIZE);

        // CREATE ROW
        for(int i = 0; i < COLUMN_SIZE; i++){
            TableRow tableRow = new TableRow(this);
            // CREATE LAYOUT IN ROW
            for(int j = 0; j < ROW_SIZE; j++) {
                if(orderCounter < allOrders.size()) {
                    // INFLATER + VIEW
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View orderView = inflater.inflate(R.layout.activity_order, null);

                    // VARIABLES
                    CombinedOrders order = allOrders.get(orderCounter++);
                    String title = "Bord: " + order.getBooking().getTableNumber() + " " + order.getEmployee().getFirstName();
                    String done = "Klar";
                    int color = getResources().getColor(R.color.appOrange);

                    // CREATES TITLE
                    TextView title_view = orderView.findViewById(R.id.titleView);
                    title_view.setText(title);

                    // CREATES BUTTON
                    Button button = orderView.findViewById(R.id.button_done);
                    button.setText(done);

                    // ADD TO ROW
                    tableRow.addView(orderView);

                    // ON BUTTON PRESS
                    button.setOnClickListener(v -> onOrderButtonPress(button));
                }
            }
            tableLayout.addView(tableRow);
        }
    }

    public void onOrderButtonPress(Button button){
        LinearLayout linearLayout = findViewById(R.id.LinearLayout);
        // REMOVES ALL BUTTONS ON CALL TO MAKE DROPDOWN MENU WORK CORRECT
        linearLayout.removeAllViews();

        // PARAMETERS FOR THE Button
        final int WIDTH = 100;
        final int HEIGHT = 100;
        final int TEXT_SIZE = 5;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WIDTH, HEIGHT);
        params.setMargins(0, 5, 0, 5);


    }
}