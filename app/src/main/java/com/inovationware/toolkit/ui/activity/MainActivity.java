package com.inovationware.toolkit.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.ActivityMainBinding;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.MessageBox;
import com.inovationware.toolkit.global.library.app.SignInManager;
import com.inovationware.toolkit.global.library.app.ThemeManager;
import com.inovationware.toolkit.global.library.external.ApkClient;
import com.inovationware.toolkit.system.service.ToolkitServiceManager;
import com.inovationware.toolkit.location.service.LocationService;
import com.inovationware.toolkit.notification.service.PushNotificationService;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.location.service.impl.LocationServiceImpl;
import com.inovationware.toolkit.tts.service.TTSService;
import com.inovationware.toolkit.ui.contract.BaseActivity;
import com.inovationware.toolkit.ui.support.MainAuthority;

import static com.inovationware.toolkit.global.domain.Strings.CHOSEN;
import static com.inovationware.toolkit.global.domain.Strings.TAN;
import static com.inovationware.toolkit.global.domain.Strings.DARKER;
import static com.inovationware.toolkit.global.domain.Strings.NATURAL;
import static com.inovationware.toolkit.global.domain.Strings.NET_TIMER_NOTIFICATION_SERVICE_IS_RUNNING;
import static com.inovationware.toolkit.global.domain.Strings.WARM;
import static com.inovationware.toolkit.global.domain.Strings.PINKY;
import static com.inovationware.toolkit.global.domain.Strings.FLUORITE;
import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_REMOTE_LINK_APPS_KEY;
import static com.inovationware.toolkit.global.domain.Strings.apps;
import static com.inovationware.toolkit.global.domain.Strings.no;
import static com.inovationware.toolkit.global.domain.Strings.ttsServiceProvider;
import static com.inovationware.toolkit.global.domain.Strings.yes;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private MainAuthority authority;
    private BottomNavigationView bottomNavigationView;
    private NavController fragment;
    private AppBarConfiguration appBarConfiguration;
    private Factory factory;
    private SharedPreferencesManager store;
    private GroupManager machines;
    private ActivityMainBinding binding;
    private EncryptionManager security;
    private SignInManager user;
    private ApkClient apkClient;
    private Context context;
    private LocationService service;
    private ToolkitServiceManager services;


    private LocationService loc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupConfigurations();
        setupAccess();
        setupReferences();
        setupListeners();
        startServices();
        otherStartupProcedures();

    }

    private void setupAccess() {
        context = MainActivity.this;
        authority = MainAuthority.getInstance();
        store = SharedPreferencesManager.getInstance();
        machines = GroupManager.getInstance();
        factory = Factory.getInstance();
        ttsServiceProvider = TTSService.getInstance(context);
        security = EncryptionManager.getInstance();
        user = SignInManager.getInstance();
        apkClient = ApkClient.getInstance();
        service = LocationServiceImpl.getInstance(context);
        services = ToolkitServiceManager.getInstance();
    }

    private void setupReferences() {
        store.setString(MainActivity.this, SHARED_PREFERENCES_REMOTE_LINK_APPS_KEY, "");
        apps = null;
        Strings.cachedMemos = null;
        Strings.getListOfInstalledApps(apkClient, getPackageManager(), true);

    }

    private void setupConfigurations() {

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragment = Navigation.findNavController(this, R.id.fragment);

        NavigationUI.setupWithNavController(bottomNavigationView, fragment);

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment, R.id.localTaskFragment, R.id.searchFragment, R.id.linkFragment, R.id.transferFragment).build();

        NavigationUI.setupActionBarWithNavController(this, fragment, appBarConfiguration);

        //customizeBottomNavigationView(bottomNavigationView);
    }

    private void setupListeners() {
        binding.QuickSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authority.sendToPCFromClipboard(context, factory, store, machines, security);
            }
        });

        /**
         LongClick should stop sending Location Updates
         */
        binding.QuickSendButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                service.stopLocationUpdates();
                return true;
            }
        });
    }

    private void startServices() {
        //ToDo fix: requires context
        //ToDo fix: check battery status, don't run if batter is below threshold
        if (!NET_TIMER_NOTIFICATION_SERVICE_IS_RUNNING) {
            startService(new Intent(getApplicationContext(), PushNotificationService.class));
            NET_TIMER_NOTIFICATION_SERVICE_IS_RUNNING = true;
        }

        services.startServices(context);
    }

    private void otherStartupProcedures() {
        authority.onFinishedLoading(context);
    }

    @Override
    protected void onDestroy() {
        ttsServiceProvider.shutdown();
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settingsMainMenuItem) {
            authority.openSettingsActivity(context, SignInManager.getInstance());
            return true;
        } else if (item.getItemId() == R.id.helpMainMenuItem) {
            startActivity(new Intent(MainActivity.this, HelpActivity.class));
            return true;
        } else if (item.getItemId() == R.id.aboutMainMenuItem) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        } else if (item.getItemId() == R.id.themeMainMenuItem) {
            showThemeMenu();
            return true;
        } else if (item.getItemId() == R.id.logoutMainMenuItem) {
            new MessageBox("Really log out?", yes,no) {
                @Override
                public void positiveButtonAction() {
                    performLogout();
                }

                @Override
                public void negativeButtonAction() {

                }
            }.show(context);
            return true;
        } else if (item.getItemId() == R.id.boardMainMenuItem) {
            if (!user.isLoggedIn(MainActivity.this)) {
                SignInManager.getInstance().beginLoginProcess(MainActivity.this, BoardActivity.class.getSimpleName());
                return true;
            }
            startActivity(new Intent(MainActivity.this, BoardActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showThemeMenu() {
        final String[] themes = new String[]{DARKER, FLUORITE, NATURAL, PINKY, TAN, WARM};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a theme...");

        // Set the radio button options
        int checkedItem = 0; // Default checked item index
        for (int i = 0; i < themes.length; i++) {
            if (themes[i].equals(store.getTheme(context))) {
                checkedItem = i; // Find the index of the selected item
                break;
            }
        }

        builder.setSingleChoiceItems(themes, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                store.setTheme(context, themes[which]);
            }
        });

        // Add a positive button to confirm the selection
        builder.setPositiveButton(CHOSEN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Dismiss the dialog
                //bottomNavigationView.setBackgroundColor(Color.parseColor("#21422a"));
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(new ThemeManager().getStatusBarColor(store.getTheme(context)));
                }*/
                //bottomNavigationView.setBackgroundColor(getResources().getColor(new ThemeManager().getStatusBarColor(store.getTheme(context))));
                recreate();
                //bottomNavigationView.setBackgroundColor(getResources().getColor(new ThemeManager().getStatusBarColor(store.getTheme(context))));
            }
        });

        // Create and show the dialog
        builder.create().show();

    }

    private void openTasksActivity(SignInManager signInManager) {
        if (signInManager.getSignedInUser(MainActivity.this) != null)
            startActivity(new Intent(MainActivity.this, NetTimerActivity.class));
        else
            signInManager.beginLoginProcess(MainActivity.this, NetTimerActivity.class.getSimpleName());
    }

