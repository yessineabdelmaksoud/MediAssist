package services.prescriptions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applicationproject.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class FullScreenImageActivity extends AppCompatActivity {

    private PhotoView photoView;
    private ImageButton closeButton;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plein_decran);

        photoView = findViewById(R.id.fullScreenImageView);
        closeButton = findViewById(R.id.closeButton);

        imagePath = getIntent().getStringExtra("imagePath");
        if (imagePath != null && !imagePath.isEmpty()) {
            loadImage();
        }

        closeButton.setOnClickListener(v -> finish());
    }

    private void loadImage() {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            photoView.setImageBitmap(myBitmap);
        }
    }
}