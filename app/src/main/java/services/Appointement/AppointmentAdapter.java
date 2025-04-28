package services.Appointement;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.R;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private Context context;
    private AppointmentDbHelper dbHelper;
    private OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onAppointmentDeleted();
        void onAppointmentEdit(Appointment appointment);
    }

    public AppointmentAdapter(Context context, List<Appointment> appointmentList, OnAppointmentActionListener listener) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.dbHelper = new AppointmentDbHelper(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        holder.appointmentTitle.setText(appointment.getTitle());
        holder.appointmentDoctor.setText(appointment.getDoctor());
        holder.appointmentDate.setText(appointment.getDate());
        holder.appointmentTime.setText(appointment.getTime());
        holder.appointmentPlace.setText(appointment.getPlace());

        // Handle edit button click
        holder.editButton.setOnClickListener(v -> {
            listener.onAppointmentEdit(appointment);
        });

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            dbHelper.deleteAppointment(appointment.getId());
            appointmentList.remove(position);
            notifyDataSetChanged();
            listener.onAppointmentDeleted();
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public void updateAppointmentList(List<Appointment> appointments) {
        this.appointmentList = appointments;
        notifyDataSetChanged();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView appointmentTitle, appointmentDoctor, appointmentDate, appointmentTime, appointmentPlace;
        ImageButton editButton, deleteButton;

        AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            appointmentTitle = itemView.findViewById(R.id.appointmentTitle);
            appointmentDoctor = itemView.findViewById(R.id.appointmentdoctorName);
            appointmentDate = itemView.findViewById(R.id.appointmentDate);
            appointmentTime = itemView.findViewById(R.id.appointmentclock);
            appointmentPlace = itemView.findViewById(R.id.appointmentplace);
            editButton = itemView.findViewById(R.id.appointmenteditButton);
            deleteButton = itemView.findViewById(R.id.appointmentdeleteButton);
        }
    }
}