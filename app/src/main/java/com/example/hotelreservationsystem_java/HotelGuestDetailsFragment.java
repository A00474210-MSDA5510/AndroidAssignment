package com.example.hotelreservationsystem_java;

import static androidx.core.content.ContextCompat.getSystemService;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;



import android.widget.Button;

import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;


public class HotelGuestDetailsFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.hotel_guest_details_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHotelDetails();
        setupGuestInputFields();
        setupSubmitButton();
    }

    private void setupHotelDetails() {
        TextView hotelRecapTextView = view.findViewById(R.id.hotel_recap_text_view);
        String hotelName = getArguments().getString("hotel name");
        String hotelPrice = getArguments().getString("hotel price");
        String hotelAvailability = getArguments().getString("hotel availability");
        int numberOfGuests = Integer.parseInt(getArguments().getString("number of guests"));
        String checkInDate = getArguments().getString("check in date");
        String checkOutDate = getArguments().getString("check out date");

        hotelRecapTextView.setText("Selected Hotel: " + hotelName + "\nCost: $" + hotelPrice +
                 "\n Number of Guest: " + numberOfGuests +
                "\nCheck in Date: " + checkInDate + "\nCheck out date: " + checkOutDate);
    }

    private void setupGuestInputFields() {
        LinearLayout guestContainer = view.findViewById(R.id.container_for_guests);
        int numberOfGuests = Integer.parseInt(getArguments().getString("number of guests"));

        for (int i = 0; i < numberOfGuests; i++) {
            LinearLayout guestDetails = new LinearLayout(getActivity());
            guestDetails.setOrientation(LinearLayout.HORIZONTAL);
            guestDetails.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            EditText guestName = new EditText(getActivity());
            guestName.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            guestName.setHint("Guest " + (i + 1) + " Name");
            guestDetails.addView(guestName);

            EditText guestGender = new EditText(getActivity());
            guestGender.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            guestGender.setHint("Guest " + (i + 1) + " Gender");
            guestDetails.addView(guestGender);

            guestContainer.addView(guestDetails);
        }
    }

    private void setupSubmitButton() {
        Button submitButton = view.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitGuestDetails();
            }
        });
    }

    private void submitGuestDetails() {
        LinearLayout guestContainer = view.findViewById(R.id.container_for_guests);
        int childCount = guestContainer.getChildCount();
        List<Guest> guests = new ArrayList<>();

        for (int i = 0; i < childCount; i++) {
            View guestView = guestContainer.getChildAt(i);
            if (guestView instanceof LinearLayout) {
                LinearLayout guestDetailsLayout = (LinearLayout) guestView;
                EditText nameEditText = (EditText) guestDetailsLayout.getChildAt(0);  // Assuming name is the first EditText
                EditText genderEditText = (EditText) guestDetailsLayout.getChildAt(1); // Assuming gender is the second EditText

                String guestName = nameEditText.getText().toString().trim();
                String guestGender = genderEditText.getText().toString().trim();

                Guest guest = new Guest(guestName, guestGender);
                guests.add(guest);
            }
        }

        // Assuming you want to do something with the guests list now, like an API call
        ApiRequestBody requestBody = new ApiRequestBody(
                getArguments().getString("hotel name"),
                getArguments().getString("check in date"),
                getArguments().getString("check out date"),
                guests
        );

        ApiInterface apiInterface = Api.getClient();
        apiInterface.postData(requestBody, new Callback<HotelRespond>() {
            @Override
            public void success(HotelRespond hotelRespond, retrofit.client.Response response) {
                if (hotelRespond != null && hotelRespond.getHash() != null) {
                    Toast.makeText(getActivity(), "Received hash: " + hotelRespond.getHash(), Toast.LENGTH_LONG).show();
                    // Generate the barcode from the hash
                    Toast.makeText(getActivity(), "Succuess to generate barcode.", Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("hash", hotelRespond.getHash());

                    BarCodeDisplayFragment barCodeDisplayFragment = new BarCodeDisplayFragment();
                    barCodeDisplayFragment.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(HotelGuestDetailsFragment.this);
                    fragmentTransaction.replace(R.id.main_layout, barCodeDisplayFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commitAllowingStateLoss();

                } else if (hotelRespond.getError() != null) {
                    Toast.makeText(getActivity(), "Error: " + hotelRespond.getError(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Unknown response from server", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Request failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    // Additional methods like generateBarcode would go here


}
