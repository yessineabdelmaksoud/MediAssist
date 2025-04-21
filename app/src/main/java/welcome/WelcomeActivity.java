package welcome;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.applicationproject.R;

import java.util.ArrayList;
import java.util.List;

import login.SignUpActivity;

public class WelcomeActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;
    private Button backButton, nextButton, finishButton;
    private WelcomeAdapter adapter;
    private List<SlideItem> slides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        finishButton = findViewById(R.id.finishButton);

        slides = new ArrayList<>();
        slides.add(new SlideItem(R.drawable.meds, "Medications", "Never miss a dose and take\ncontrol of your health."));
        slides.add(new SlideItem(R.drawable.prescriptions, "Prescriptions", "Start saving your health\ninformation today."));
        slides.add(new SlideItem(R.drawable.appointments, "Appointments", "Start now to never miss an\nappointment again."));
        slides.add(new SlideItem(R.drawable.finish_image, "", "Start your journey towards\nimproved health with just one\ntap.."));

        adapter = new WelcomeAdapter(slides);
        viewPager.setAdapter(adapter);
        addDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                addDots(position);
                backButton.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                nextButton.setVisibility(position == slides.size() - 1 ? View.GONE : View.VISIBLE);
                finishButton.setVisibility(position == slides.size() - 1 ? View.VISIBLE : View.GONE);
            }
        });

        backButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() > 0)
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        });

        nextButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < slides.size() - 1)
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        });

        finishButton.setOnClickListener(v -> {
            // Aller à l'écran suivant (ex: login ou dashboard)
            Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void addDots(int currentPosition) {
        dotsLayout.removeAllViews();
        for (int i = 0; i < slides.size(); i++) {
            TextView dot = new TextView(this);
            dot.setText("●");
            dot.setTextSize(18);
            dot.setTextColor(i == currentPosition ? Color.BLACK : Color.GRAY);
            dotsLayout.addView(dot);
        }
    }
}
