package com.inovationware.toolkit.ui.contract;

import static com.inovationware.toolkit.common.domain.DomainObjects.AGRELLITE;
import static com.inovationware.toolkit.common.domain.DomainObjects.BLUISH;
import static com.inovationware.toolkit.common.domain.DomainObjects.TAN;
import static com.inovationware.toolkit.common.domain.DomainObjects.DARKER;
import static com.inovationware.toolkit.common.domain.DomainObjects.NATURAL;
import static com.inovationware.toolkit.common.domain.DomainObjects.THROWBACK;
import static com.inovationware.toolkit.common.domain.DomainObjects.WARM;
import static com.inovationware.toolkit.common.domain.DomainObjects.PINKY;
import static com.inovationware.toolkit.common.domain.DomainObjects.FLUORITE;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    private Map<String, Integer> themes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themes = new HashMap<>();
        themes.put(PINKY, R.style.Pinky);
        themes.put(DARKER, R.style.Darker);
        themes.put(NATURAL, R.style.Natural);
        themes.put(TAN, R.style.Brown);
        themes.put(WARM, R.style.Orange);
        themes.put(FLUORITE, R.style.Purple);
        themes.put(THROWBACK, R.style.Throwback);
        themes.put(AGRELLITE, R.style.Magenta);
        themes.put(BLUISH, R.style.Bluish);
        setAppTheme(); // Set the theme before calling super

        super.onCreate(savedInstanceState);
    }
    private void setAppTheme() {
        setTheme(themes.get(SharedPreferencesManager.getInstance().getTheme(this)));
    }
}
