package com.inovationware.toolkit.global.library.app;

import static com.inovationware.toolkit.global.domain.Strings.DARKER;
import static com.inovationware.toolkit.global.domain.Strings.FLUORITE;
import static com.inovationware.toolkit.global.domain.Strings.NATURAL;
import static com.inovationware.toolkit.global.domain.Strings.PINKY;
import static com.inovationware.toolkit.global.domain.Strings.TAN;
import static com.inovationware.toolkit.global.domain.Strings.WARM;

import com.inovationware.toolkit.R;

import java.util.HashMap;
import java.util.Map;

public class ThemeManager {
    private Map<String, Integer> statusBarColors;

    public ThemeManager(){
        setupReferences();
    }

    private void setupReferences() {
        statusBarColors = new HashMap<>();
        statusBarColors.put(PINKY, R.color.pinky_primary_700);
        statusBarColors.put(DARKER, R.color.darker_primary_700);
        statusBarColors.put(NATURAL, R.color.natural_primary_700);
        statusBarColors.put(TAN, R.color.brown_primary_700);
        statusBarColors.put(WARM, R.color.orange_primary_700);
        statusBarColors.put(FLUORITE, R.color.purple_primary_700);

    }

    public int getStatusBarColor(String theme){
        return statusBarColors.get(theme);
    }


}
