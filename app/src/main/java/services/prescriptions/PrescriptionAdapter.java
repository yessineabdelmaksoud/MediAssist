package services.prescriptions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.R;

import java.io.File;
import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {

    private Context context;
    private List<Prescription> prescriptionList;
    private OnPrescriptionClickListener listener;

    // Interface pour gérer les clics sur les éléments
    public interface OnPrescriptionClickListener {
        void onImageClick(Prescription prescription);
        void onEditClick(Prescription prescription);
        void onDeleteClick(Prescription prescription);
        void onSaveClick(Prescription prescription);
    }

    public PrescriptionAdapter(Context context, List<Prescription> prescriptionList, OnPrescriptionClickListener listener) {
        this.context = context;
        this.prescriptionList = prescriptionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PrescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_prescription, parent, false);
        return new PrescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionViewHolder holder, int position) {
        Prescription prescription = prescriptionList.get(position);

        // Affichage des données de la prescription
        holder.prescriptionTitle.setText(prescription.getTitle());
        holder.doctorName.setText(prescription.getDoctorName());
        holder.prescriptionDate.setText(prescription.getDate());
        holder.prescriptionNote.setText(prescription.getNote());

        // Chargement de l'image si elle existe
        if (prescription.getImagePath() != null && !prescription.getImagePath().isEmpty()) {
            File imgFile = new File(prescription.getImagePath());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.prescriptionImage.setImageBitmap(myBitmap);
            } else {
                holder.prescriptionImage.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            holder.prescriptionImage.setImageResource(R.drawable.placeholder_image);
        }

        // Assignation des écouteurs d'événements
        holder.prescriptionImage.setOnClickListener(v ->
                listener.onImageClick(prescription));

        holder.editButton.setOnClickListener(v ->
                listener.onEditClick(prescription));

        holder.deleteButton.setOnClickListener(v ->
                listener.onDeleteClick(prescription));

        holder.saveButton.setOnClickListener(v ->
                listener.onSaveClick(prescription));
    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    // Méthode pour mettre à jour les données
    public void updateData(List<Prescription> newPrescriptions) {
        this.prescriptionList = newPrescriptions;
        notifyDataSetChanged();
    }

    // Classe ViewHolder pour les éléments de la liste
    public static class PrescriptionViewHolder extends RecyclerView.ViewHolder {
        TextView prescriptionTitle, doctorName, prescriptionDate, prescriptionNote;
        ImageView prescriptionImage;
        ImageButton editButton, deleteButton, saveButton;

        public PrescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            prescriptionTitle = itemView.findViewById(R.id.prescriptionTitle);
            doctorName = itemView.findViewById(R.id.doctorName);
            prescriptionDate = itemView.findViewById(R.id.prescriptionDate);
            prescriptionNote = itemView.findViewById(R.id.prescriptionNote);
            prescriptionImage = itemView.findViewById(R.id.prescriptionImage);
            editButton = itemView.findViewById(R.id.editButton1);
            deleteButton = itemView.findViewById(R.id.deleteButton1);
            saveButton = itemView.findViewById(R.id.saveButton1);
        }
    }
}