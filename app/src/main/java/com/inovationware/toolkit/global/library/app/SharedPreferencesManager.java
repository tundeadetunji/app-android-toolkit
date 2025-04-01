package com.inovationware.toolkit.global.library.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.global.domain.DomainObjects;

import static com.inovationware.toolkit.global.domain.DomainObjects.EMPTY_STRING;
import static com.inovationware.toolkit.global.domain.DomainObjects.HAPTIC_FEEDBACK_ONLY_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.HAPTIC_FEEDBACK_ON_ACKNOWLEDGEMENT_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.LAST_WEB_PAGE_SENT_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_APPEND_TIMEZONE_WHEN_SENDING;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_APPEND_UUID_WHEN_SENDING;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_BASE_URL_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_PROMPT_TO_SYNC_NOTE;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_SHOW_VOCABULARY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_USERNAME_KEY;
import static com.inovationware.toolkit.global.library.utility.Code.clean;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_DISPLAY_ERROR_MESSAGE;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_ID_KEY;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_SENDER;
import static com.inovationware.toolkit.global.domain.DomainObjects.SHARED_PREFERENCES_TARGET_MODE;
import static com.inovationware.toolkit.global.domain.DomainObjects.TARGET_MODES;
import static com.inovationware.toolkit.global.domain.DomainObjects.TARGET_MODE_TO_DEVICE;
import static com.inovationware.toolkit.global.domain.DomainObjects.TARGET_MODE_TO_GROUP;

public class SharedPreferencesManager {
    private static SharedPreferencesManager instance;
    private final String name = "c78b493e-c6e5";
    private ArrayAdapter<String> dropdownAdapter;

    private SharedPreferencesManager() {
    }

