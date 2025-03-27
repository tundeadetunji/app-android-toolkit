package com.inovationware.toolkit.global.repository;

import static com.inovationware.toolkit.global.domain.Strings.DARKER;
import static com.inovationware.toolkit.global.domain.Strings.FLUORITE;
import static com.inovationware.toolkit.global.domain.Strings.NATURAL;
import static com.inovationware.toolkit.global.domain.Strings.PINKY;
import static com.inovationware.toolkit.global.domain.Strings.TAN;
import static com.inovationware.toolkit.global.domain.Strings.WARM;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.global.library.utility.Support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcesManager {
    private List<Integer> welcomeImages;

    public ResourcesManager(){
        setupReferences();
    }

    private void setupReferences() {

        welcomeImages = new ArrayList<>();
        welcomeImages.add(R.drawable.batman_1);
        welcomeImages.add(R.drawable.boss_baby_1);
        welcomeImages.add(R.drawable.boss_baby_2);
        welcomeImages.add(R.drawable.bugs_bunny_1);
        welcomeImages.add(R.drawable.buzz_1);
        welcomeImages.add(R.drawable.dapino_hi);
        welcomeImages.add(R.drawable.donald_duck_1);
        welcomeImages.add(R.drawable.donald_duck_2);
        welcomeImages.add(R.drawable.donald_duck_3);
        welcomeImages.add(R.drawable.fairy_1);
        welcomeImages.add(R.drawable.farquaad_1);
        welcomeImages.add(R.drawable.fred_1);
        welcomeImages.add(R.drawable.goofy_1);
        welcomeImages.add(R.drawable.hercules_1);
        welcomeImages.add(R.drawable.hook_1);
        welcomeImages.add(R.drawable.hook_2);
        welcomeImages.add(R.drawable.jack_jack_1);
        welcomeImages.add(R.drawable.jasmine_1);
        welcomeImages.add(R.drawable.mickey_mouse_1);
        welcomeImages.add(R.drawable.popeye_2);
        welcomeImages.add(R.drawable.potato_1);
        welcomeImages.add(R.drawable.random_1);
        welcomeImages.add(R.drawable.winnie_1);
        welcomeImages.add(R.drawable.woody_1);
    }

    public int getWelcomeImage(){
        return welcomeImages.get(Support.anyOf(0, welcomeImages.size()-1));
    }

}
