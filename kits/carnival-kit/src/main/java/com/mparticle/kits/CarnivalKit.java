package com.mparticle.kits;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.text.TextUtils;

import com.carnival.sdk.Carnival;
import com.mparticle.MPEvent;
import com.mparticle.MParticle;
import com.mparticle.messaging.MessagingConfigCallbacks;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CarnivalKit extends AbstractKit implements MessagingConfigCallbacks, ClientSideForwarder, PushProvider {

    private static final String KEY_APP_KEY = "com.carnival.sdk.KEY_APP_KEY";
    private static final String KEY_GEOIP_ENABLED = "com.carnival.sdk.KEY_GEOIP_ENABLED";
    private static final String KEY_INAPP_NOTIFICATION_ENABLED = "com.carnival.sdk.KEY_INAPP_NOTIFICATION_ENABLED";

    boolean initialized = false;

    @Override
    public Object getInstance(Activity activity) {
        return null;
    }

    @Override
    public String getName() {
        return "Carnival";
    }

    @Override
    public boolean isOriginator(String uri) {
        return false;
    }

    @Override
    protected AbstractKit update() {
        String appKey = properties.get(KEY_APP_KEY);

        if (properties.containsKey(KEY_GEOIP_ENABLED)) {
            Carnival.setGeoIpTrackingEnabled(Boolean.parseBoolean(properties.get(KEY_GEOIP_ENABLED)));
        }

        if (properties.containsKey(KEY_INAPP_NOTIFICATION_ENABLED)) {
            Carnival.setInAppNotificationsEnabled(Boolean.parseBoolean(properties.get(KEY_INAPP_NOTIFICATION_ENABLED)));
        }

        if (!initialized && !TextUtils.isEmpty(appKey)) {
            Carnival.startEngine(context, appKey);
            initialized = true;
        }

        return this;
    }

    @Override
    public boolean isRunning() {
        return initialized;
    }

    @Override
    public List<ReportingMessage> logEvent(MPEvent event) throws Exception {
        String eventName = event.getEventName();
        if (!TextUtils.isEmpty(eventName)) {
            List<ReportingMessage> messages = new LinkedList<>();
            messages.add(ReportingMessage.fromEvent(this, event));

            Carnival.logEvent(eventName);
            return messages;
        }

        return null;
    }

    @Override
    public void setLocation(Location location) {
        Carnival.updateLocation(location);
    }

    @Override
    public void setUserAttributes(JSONObject attributes) {
        if (attributes != null) {
            Iterator<String> keys = attributes.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String rawValue = attributes.optString(key, "");

                Carnival.setAttribute(key, rawValue);
            }
        }
    }

    @Override
    public void removeUserAttribute(String key) {
        Carnival.removeAttribute(key);
    }

    @Override
    public void setUserIdentity(String id, MParticle.IdentityType identityType) {
        if (MParticle.IdentityType.CustomerId == identityType) {
            Carnival.setUserId(id, null);
        }
    }

    @Override
    public void setPushNotificationIcon(int resId) {
        Carnival.setNotificationIcon(resId);
    }

    @Override
    public List<ReportingMessage> handleGcmMessage(Intent intent) {

        //TODO: Start GcmIntentService with intent
//        new GcmIntentService().onMessageReceived("", intent.getExtras());
        List<ReportingMessage> messages = new LinkedList<>();
        messages.add(ReportingMessage.fromPushMessage(this, intent));
        return messages;

    }

    @Override
    public List<ReportingMessage> logout() {
        Carnival.setUserId(null, null);
        //TODO: Clear User Attributes
        return null;
    }

    @Override
    public void setPushRegistrationId(String registrationId) {
        //TODO: Set GCM Token
    }

    /*
     * Unsupported Methods
     */

    @Override
    public void setPushNotificationTitle(int resId) {

    }

    @Override
    public void setPushSenderId(String senderId) {

    }

    @Override
    public void setPushSoundEnabled(boolean enabled) {

    }

    @Override
    public void setPushVibrationEnabled(boolean enabled) {

    }

    @Override
    public List<ReportingMessage> logScreen(String screenName, Map<String, String> eventAttributes) throws Exception {
        return null;
    }

    @Override
    public List<ReportingMessage> logLtvIncrease(BigDecimal valueIncreased, String eventName, Map<String, String> contextInfo) {
        return null;
    }
}
