package com.inovationware.toolkit.common.utility;

import static com.inovationware.toolkit.common.utility.Support.initialParamsAreSet;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.location.service.LocationService;

import java.util.Random;

public class WelcomeCaptionService {
    private static WelcomeCaptionService instance;

    public static WelcomeCaptionService getInstance(Activity activity, Context context, LocationService locationService) {
        if (instance == null) instance = new WelcomeCaptionService(activity, context, locationService);
        return instance;
    }

    private WelcomeCaptionService(Activity activity, Context context, LocationService locationService) {
        this.activity = activity;
        this.context = context;
        this.locationService = locationService;
    }

    private boolean firstTimeShowingScreen = true;
    private final Activity activity;
    private Context context;
    private LocationService locationService;

    public void setupCaptions(TextView captionTextView, TextView initialInfoTextView, SharedPreferencesManager store, GroupManager machines){

        setSignInText(captionTextView, context);
        setInitialText(initialInfoTextView, context,  store, machines);
        setWelcomeText(captionTextView, initialInfoTextView, context, store, machines);

        captionTextView.setOnClickListener(captionTextViewClickListener);
        captionTextView.setOnLongClickListener(guideImageViewLongClick);
        initialInfoTextView.setOnClickListener(initialInfoTextViewClickListener);

    }

    void setSignInText(TextView captionTextView, Context context){
        SignInManager signInManager = SignInManager.getInstance();
        captionTextView.setText(
                signInManager.thereIsPrincipal(context) ?
                        DomainObjects.WELCOME + signInManager.getSignedInUser(context).getName() :
                        DomainObjects.WELCOME);
    }

    void setInitialText(TextView initialInfoTextView, Context context, SharedPreferencesManager store, GroupManager machines) {
        if (initialParamsAreSet(context, store, machines)) {
            return;
        }

        initialInfoTextView.setText(
                "Hotspot information must be present.\n" +
                        "\nAnd you need to set the id and username," +
                        "\nand have at least one target device (while" +
                        "\none of them is set as default)." +
                        "\n\nYou also need to set the password and " +
                        "\nsalt for encyption." +
                        "\n\nYou can get relevant info from the PC" +
                        "\nby clicking Info on the tray icon." +
                        "\n\nNote that you have to enter password" +
                        "\nand salt into your target devices manually." +
                        "\n\nTap this message to go to Settings.");

        initialInfoTextView.setVisibility(View.VISIBLE);
    }

    private void setWelcomeText(TextView captionTextView, TextView initialInfoTextView, Context context, SharedPreferencesManager store, GroupManager machines) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);

        if (acct != null) {
            captionTextView.setText(DomainObjects.WELCOME + ", " + acct.getGivenName());
            /*String personName = acct.getDisplayName();
            Uri personPhoto = acct.getPhotoUrl();

            textName.setText(personName);
            //Glide.with(this).load(String.valueOf(personPhoto)).into(imagePicture);
            Glide.with(this).load(String.valueOf(personPhoto)).circleCrop().into(imagePicture);*/
        } else {
            captionTextView.setText(DomainObjects.WELCOME);
            initialInfoTextView.setVisibility(initialParamsAreSet(context, store, machines) ? View.INVISIBLE : View.VISIBLE);
        }

    }

    private final View.OnClickListener captionTextViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //activity.startActivity(new Intent(context, CyclesDayViewActivity.class));
        }
    };
    private final View.OnClickListener initialInfoTextViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //activity.startActivity(new Intent(context, SettingsActivity.class));
        }
    };
    private final View.OnLongClickListener guideImageViewLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            locationService.stopLocationUpdates();
            Toast.makeText(context, DomainObjects.FRIENDLY_MESSAGES.get(new Random().nextInt(DomainObjects.FRIENDLY_MESSAGES.size() - 0) + 0), Toast.LENGTH_SHORT).show();
            return true;
        }
    };


}
