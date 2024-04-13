package com.example.hotelreservationsystem_java;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import java.util.Locale;
import android.app.DatePickerDialog;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import android.graphics.Color;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.qrcode.QRCodeWriter;

public class HotelSearchFragment extends Fragment {

    // These are the global variables
    View view;
    ConstraintLayout mainLayout;
    TextView titleTextView, searchTextConfirmationTextView;
    EditText guestsCountEditText, nameEditText;
    Button confirmSearchButton, searchButton, retrieveButton, clearButton;
    DatePicker checkInDatePicker, checkOutDatePicker;

    ImageView barcodeImageView;
    String  numberOfGuests, guestName;


    private EditText checkInEditText;
    private EditText checkOutEditText;

    private Calendar checkInDate;
    private Calendar checkOutDate;




    // Declaration of shared preferences keys
    SharedPreferences sharedPreferences;
    public static final String myPreference = "myPref";
    public static final String name = "nameKey";
    public static final String guestsCount = "guestsCount";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.hotel_search_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainLayout = view.findViewById(R.id.main_layout);
        titleTextView = view.findViewById(R.id.title_text_view);
        searchTextConfirmationTextView = view.findViewById(R.id.search_confirm_text_view);


        guestsCountEditText = view.findViewById(R.id.guests_count_edit_text);

        //For Shared Pref Demo

        searchButton = view.findViewById(R.id.search_button);


        barcodeImageView = view.findViewById(R.id.barcodeImageView);

        //set Title Text
        titleTextView.setText(R.string.welcome_text);


        checkInEditText = view.findViewById(R.id.checkin_edit_text);
        checkOutEditText = view.findViewById(R.id.checkout_edit_text);

        checkInEditText.setOnClickListener(v -> showDatePickerDialog(checkInEditText, true));
        checkOutEditText.setOnClickListener(v -> showDatePickerDialog(checkOutEditText, false));






        //Set up the text of confirm text box

        //Search Button click Listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean isDateValid = isDateRangeValid(checkInDate, checkOutDate);
                if(isNumeric(guestsCountEditText.getText().toString())) {
                    numberOfGuests = guestsCountEditText.getText().toString();
                    if (isDateValid && Integer.parseInt(numberOfGuests) < 10) {

                        Bundle bundle = new Bundle();
                        bundle.putString("check in date", getDateFromCalendar(checkInDate));
                        bundle.putString("check out date", getDateFromCalendar(checkOutDate));
                        bundle.putString("number of guests", numberOfGuests);

                        HotelsListFragment hotelsListFragment = new HotelsListFragment();
                        hotelsListFragment.setArguments(bundle);

                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_layout, hotelsListFragment);
                        fragmentTransaction.remove(HotelSearchFragment.this);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if (!isDateValid) {
                        searchTextConfirmationTextView.setText("Please input a valid date");
                    } else if (Integer.parseInt(numberOfGuests) > 10) {
                        searchTextConfirmationTextView.setText("Cannot handle more than 10 person");
                    } else {
                        searchTextConfirmationTextView.setText("unknown date related error");
                    }
                } else{
                    searchTextConfirmationTextView.setText("Please enter a valid number");
                }

            }
        });



        //Clear Button Click Listener

    }

    // Function to get the date object


    private String getDateFromCalendar(Calendar date) {
        // Use the date here
        // Example: Log the selected date or process it further
        SimpleDateFormat logFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        System.out.println("Selected date: " + logFormat.format(date.getTime()));
        return logFormat.format(date.getTime());
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isDateRangeValid(Calendar startDateString, Calendar endDateString) {
        if(startDateString == null){
            return false;
        }
        if(endDateString == null){
            return false;
        }
        try {
            // Parse start and end date strings to LocalDate objects
            LocalDate startDate = LocalDate.parse(getDateFromCalendar(startDateString), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate endDate = LocalDate.parse(getDateFromCalendar(endDateString), DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            // Check if start date is before or equal to end date
            return !endDate.isBefore(startDate);
        } catch (DateTimeParseException e) {
            // Handle parsing errors (invalid date format)
            e.printStackTrace(); // Or log the error
            return false; // Consider invalid if parsing fails
        } catch (NullPointerException e) {
            // Handle null input strings
            e.printStackTrace(); // Or log the error
            return false; // Consider invalid if either start or end date is null
        }
    }

    private void showDatePickerDialog(final EditText editText, boolean isCheckIn) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            editText.setText(dateFormat.format(calendar.getTime()));

            // Store the date in the appropriate variable
            if (isCheckIn) {
                checkInDate = (Calendar) calendar.clone();
            } else {
                checkOutDate = (Calendar) calendar.clone();
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

}