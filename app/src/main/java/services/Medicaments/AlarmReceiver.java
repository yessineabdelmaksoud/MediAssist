package services.Medicaments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.example.applicationproject.R;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "medicament_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        long medicamentId = intent.getLongExtra("medicamentId", -1);

        // Créer l'intent pour ouvrir l'application au clic sur la notification
        Intent mainIntent = new Intent(context, medicament_activity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Configurer le son de notification
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Construire la notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(alarmSound)
                .setVibrate(new long[]{0, 500, 250, 500})
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Afficher la notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());

        // Re-programmer l'alarme pour la semaine prochaine
        reprogramAlarm(context, notificationId, title, message, medicamentId);
    }

    private void reprogramAlarm(Context context, int notificationId, String title, String message, long medicamentId) {
        // Récupérer les informations du jour et de l'heure actuels
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Créer un nouvelle notification helper
        NotificationHelper notificationHelper = new NotificationHelper(context);

        // Re-programmer pour la semaine prochaine (dans 7 jours)
        calendar.add(Calendar.DAY_OF_YEAR, 7);

        // Récupérer le médicament pour vérifier si ce jour est programmé
        MedicamentDAO medicamentDAO = new MedicamentDAO(context);
        medicamentDAO.open();
        Medicament medicament = medicamentDAO.getMedicamentById(medicamentId);

        if (medicament != null) {
            // Vérifier si ce jour est programmé pour ce médicament
            String jourString = convertCalendarDayToString(dayOfWeek);
            if (medicament.getJours().contains(jourString)) {
                // Re-programmer la notification
                notificationHelper.scheduleNotification(
                        notificationId,
                        dayOfWeek,
                        hourOfDay,
                        minute,
                        title,
                        message,
                        medicamentId
                );
            }
        }
        medicamentDAO.close();
    }

    private String convertCalendarDayToString(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "Lundi";
            case Calendar.TUESDAY:
                return "Mardi";
            case Calendar.WEDNESDAY:
                return "Mercredi";
            case Calendar.THURSDAY:
                return "Jeudi";
            case Calendar.FRIDAY:
                return "Vendredi";
            case Calendar.SATURDAY:
                return "Samedi";
            case Calendar.SUNDAY:
                return "Dimanche";
            default:
                return "Lundi";
        }
    }
}