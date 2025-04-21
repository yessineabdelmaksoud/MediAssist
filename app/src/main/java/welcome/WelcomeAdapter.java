package welcome;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.R;

import java.util.List;

public class WelcomeAdapter extends RecyclerView.Adapter<WelcomeAdapter.SlideViewHolder> {
    private List<SlideItem> slides;

    public WelcomeAdapter(List<SlideItem> slides) {
        this.slides = slides;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        SlideItem item = slides.get(position);
        holder.image.setImageResource(item.getImageRes());
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description;

        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.slideImage);
            title = itemView.findViewById(R.id.slideTitle);
            description = itemView.findViewById(R.id.slideDescription);
        }
    }
}