/*
    private void openSettingsActivity(SignInManager signInManager) {
        if (signInManager.getSignedInUser(MainActivity.this) != null)
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        else
            signInManager.beginLoginProcess(MainActivity.this, SettingsActivity.class.getSimpleName());
    }
*/

    /*private void beginLoginProcess(Context context, String value) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Phrase.DTO_CLASS_STRING, value);
        startActivity(intent);
    }*/


    private void performLogout() {
        if (!user.isLoggedIn(MainActivity.this)) return;

        GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()).signOut()
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Signed out!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        //finish();
                    }
                });
    }

/*
    private void sendToPCFromClipboard() {
        //canSend
        ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
        if (initialParamsAreSet(MainActivity.this, store, machines) && clipboard.hasText())
            doSend(POST_PURPOSE_REGULAR, determineMeta(MainActivity.this, store));
    }
*/


/*
    void doSend(String purpose, String meta) {
        if (!thereIsInternet(getApplicationContext())) return;

        factory.transfer.service.sendText(
                getApplicationContext(),
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL,
                        store.getUsername(MainActivity.this),
                        store.getID(MainActivity.this),
                        Transfer.Intent.writeText,
                        store.getSender(MainActivity.this),
                        determineTarget(MainActivity.this, store, machines),
                        purpose,
                        meta,
                        security.encrypt(MainActivity.this, store, clipboardGetText(getApplicationContext())),
                        Strings.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);
    }
*/


/*
    private void startSystemAlertWindowPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    //Log.i(TAG, "[startSystemAlertWindowPermission] requesting system alert window permission.");
                    startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
                }
            }
        } catch (Exception ignored) {

        }
    }
*/

}