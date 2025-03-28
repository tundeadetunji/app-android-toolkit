package com.inovationware.toolkit.global.repository;

import static com.inovationware.toolkit.global.domain.Strings.AGRELLITE;
import static com.inovationware.toolkit.global.domain.Strings.BLUISH;
import static com.inovationware.toolkit.global.domain.Strings.DARKER;
import static com.inovationware.toolkit.global.domain.Strings.FLUORITE;
import static com.inovationware.toolkit.global.domain.Strings.NATURAL;
import static com.inovationware.toolkit.global.domain.Strings.PINKY;
import static com.inovationware.toolkit.global.domain.Strings.TAN;
import static com.inovationware.toolkit.global.domain.Strings.THROWBACK;
import static com.inovationware.toolkit.global.domain.Strings.WARM;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.global.library.utility.Support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcesManager {
    private List<Integer> pinkyImages;
    private List<Integer> magentaImages;
    private List<Integer> bluishImages;
    private List<Integer> darkerImages;
    private List<Integer> purpleImages;
    private List<Integer> naturalImages;
    private List<Integer> classicImages;
    private List<Integer> orangeImages;
    private List<Integer> brownImages;
    private Map<String, List<Integer>> welcomeImages;

    public ResourcesManager() {
        setupReferences();
    }

    private void setupReferences() {
        magentaImages = new ArrayList<>();
        magentaImages.add(R.drawable.dapino_hi);
        magentaImages.add(R.drawable.farquaad_1);
        magentaImages.add(R.drawable.hook_2);
        magentaImages.add(R.drawable.jack_jack_1);
        magentaImages.add(R.drawable.jack_jack_2);

        bluishImages = new ArrayList<>();
        bluishImages.add(R.drawable.batman_1);
        bluishImages.add(R.drawable.donald_duck_2);
        bluishImages.add(R.drawable.fairy_1);

        darkerImages = new ArrayList<>();
        darkerImages.add(R.drawable.batman_1);
        darkerImages.add(R.drawable.boss_baby_1);
        darkerImages.add(R.drawable.boss_baby_2);
        darkerImages.add(R.drawable.bugs_bunny_1);
        darkerImages.add(R.drawable.fred_1);
        darkerImages.add(R.drawable.goofy_1);
        darkerImages.add(R.drawable.jasmine_1);
        darkerImages.add(R.drawable.minion_hello);
        darkerImages.add(R.drawable.random_1);
        darkerImages.add(R.drawable.woody_1);

        purpleImages = new ArrayList<>();
        purpleImages.add(R.drawable.donald_duck_2);
        purpleImages.add(R.drawable.fairy_1);
        purpleImages.add(R.drawable.goofy_1);
        purpleImages.add(R.drawable.hook_2);
        purpleImages.add(R.drawable.potato_1);

        naturalImages = new ArrayList<>();
        naturalImages.add(R.drawable.buzz_1);
        naturalImages.add(R.drawable.goofy_1);
        naturalImages.add(R.drawable.jasmine_1);

        pinkyImages = new ArrayList<>();
        pinkyImages.add(R.drawable.dapino_hi);
        pinkyImages.add(R.drawable.fairy_1);
        pinkyImages.add(R.drawable.goofy_1);
        pinkyImages.add(R.drawable.potato_1);

        brownImages = new ArrayList<>();
        brownImages.add(R.drawable.hercules_1);
        brownImages.add(R.drawable.jasmine_1);
        brownImages.add(R.drawable.minion_hello);
        brownImages.add(R.drawable.woody_1);

        classicImages = new ArrayList<>();
        classicImages.add(R.drawable.donald_duck_3);
        classicImages.add(R.drawable.fairy_1);
        classicImages.add(R.drawable.random_1);

        orangeImages = new ArrayList<>();
        orangeImages.add(R.drawable.fred_1);
        orangeImages.add(R.drawable.goofy_1);
        orangeImages.add(R.drawable.hercules_1);
        orangeImages.add(R.drawable.jasmine_1);

        //{AGRELLITE, BLUISH, DARKER, FLUORITE, NATURAL, PINKY, TAN, THROWBACK, WARM};
        welcomeImages = new HashMap<>();
        welcomeImages.put(AGRELLITE, magentaImages);
        welcomeImages.put(BLUISH, bluishImages);
        welcomeImages.put(DARKER, darkerImages);
        welcomeImages.put(FLUORITE, purpleImages);
        welcomeImages.put(NATURAL, naturalImages);
        welcomeImages.put(PINKY, pinkyImages);
        welcomeImages.put(TAN, brownImages);
        welcomeImages.put(THROWBACK, classicImages);
        welcomeImages.put(WARM, orangeImages);


        /*

        whatImages.add(R.drawable.aurora_1);
        whatImages.add(R.drawable.batman_1);
        whatImages.add(R.drawable.boss_baby_1);
        whatImages.add(R.drawable.boss_baby_2);
        whatImages.add(R.drawable.bugs_bunny_1);
        whatImages.add(R.drawable.buzz_1);
        whatImages.add(R.drawable.dapino_hi);
        whatImages.add(R.drawable.donald_duck_1);
        whatImages.add(R.drawable.donald_duck_2);
        whatImages.add(R.drawable.donald_duck_3);
        whatImages.add(R.drawable.fairy_1);
        whatImages.add(R.drawable.farquaad_1);
        whatImages.add(R.drawable.fred_1);
        whatImages.add(R.drawable.goofy_1);
        whatImages.add(R.drawable.hercules_1);
        whatImages.add(R.drawable.hook_1);
        whatImages.add(R.drawable.hook_2);
        whatImages.add(R.drawable.jack_jack_1);
        whatImages.add(R.drawable.jack_jack_2);
        whatImages.add(R.drawable.jasmine_1);
        whatImages.add(R.drawable.mickey_mouse_1);
        whatImages.add(R.drawable.minion_hello);
        whatImages.add(R.drawable.popeye_1);
        whatImages.add(R.drawable.popeye_2);
        whatImages.add(R.drawable.potato_1);
        whatImages.add(R.drawable.random_1);
        whatImages.add(R.drawable.woody_1);
*/
    }

    public int getWelcomeImage(String theme) {
        return welcomeImages.get(theme).get(Support.anyOf(0, welcomeImages.get(theme).size()));
    }
    /*public int getWelcomeImage() {
        return pinkyImages.get(Support.anyOf(0, pinkyImages.size()));
    }*/

}
