package app.clockwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;

public class ClockWidget extends AppWidgetProvider{

    public static final String CLOCK_WIDGET_UPDATE = "app.clockwidget.ClockWidget.CLOCK_WIDGET_UPDATE";

    private PendingIntent createUpdateIntent(Context context){
        Intent intent = new Intent(CLOCK_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createUpdateIntent(context));
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);

        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
        1000, createUpdateIntent(context));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
            context.startService(new Intent(context, UpdateService.class));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        context.startService(new Intent(context, UpdateService.class));
    }

    public static class UpdateService extends Service {

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onStart(Intent intent, int startId) {
            RemoteViews updateViews = buildUpdate(this);
            ComponentName widget = new ComponentName(this, ClockWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(widget, updateViews);
        }
        private RemoteViews buildUpdate(Context context) {
            //we build view for clock widget
            RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.clock_widget);
            //clock data
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            updateViews.setTextViewText(R.id.hours, String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
            //min
            int min = c.get(Calendar.MINUTE);
            updateViews.setTextViewText(R.id.min, (min < 10 ? "0" : "") +  String.valueOf(min));


            return null;
        }
    }

}
