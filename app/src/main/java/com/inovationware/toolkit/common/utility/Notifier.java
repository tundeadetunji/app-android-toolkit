package com.inovationware.toolkit.common.utility;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notifier {
    private Context context;
    private TextInfo textInfo;
    private NotificationInfo notificationInfo;
    private DrawableInfo drawableInfo;
    private ChannelInfo channelInfo;

    private Notifier(Context context, TextInfo textInfo, NotificationInfo notificationInfo, DrawableInfo drawableInfo, ChannelInfo channelInfo) {
        this.textInfo = textInfo;
        this.notificationInfo = notificationInfo;
        this.channelInfo = channelInfo;
        this.drawableInfo = drawableInfo;
        this.context = context;
    }

    public static class NotifierBuilder {
        private Context context;
        private TextInfo textInfo;
        private NotificationInfo notificationInfo;
        private DrawableInfo drawableInfo;
        private ChannelInfo channelInfo;

        public NotifierBuilder withContext(Context context) {
            this.context = context;
            this.drawableInfo = drawableInfo;
            this.channelInfo = channelInfo;
            return this;
        }

        public NotifierBuilder withTextInfo(TextInfo textInfo) {
            this.textInfo = textInfo;
            return this;
        }

        public NotifierBuilder withNotificationInfo(NotificationInfo notificationInfo) {
            this.notificationInfo = notificationInfo;
            return this;
        }

        public NotifierBuilder withDrawableInfo(DrawableInfo drawableInfo) {
            this.drawableInfo = drawableInfo;
            return this;
        }

        public NotifierBuilder withChannelInfo(ChannelInfo channelInfo) {
            this.channelInfo = channelInfo;
            return this;
        }

        public Notifier build() {
            return new Notifier(this.context, this.textInfo, this.notificationInfo, this.drawableInfo, this.channelInfo);
        }
    }

    public void notify(Class<?> activity) {
        notify(activity, this.textInfo, this.notificationInfo, this.drawableInfo, this.channelInfo);
    }

    private void notify(Class<?> activity, TextInfo textInfo, NotificationInfo notificationInfo, DrawableInfo drawableInfo, ChannelInfo channelInfo) {
        createNotificationChannel(channelInfo.CHANNEL_ID, channelInfo.CHANNEL_NAME, channelInfo.CHANNEL_DESCRIPTION);

        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelInfo.CHANNEL_ID)
                .setSmallIcon(drawableInfo.SMALL_ICON_DRAWABLE_RESOURCE)
                .setContentTitle(textInfo.TITLE)
                .setContentText(textInfo.CONTENT)
                .setPriority(notificationInfo.NotificationCompat_Priority)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setTicker(textInfo.TICKER)
                .setWhen(System.currentTimeMillis())

                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(context.getResources(), drawableInfo.LARGE_ICON_DRAWABLE_RESOURCE)).bigLargeIcon((Bitmap) null))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(textInfo.BIG_TEXT));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationInfo.NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel(String CHANNEL_ID, String channel_name, String channel_description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channel_name;
            String description = channel_description;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static class ChannelInfo {
        private final String CHANNEL_ID;
        private final String CHANNEL_NAME;
        private final String CHANNEL_DESCRIPTION;

        private ChannelInfo(String channelId, String channelName, String channelDescription) {
            this.CHANNEL_ID = channelId;
            this.CHANNEL_NAME = channelName;
            this.CHANNEL_DESCRIPTION = channelDescription;
        }

        public static class ChannelInfoBuilder {
            private String channelId;
            private String channelName;
            private String channelDescription;

            public ChannelInfoBuilder withChannelId(String channelId) {
                this.channelId = channelId;
                return this;
            }

            public ChannelInfoBuilder withChannelName(String channelName) {
                this.channelName = channelName;
                return this;
            }

            public ChannelInfoBuilder withChannelDescription(String channelDescription) {
                this.channelDescription = channelDescription;
                return this;
            }

            public ChannelInfo build() {
                return new ChannelInfo(this.channelId, this.channelName, this.channelDescription);
            }
        }
    }

    public static class NotificationInfo {
        private final int NOTIFICATION_ID;
        private final int NotificationCompat_Priority;

        private NotificationInfo(int notificationId, int notificationCompat_Priority) {
            this.NOTIFICATION_ID = notificationId;
            NotificationCompat_Priority = notificationCompat_Priority;
        }

        public static class NotificationInfoBuilder {
            private int notificationId;
            private int NotificationCompat_Priority;

            public NotificationInfoBuilder withNotificationId(int notificationId) {
                this.notificationId = notificationId;
                return this;
            }

            public NotificationInfoBuilder withNotificationCompatPriority(int NotificationCompat_Priority) {
                this.NotificationCompat_Priority = NotificationCompat_Priority;
                return this;
            }

            public NotificationInfo build() {
                return new NotificationInfo(this.notificationId, this.NotificationCompat_Priority);
            }

        }
    }

    public static class DrawableInfo {
        private final int SMALL_ICON_DRAWABLE_RESOURCE;
        private final int LARGE_ICON_DRAWABLE_RESOURCE;

        public DrawableInfo(int smallIconDrawableResource, int largeIconDrawableResource) {
            SMALL_ICON_DRAWABLE_RESOURCE = smallIconDrawableResource;
            LARGE_ICON_DRAWABLE_RESOURCE = largeIconDrawableResource;
        }

        public static class DrawableInfoBuilder {
            private int smallIconDrawableResource;
            private int largeIconDrawableResource;

            public DrawableInfoBuilder withSmallIconDrawableResource(int smallIconDrawableResource) {
                this.smallIconDrawableResource = smallIconDrawableResource;
                return this;
            }

            public DrawableInfoBuilder withLargeIconDrawableResource(int largeIconDrawableResource) {
                this.largeIconDrawableResource = largeIconDrawableResource;
                return this;
            }

            public DrawableInfo build() {
                return new DrawableInfo(this.smallIconDrawableResource, this.largeIconDrawableResource);
            }
        }
    }

    public static class TextInfo {
        private final String TITLE;
        private final String CONTENT;
        private final String TICKER;
        private final String BIG_TEXT;

        private TextInfo(String title, String content, String ticker, String bigText) {
            this.TITLE = title;
            this.CONTENT = content;
            this.TICKER = ticker;
            this.BIG_TEXT = bigText;
        }

        public static class TextInfoBuilder {
            private String title;
            private String content;
            private String ticker;
            private String bigText;

            public TextInfoBuilder withTitle(String title) {
                this.title = title;
                return this;
            }

            public TextInfoBuilder withContent(String content) {
                this.content = content;
                return this;
            }

            public TextInfoBuilder withTicker(String ticker) {
                this.ticker = ticker;
                return this;
            }

            public TextInfoBuilder withBigText(String bigText) {
                this.bigText = bigText;
                return this;
            }

            public TextInfo build() {
                return new TextInfo(this.title, this.content, this.ticker, this.bigText);
            }
        }
    }
}
