package com.inovationware.toolkit.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.databinding.ActivityLoginBinding;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.ui.contract.BaseActivity;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;

    private static final int RC_SIGN_IN = 0;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUi();

    }


    private void setupUi() {
        mGoogleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build());

        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //SignInManager.getInstance().onSignIn(LoginActivity.this);

            // Signed in successfully, show authenticated UI.
            if (getIntent().getExtras() != null){
                String key = getIntent().getExtras().getString(Strings.DTO_CLASS_STRING);
                try {
                    startActivity(new Intent(LoginActivity.this, key.isEmpty() ? MainActivity.class : determineWhichActivity(key)));
                } catch (Exception ignored) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
            else{
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

        } catch (ApiException ignored) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Toast.makeText(this, "signInResult:failed code=" + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private Class<?> determineWhichActivity(String key) {
        if(key.equalsIgnoreCase(SettingsActivity.class.getSimpleName())){
            return SettingsActivity.class;
        }
        else if (key.equalsIgnoreCase(NetTimerActivity.class.getSimpleName())){
            return NetTimerActivity.class;
        }
        else if (key.equalsIgnoreCase(ReplyActivity.class.getSimpleName())){
            return ReplyActivity.class;
        }
        else if (key.equalsIgnoreCase(ManageBoardActivity.class.getSimpleName())){
            return ManageBoardActivity.class;
        }
        else if (key.equalsIgnoreCase(BoardActivity.class.getSimpleName())){
            return BoardActivity.class;
        }
        throw new RuntimeException("ToDo");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.homeHomeMenuItem) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}