package services.contacts;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import services.contacts.Model.Contact;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactsList;
    private OnContactClickListener listener;

    public interface OnContactClickListener {
        void onCallButtonClick(String phoneNumber);
        void onEditButtonClick(Contact contact);
        void onDeleteButtonClick(Contact contact);
    }

    public ContactAdapter(List<Contact> contactsList, OnContactClickListener listener) {
        this.contactsList = contactsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactsList.get(position);

        holder.contactName.setText(contact.getName());
        holder.contactType.setText(contact.getType());
        holder.contactPhone.setText(contact.getPhoneNumber());

        // Set default label visibility
        if (contact.isDefault()) {
            holder.contactLabel.setVisibility(View.VISIBLE);
        } else {
            holder.contactLabel.setVisibility(View.GONE);
        }

        // Set contact image
        if (contact.getImageUri() != null) {
            try {
                holder.contactImage.setImageURI(contact.getImageUri());
            } catch (Exception e) {
                Log.e("ContactAdapter", "Error loading image: " + e.getMessage());
                holder.contactImage.setImageResource(R.drawable.default_profile);
            }
        } else {
            holder.contactImage.setImageResource(R.drawable.default_profile);
        }

        // Set call button click listener
        holder.callButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCallButtonClick(contact.getPhoneNumber());
            }
        });

        // Set edit button click listener
        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditButtonClick(contact);
            }
        });

        // Set delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteButtonClick(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    // Method to update the contact list
    public void updateContacts(List<Contact> newContacts) {
        this.contactsList.clear();
        this.contactsList.addAll(newContacts);
        notifyDataSetChanged();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        CircleImageView contactImage;
        TextView contactName;
        TextView contactType;
        TextView contactPhone;
        TextView contactLabel;
        FloatingActionButton callButton;
        FloatingActionButton editButton;
        FloatingActionButton deleteButton;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contactImage);
            contactName = itemView.findViewById(R.id.contactName);
            contactType = itemView.findViewById(R.id.contactType);
            contactPhone = itemView.findViewById(R.id.contactPhone);
            contactLabel = itemView.findViewById(R.id.contactLabel);
            callButton = itemView.findViewById(R.id.callButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}