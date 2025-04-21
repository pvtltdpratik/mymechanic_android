package com.mba.my_mechanic.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mba.my_mechanic.R;
import com.mba.my_mechanic.models.ServiceRequest;

import java.util.List;
public class ServiceRequestAdapter extends RecyclerView.Adapter<ServiceRequestAdapter.ViewHolder> {

    private Context context;
    private List<ServiceRequest> requestList;

    public ServiceRequestAdapter(Context context, List<ServiceRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceRequest request = requestList.get(position);
        holder.tvUsername.setText(request.getUsername());
        holder.tvVehicleType.setText("Vehicle: " + request.getVehicleType());
        holder.tvProblem.setText("Problem: " + request.getProblemDescription());
        holder.tvStatus.setText("Status: " + request.getStatus());

        holder.itemView.setOnClickListener(v -> showDetailsDialog(request));
    }

    private void showDetailsDialog(ServiceRequest request) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_request_details, null);

        TextView usernameTV = dialogView.findViewById(R.id.detailUsername);
        TextView phoneTV = dialogView.findViewById(R.id.detailPhone);
        TextView locationTV = dialogView.findViewById(R.id.detailLocation);
        TextView vehicleTV = dialogView.findViewById(R.id.detailVehicle);
        TextView problemTV = dialogView.findViewById(R.id.detailProblem);
        TextView statusTV = dialogView.findViewById(R.id.detailStatus);
        Button closeBtn = dialogView.findViewById(R.id.btnClose);

        usernameTV.setText("Username: " + request.getUsername());
        phoneTV.setText("Phone: " + request.getUserPhone());
        locationTV.setText("Location: " + request.getUserLocation());
        vehicleTV.setText("Vehicle Type: " + request.getVehicleType());
        problemTV.setText("Problem: " + request.getProblemDescription());
        statusTV.setText("Status: " + request.getStatus());

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        closeBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvVehicleType, tvProblem, tvStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvVehicleType = itemView.findViewById(R.id.tvVehicleType);
            tvProblem = itemView.findViewById(R.id.tvProblem);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}