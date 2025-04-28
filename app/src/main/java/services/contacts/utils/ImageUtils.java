package services.contacts.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    // Copy image from URI to app's private storage
    public static Uri saveImageToInternalStorage(Context context, Uri imageUri) {
        try {
            if (imageUri == null) return null;

            // Create a file in the app's private directory
            String fileName = "contact_" + UUID.randomUUID().toString() + ".jpg";
            File outputFile = new File(context.getFilesDir(), fileName);

            try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                 FileOutputStream fos = new FileOutputStream(outputFile)) {

                // Read image from input URI
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Write to app's private storage with compression
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);

                // Return URI for the saved file
                return Uri.fromFile(outputFile);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving image: " + e.getMessage());
            return null;
        }
    }
}