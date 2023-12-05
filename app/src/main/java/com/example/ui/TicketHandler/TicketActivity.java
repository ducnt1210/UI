package com.example.ui.TicketHandler;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.MainActivity;
import com.example.ui.Model.CreateOrder;
import com.example.ui.Model.PriorityModel;
import com.example.ui.Model.TicketModel;
import com.example.ui.Model.TransactionModel;
import com.example.ui.SettingPackage.CheckPriorityActivity;
import com.example.ui.Util.NumberTextWatcherForThousand;
import com.example.ui.databinding.ActivityTicketBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class TicketActivity extends AppCompatActivity {
    ActivityTicketBinding binding;
    Calendar calendar;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    SweetAlertDialog sweetAlertDialog;

    PriorityModel priorityModel;
    String uid = MainActivity.currentUser.getId();
    long discount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Loading");
        sweetAlertDialog.setCancelable(false);

        binding = ActivityTicketBinding.inflate(getLayoutInflater());
        binding.numberPicker.setMinValue(1);
        binding.numberPicker.setMaxValue(100);

        getDiscount();

        // Zalo pay
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        binding.numberPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            String price = NumberTextWatcherForThousand.trimCommaOfString(binding.pricePerTicket.getText().toString());
            binding.totalPrice.setText(NumberTextWatcherForThousand.getDecimalFormattedString(String.valueOf((i1 * Integer.parseInt(price) + discount))));
            binding.numberOfTickets.setText(String.valueOf(i1));
        });

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        // End Zalo pay
        getDateInput();
        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.show();
                requestZalo();
            }
        });
        binding.tickerPriceDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TicketActivity.this, CheckPriorityActivity.class);
                startActivity(intent);
            }
        });
        setContentView(binding.getRoot());
    }

    private void getDiscount() {
        FirebaseFirestore.getInstance().collection("Priority").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                priorityModel = documentSnapshot.toObject(PriorityModel.class);
                String priority = priorityModel.getPriority();
                if (priorityModel.isVerified()) {
                    if (priority.equals("Học sinh")) {
                        discount = -30000;
                        binding.totalPrice.setText("10,000");
                    } else {
                        discount = -20000;
                        binding.totalPrice.setText("20,000");
                    }
                    binding.discountLayout.setVisibility(View.VISIBLE);
                    binding.line.setVisibility(View.VISIBLE);
                } else {
                    binding.discountLayout.setVisibility(View.GONE);
                    binding.line.setVisibility(View.GONE);
                }
                binding.discount.setText(NumberTextWatcherForThousand.getDecimalFormattedString(String.valueOf(discount)));
            } else {
                binding.discountLayout.setVisibility(View.GONE);
                binding.line.setVisibility(View.GONE);
            }
        });
    }

    private void requestZalo() {
        CreateOrder orderApi = new CreateOrder();

        try {
            JSONObject data = orderApi.createOrder(NumberTextWatcherForThousand.trimCommaOfString(binding.totalPrice.getText().toString()));
            Log.d("Amount", NumberTextWatcherForThousand.trimCommaOfString(binding.totalPrice.getText().toString()));
            String code = data.getString("return_code");

            if (code.equals("1")) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                String token = data.getString("zp_trans_token");
                ZaloPaySDK.getInstance().payOrder(TicketActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        Log.d("ZaloPayment", "Payment complete " + NumberTextWatcherForThousand.trimCommaOfString(binding.totalPrice.getText().toString()));

                        String id = FirebaseFirestore.getInstance().collection("Transaction").document().getId();
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis() / 1000, 0);
                        Timestamp usedTimestamp = new Timestamp(System.currentTimeMillis() / 1000, 0);
                        try {
                            usedTimestamp = new Timestamp(dateFormat.parse(binding.dateET.getText().toString()).getTime() / 1000, 0);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        long numberOfTickets = Long.parseLong(binding.numberOfTickets.getText().toString());
                        long amount = Long.parseLong(NumberTextWatcherForThousand.trimCommaOfString(binding.totalPrice.getText().toString()));
                        String userID = MainActivity.currentUser.getId();
                        TransactionModel transactionModel = new TransactionModel(id, timestamp, usedTimestamp, numberOfTickets, token, amount, false, userID);
                        DocumentReference transactionRef = FirebaseFirestore.getInstance().collection("Transaction").document(id);
                        batch.set(transactionRef, transactionModel);

                        for (int i = 0; i < numberOfTickets; i++) {
                            String ticketID = FirebaseFirestore.getInstance().collection("Ticket").document().getId();
                            long price = Long.parseLong(NumberTextWatcherForThousand.trimCommaOfString(binding.pricePerTicket.getText().toString()));
                            TicketModel ticketModel = new TicketModel(ticketID, timestamp, usedTimestamp, price, userID, id);
                            DocumentReference ticketRef = FirebaseFirestore.getInstance().collection("Ticket").document(ticketID);
                            batch.set(ticketRef, ticketModel);
                        }
                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("ZaloPayment", "Add transaction and ticket to database");
                                        sweetAlertDialog.dismiss();
                                    }
                                }).addOnFailureListener(e -> {
                                    Log.d("ZaloPayment", "Add transaction and ticket to database failed");
                                    sweetAlertDialog.dismiss();
                                });

                        Intent intent = new Intent(TicketActivity.this, SuccessPaymentActivity.class);
                        intent.putExtra("amount", binding.totalPrice.getText().toString());
                        intent.putExtra("transactionID", id);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        Log.d("ZaloPayment", "Payment cancel");
                        sweetAlertDialog.dismiss();
                        Toast.makeText(TicketActivity.this, "Payment cancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Log.d("ZaloPayment", "Payment error");
                        sweetAlertDialog.dismiss();
                        Toast.makeText(TicketActivity.this, "Payment error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDateInput() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        calendar = Calendar.getInstance();
        binding.dateET.setText(dateFormat.format(calendar.getTime()));
        binding.dateReceipt.setText(dateFormat.format(calendar.getTime()));

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateDate();
            }

            private void updateDate() {
                binding.dateET.setText(dateFormat.format((calendar.getTime())));
                binding.dateReceipt.setText(dateFormat.format((calendar.getTime())));
            }
        };
        binding.dateET.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new DatePickerDialog(TicketActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        this.overridePendingTransition(R.anim.animate_zoom_exit, R.anim.animate_zoom_enter);
//    }
}