    public static SharedPreferencesManager getInstance() {
        if (instance == null) instance = new SharedPreferencesManager();
        return instance;
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public String getString(Context context, String key, String defaultValue) {
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    public String getString(Context context, String key) {
        return getString(context, key, "");
    }

    public String getLocalTaskValueFromKey(Context context, String key) {
        return getString(context, key, EMPTY_STRING);
    }

    public void setString(Context context, String key, String value) {
        if (key.trim().length() < 1) return;
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key.trim(), value.trim());
        editor.apply();
    }

    public void setChecked(Context context, String key, boolean value) {
        setString(context, key.trim(), String.valueOf(value));
    }

    public boolean getChecked(Context context, String key, boolean defaultValue) {
        return Boolean.parseBoolean(getString(context, key, String.valueOf(defaultValue)));
    }

    public boolean getChecked(Context context, String key) {
        return Boolean.parseBoolean(getString(context, key, EMPTY_STRING));
    }

    public String getSender(Context context) {
        //return getString(SHARED_PREFERENCES_SENDER, Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        return getString(context, SHARED_PREFERENCES_SENDER, Settings.Secure.getString(context.getContentResolver(), "bluetooth_name"));
    }

    public void setSender(Context context, String sender) {
        setString(context, SHARED_PREFERENCES_SENDER, clean(sender).length() > 0 ? clean(sender) : defaultSender(context));
    }

    public void clearSender(Context context, TextView senderTextView) {
        setSender(context, "");
        senderTextView.setText("");
    }

    public String getBaseUrl(Context context) {
        return getString(context, SHARED_PREFERENCES_BASE_URL_KEY, "");
    }

    public void setBaseUrl(Context context, String url) {
        setString(context, SHARED_PREFERENCES_BASE_URL_KEY, clean(url));
    }

    public void clearBaseUrl(Context context, TextView baseUrlTextView) {
        setBaseUrl(context, "");
        baseUrlTextView.setText("");
    }

    public String getTheme(Context context){
        return getString(context, DomainObjects.SHARED_PREFERENCES_THEME_KEY, DomainObjects.PINKY);
    }

    public void setTheme(Context context, String value){
        setString(context, DomainObjects.SHARED_PREFERENCES_THEME_KEY, value);
    }

    public boolean shouldAppendUuidToOutput(Context context){
        return getChecked(context, SHARED_PREFERENCES_APPEND_UUID_WHEN_SENDING, true);
    }
    public boolean shouldAppendTimezoneToOutput(Context context){
        return getChecked(context, SHARED_PREFERENCES_APPEND_TIMEZONE_WHEN_SENDING, true);
    }

    public boolean shouldDisplayErrorMessage(Context context) {
        return getChecked(context, SHARED_PREFERENCES_DISPLAY_ERROR_MESSAGE, false);
    }

    public boolean shouldPromptToSyncNote(Context context) {
        return getChecked(context, SHARED_PREFERENCES_PROMPT_TO_SYNC_NOTE, false);
    }

    public boolean shouldShowVocabulary(Context context) {
        return getChecked(context, SHARED_PREFERENCES_SHOW_VOCABULARY, false);
    }

    public void updateShouldShowVocabulary(Context context, boolean value) {
        setChecked(context, SHARED_PREFERENCES_SHOW_VOCABULARY, value);
    }

    public boolean hapticFeedbackOnly(Context context){
        return getChecked(context, HAPTIC_FEEDBACK_ONLY_KEY, false);
    }

    public void setHapticFeedbackOnly(Context context, boolean value){
        setChecked(context, HAPTIC_FEEDBACK_ONLY_KEY, value);
    }

    public boolean hapticFeedbackOnAcknowledgement(Context context){
        return getChecked(context, HAPTIC_FEEDBACK_ON_ACKNOWLEDGEMENT_KEY, false);
    }

    public String getLastSentWebPage(Context context){
        return getString(context, LAST_WEB_PAGE_SENT_KEY, "");
    }
    public void setLastSentWebPage(Context context, String url){
        setString(context, LAST_WEB_PAGE_SENT_KEY, url);
    }

    public void setHapticFeedbackOnAcknowledgement(Context context, boolean value){
        setChecked(context, HAPTIC_FEEDBACK_ON_ACKNOWLEDGEMENT_KEY, value);
    }

    public String getID(Context context) {
        return getString(context, SHARED_PREFERENCES_ID_KEY, EMPTY_STRING);
    }

    public void setID(Context context, String id) {
        setString(context, SHARED_PREFERENCES_ID_KEY, id.trim());
    }

    public void clearID(Context context, TextView idTextView) {
        setID(context, "");
        idTextView.setText("");
    }

    public String getUsername(Context context) {
        return getString(context, SHARED_PREFERENCES_USERNAME_KEY, EMPTY_STRING);
    }

    public void setUsername(Context context, String username) {
        setString(context, SHARED_PREFERENCES_USERNAME_KEY, username.trim());
    }

    public void clearUsername(Context context, TextView usernameTextView) {
        setUsername(context, "");
        usernameTextView.setText("");
    }

    public String getTargetMode(Context context) {
        return getString(context, SHARED_PREFERENCES_TARGET_MODE, TARGET_MODE_TO_DEVICE);
    }

    public String[] getTargetModes() {
        return TARGET_MODES;
    }

    public void setTargetMode(Context context, String mode) {
        if (clean(mode).equalsIgnoreCase(TARGET_MODE_TO_DEVICE) || clean(mode).equalsIgnoreCase(TARGET_MODE_TO_GROUP))
            setString(context, SHARED_PREFERENCES_TARGET_MODE, mode);
    }

    public void setEncryptionSalt(Context context, String salt){
        setString(context, DomainObjects.SHARED_PREFERENCES_ENCRYPTION_SALT, salt);
    }

    public String getEncryptionSalt(Context context){
        return getString(context, DomainObjects.SHARED_PREFERENCES_ENCRYPTION_SALT);
    }

    public void setEncryptionPassword(Context context, String password){
        setString(context, DomainObjects.SHARED_PREFERENCES_ENCRYPTION_PASSWORD, password);
    }

    public String getEncryptionPassword(Context context){
        return getString(context, DomainObjects.SHARED_PREFERENCES_ENCRYPTION_PASSWORD);
    }

    public void setGithubOwner(Context context, String owner){
        setString(context, DomainObjects.SHARED_PREFERENCES_GITHUB_OWNER, owner);
    }

    public String getGithubOwner(Context context){
        return getString(context, DomainObjects.SHARED_PREFERENCES_GITHUB_OWNER);
    }

    public void setGithubToken(Context context, String token){
        setString(context, DomainObjects.SHARED_PREFERENCES_GITHUB_TOKEN, token);
    }

    public String getGithubToken(Context context){
        return getString(context, DomainObjects.SHARED_PREFERENCES_GITHUB_TOKEN);
    }

    public void setGithubDefaultRepository(Context context, String repository){
        setString(context, DomainObjects.SHARED_PREFERENCES_GITHUB_DEFAULT_REPOSITORY, repository);
    }

    public String getGithubDefaultRepository(Context context){
        return getString(context, DomainObjects.SHARED_PREFERENCES_GITHUB_DEFAULT_REPOSITORY);
    }

    public void setGithubDefaultBranch(Context context, String branch){
        setString(context, DomainObjects.SHARED_PREFERENCES_GITHUB_DEFAULT_BRANCH, branch);
    }

    public String getGithubDefaultBranch(Context context){
        return getString(context, DomainObjects.SHARED_PREFERENCES_GITHUB_DEFAULT_BRANCH);
    }

    public boolean isGithubDetailsSet(Context context){
        return !getGithubOwner(context).isEmpty() && !getGithubToken(context).isEmpty();
    }

    public void setDefaultNoteTitle(Context context, String title){
        setString(context, DomainObjects.SHARED_PREFERENCES_DEFAULT_NOTE_TITLE_KEY, title);
    }

    public String getDefaultNoteTitle(Context context){
        return getString(context, DomainObjects.SHARED_PREFERENCES_DEFAULT_NOTE_TITLE_KEY, DomainObjects.SHARED_PREFERENCES_DEFAULT_NOTE_TITLE);
    }

    public void setDropDown(Context context, AutoCompleteTextView dropdown, String[] newList) {
        dropdownAdapter = new ArrayAdapter<String>(context, R.layout.default_drop_down, newList);
        //dropdown.setAdapter(null);
        dropdown.setAdapter(dropdownAdapter);
    }

    public void setDropDown(Context context, AutoCompleteTextView dropdown, String[] newList, String text) {
        dropdown.setText(text);
        setDropDown(context, dropdown, newList);
    }

    String defaultSender(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
        //return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
    }

}
