package services.Medicaments;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.R;

import java.util.ArrayList;
import java.util.List;

public class MedicamentAdapter extends RecyclerView.Adapter<MedicamentAdapter.ViewHolder> implements Filterable {

    private List<Medicament> medicamentList;
    private List<Medicament> medicamentListFull;
    private Context context;
    private OnItemClickListener listener;

    // Interface pour gérer les clics
    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MedicamentAdapter(Context context, List<Medicament> medicamentList) {
        this.context = context;
        this.medicamentList = medicamentList;
        this.medicamentListFull = new ArrayList<>(medicamentList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicaments, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicament medicament = medicamentList.get(position);

        // Définir les données du médicament
        holder.textViewNom.setText(medicament.getNom());
        holder.textViewPosologie.setText("Posologie : " + medicament.getPosologie());
        holder.textViewFrequence.setText("Fréquence : " + medicament.getFrequence());
        holder.textViewHeures.setText("Heures : " + medicament.getHeuresAsString());
        holder.textViewJours.setText("Jours : " + medicament.getJoursAsString());

        if (medicament.getRemarque() != null && !medicament.getRemarque().isEmpty()) {
            holder.textViewRemarque.setText("Remarque : " + medicament.getRemarque());
            holder.textViewRemarque.setVisibility(View.VISIBLE);
        } else {
            holder.textViewRemarque.setVisibility(View.GONE);
        }

        // Charger l'image si disponible
        if (medicament.getImagePath() != null && !medicament.getImagePath().isEmpty()) {
            holder.imageMedicament.setVisibility(View.VISIBLE);
            holder.imageMedicament.setImageURI(Uri.parse(medicament.getImagePath()));
        } else {
            holder.imageMedicament.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return medicamentList.size();
    }

    public void updateData(List<Medicament> newMedicaments) {
        medicamentList = newMedicaments;
        medicamentListFull = new ArrayList<>(newMedicaments);
        notifyDataSetChanged();
    }

    public Medicament getItem(int position) {
        return medicamentList.get(position);
    }

    @Override
    public Filter getFilter() {
        return medicamentFilter;
    }

    private Filter medicamentFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Medicament> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(medicamentListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Medicament medicament : medicamentListFull) {
                    if (medicament.getNom().toLowerCase().contains(filterPattern)) {
                        filteredList.add(medicament);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            medicamentList.clear();
            medicamentList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageMedicament;
        public TextView textViewNom;
        public TextView textViewPosologie;
        public TextView textViewFrequence;
        public TextView textViewHeures;
        public TextView textViewJours;
        public TextView textViewRemarque;
        public ImageButton btnEdit;
        public ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageMedicament = itemView.findViewById(R.id.imageMedicamentSaved);
            textViewNom = itemView.findViewById(R.id.textViewNomSaved);
            textViewPosologie = itemView.findViewById(R.id.textViewPosologieSaved);
            textViewFrequence = itemView.findViewById(R.id.textViewFrequenceSaved);
            textViewHeures = itemView.findViewById(R.id.textViewHeuresSaved);
            textViewJours = itemView.findViewById(R.id.textViewJoursSaved);
            textViewRemarque = itemView.findViewById(R.id.textViewRemarqueSaved);
            btnEdit = itemView.findViewById(R.id.btnEditMedicament);
            btnDelete = itemView.findViewById(R.id.btnDeleteMedicament);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(position);
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}
