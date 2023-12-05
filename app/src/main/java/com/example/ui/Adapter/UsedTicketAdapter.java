package com.example.ui.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Model.TransactionModel;
import com.example.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class UsedTicketAdapter extends RecyclerView.Adapter<UsedTicketAdapter.UsedTicketViewHolder> {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public List<TransactionModel> transactionModelList;
    public Context context;

    public UsedTicketAdapter(Context context) {
        this.context = context;
        transactionModelList = new ArrayList<>();
    }

    public void setData(List<TransactionModel> transactionModelList) {
        this.transactionModelList = transactionModelList;
        notifyDataSetChanged();
    }

    public void addData(TransactionModel transactionModel) {
        transactionModelList.add(transactionModel);
        notifyDataSetChanged();
    }

    public void clearData() {
        transactionModelList.clear();
        notifyDataSetChanged();
    }

    public void removeData(String id) {
        for (int i = 0; i < transactionModelList.size(); i++) {
            if (transactionModelList.get(i).getID().equals(id)) {
                transactionModelList.remove(i);
                notifyDataSetChanged();
            }
        }
    }

    @NonNull
    @Override
    public UsedTicketAdapter.UsedTicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_item, parent, false);
        return new UsedTicketAdapter.UsedTicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsedTicketAdapter.UsedTicketViewHolder holder, int position) {
        TransactionModel transactionModel = transactionModelList.get(position);
        holder.date.setText(dateFormat.format(transactionModel.getUsedTimestamp().toDate()));
        holder.id.setText(transactionModel.getID());

        String qrCode = "THISISVMUSEUMQRCODE " + transactionModel.getNumberOfTickets() + transactionModel.getID();
        QRGEncoder qrgEncoder = new QRGEncoder(qrCode, null, QRGContents.Type.TEXT, 500);
        Bitmap bitmap = qrgEncoder.getBitmap();
        holder.qrCode.setImageBitmap(bitmap);

        holder.ticketItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ticketItem.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        holder.ticketItem.animate().scaleX(1f).scaleY(1f).setDuration(100);
                    }
                });
                Dialog dialog = new Dialog(context);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_ticket_qr);

                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                ImageView imageView = dialog.findViewById(R.id.imageViewMap);
                Bitmap bitmap = qrgEncoder.getBitmap();
                imageView.setImageBitmap(bitmap);

                // Blur the background
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.dimAmount = 0.8f; // Adjust the amount of blur by changing this value
                    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    window.setAttributes(params);
                }

                // Show the dialog
                dialog.show();

                // Set an onTouchListener for the dialog's window to dismiss it when touched outside
                dialog.getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Dismiss the dialog when touched outside the dialog bounds
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            float x = event.getX();
                            float y = event.getY();
                            Rect dialogBounds = new Rect();
                            dialog.getWindow().getDecorView().getHitRect(dialogBounds);
                            if (!dialogBounds.contains((int) x, (int) y)) {
                                dialog.dismiss();
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (transactionModelList != null) return transactionModelList.size();
        return 0;
    }

    public class UsedTicketViewHolder extends RecyclerView.ViewHolder {
        TextView date, id;
        ImageView qrCode;
        LinearLayout ticketItem;

        public UsedTicketViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            id = itemView.findViewById(R.id.transactionID);
            qrCode = itemView.findViewById(R.id.qrCode);
            ticketItem = itemView.findViewById(R.id.ticket_item);
        }
    }
}
