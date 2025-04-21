package services.Medication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.R;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedViewHolder> {

    private Context context;
    private List<Medication> medicationList;
    private MedicationRepository repo;
    public void setMedications(List<Medication> newMeds) {
        this.medicationList = newMeds;
        notifyDataSetChanged(); // Notifie le RecyclerView que la liste a changé
    }

    public MedicationAdapter(Context context, List<Medication> medicationList, MedicationRepository repo) {
        this.context = context;
        this.medicationList = medicationList;
        this.repo = repo;
    }

    @NonNull
    @Override
    public MedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medication, parent, false);
        return new MedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedViewHolder holder, int position) {
        Medication med = medicationList.get(position);
        holder.textMedName.setText(med.getNom());

        String details = "Posologie: " + med.getPosologie() +
                "\nFréquence: " + med.getFrequence() +
                "\nHeure: " + med.getHeure() +
                "\nRemarque: " + med.getRemarque();

        holder.textMedDetails.setText(details);

        // Charger l'image du médicament si elle existe
        String photoPath = med.getPhotoPath();
        if (photoPath != null && !photoPath.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            if (bitmap != null) {
                holder.imgMed.setVisibility(View.VISIBLE);
                holder.imgMed.setImageBitmap(bitmap);
            } else {
                holder.imgMed.setVisibility(View.GONE);
            }
        } else {
            holder.imgMed.setVisibility(View.GONE);
        }

        // Bouton Supprimer
        holder.btnDelete.setOnClickListener(v -> {
            Medication medToDelete = medicationList.get(position);
            repo.deleteMedication(medToDelete.getId());
            medicationList.remove(position);
            notifyItemRemoved(position);
        });

        // Bouton Modifier
        holder.btnEdit.setOnClickListener(v -> {
            Medication medToEdit = medicationList.get(position);
            Intent intent = new Intent(context, AddMedicationActivity.class);
            intent.putExtra("edit", true);
            intent.putExtra("id", medToEdit.getId());
            intent.putExtra("nom", medToEdit.getNom());
            intent.putExtra("posologie", medToEdit.getPosologie());
            intent.putExtra("frequence", medToEdit.getFrequence());
            intent.putExtra("heure", medToEdit.getHeure());
            intent.putExtra("remarque", medToEdit.getRemarque());
            intent.putExtra("photo_path", medToEdit.getPhotoPath()); // Ajouter le chemin de la photo
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    static class MedViewHolder extends RecyclerView.ViewHolder {
        TextView textMedName, textMedDetails;
        Button btnEdit, btnDelete;

        ImageView imgMed; // Nouvel élément


        public MedViewHolder(@NonNull View itemView) {
            super(itemView);
            textMedName = itemView.findViewById(R.id.textMedName);
            textMedDetails = itemView.findViewById(R.id.textMedDetails);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            imgMed = itemView.findViewById(R.id.imgMed); // Initialiser la vue

        }
    }
}

