package com.inovationware.toolkit.features.cycles.service;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.inovationware.toolkit.features.code.domain.Language;
import com.inovationware.toolkit.features.cycles.model.Entity;
import com.inovationware.toolkit.features.cycles.model.domain.Cycle;
import com.inovationware.toolkit.features.cycles.model.domain.Period;
import com.inovationware.toolkit.features.profile.strategy.ProfileManager;

import java.time.Month;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Same as CyclesFacade, but not making any assumption on what type any View is.
 */
public class CyclesFacadeLite {
    private static CyclesFacadeLite instance;

    public static CyclesFacadeLite getInstance() {
        if (instance == null) instance = new CyclesFacadeLite();
        return instance;
    }

    private CyclesFacadeLite() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Entity createEntity(int month, int day, String name) {
        if (!userInputIsValid(Month.of(month), day, name)) throw new IllegalArgumentException("Some fields are invalid.");
        return new Entity(day, month, name);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DetailResource toLanguage(Entity mEntity, Language language) {

        switch (language) {
            case English:
                return loadEnglish(mEntity);
            case Yoruba:
                return loadYoruba(mEntity);
            /*case Bulgarian:
                return loadBulgarian(mEntity);*/
        }

        throw new RuntimeException("Language not understood: expected English, Yoruba or Bulgarian.");
    }

    private DetailResource loadEnglish(Entity entity) {
        return DetailResource.create(
                entity.getHEADLINE(),
                entity.getSOUL(),
                entity.getPERSONAL(),
                entity.getBUSINESS(),
                entity.getHEALTH()
        );
    }

    private DetailResource loadYoruba(Entity entity) {
        return DetailResource.create(
                entity.getHEADLINE_IN_YORUBA(),
                entity.getSOUL_IN_YORUBA(),
                entity.getPERSONAL_IN_YORUBA(),
                entity.getBUSINESS_IN_YORUBA(),
                entity.getHEALTH_IN_YORUBA()
        );
    }

    private DetailResource loadBulgarian(Entity entity) {
        return DetailResource.create(
                entity.getHEADLINE_IN_BULGARIAN(),
                entity.getSOUL_IN_BULGARIAN(),
                entity.getPERSONAL_IN_BULGARIAN(),
                entity.getBUSINESS_IN_BULGARIAN(),
                entity.getHEALTH_IN_BULGARIAN()
        );
    }

    public String createFilename(String name) {
        return (name + ".txt");
    }

    public String createTitleForSchedule(String name) {
        //Todo reflect if it's business or person, check wherever else applicable
        return name;
    }

    public String createDescriptionForSchedule(String name) {
        return "Yearly cycle for " + createTitleForSchedule(name);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean userInputIsValid(Month month, int day, String name) {
        if (name.isEmpty()) return false;

        if (day > 28 && month.equals(Month.FEBRUARY)) return false;

        if (day > 30 &&
                (!month.equals(Month.SEPTEMBER) &&
                        !month.equals(Month.APRIL) &&
                        !month.equals(Month.JUNE) &&
                        !month.equals(Month.NOVEMBER)))
            return false;

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String scroll(Entity entity, Language language) {
        return title(entity.getHEADLINE(), language) + "\n" + entity.getHEADLINE() + BREAK + dates(entity.getPERIOD_LISTING()) + BREAK + personal(entity.getPERSONAL()) + BREAK + health(entity.getHEALTH()) + BREAK + business(entity.getBUSINESS()) + BREAK + soul(entity.getSOUL());
    }

    private final String BREAK = "\n\n";

    private String title(String headline, Language language) {

        if (language == Language.English) {
            return "Cycles Information for " + headline.split("belongs")[0].trim();
        } /*else if (language == Language.Bulgarian) {
            return "Цикли Информация за " + headline.split("принадлежи")[0].trim();
        }*/

        return "Awọn iyika Alaye fun " + headline.split("je")[0].trim();
    }

    private String dates(List<String> listing) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < listing.size(); i++) {
            result.append(Period.toCanonicalString(i + 1)).append(": ").append(listing.get(i)).append("\n");
        }

        return result.toString();
    }


    private String personal(String personal) {
        return Cycle.Personal.name() + BREAK + personal;
    }

    private String health(String health) {
        return Cycle.Health.name() + BREAK + health;
    }

    private String business(String business) {
        return Cycle.Business.name() + BREAK + business;
    }

    private String soul(String soul) {
        return Cycle.Soul.name() + BREAK + soul;
    }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class DetailResource {
        private String headline;
        private String soul;
        private String personal;
        private String business;
        private String health;

        public static DetailResource create(String headline, String soul, String personal, String business, String health) {
            return new DetailResource(headline, soul, personal, business, health);
        }
    }


    @Getter
    public static class DateResource {
        private int day;
        private int month;

        private DateResource(int day, int month) {
            this.day = day;
            this.month = month;
        }

        /**
         * @param personalCycleDateString in the format "MMMM d  to  MMMM d"
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        public static DateResource create(String personalCycleDateString, ProfileManager profiler) {
            String[] tokens = personalCycleDateString.split(" {2}to {2}")[0].split(" ");
            return new DateResource(Integer.parseInt(tokens[1].trim()), profiler.intValueFromMonthString(tokens[0].trim()));
        }


    }

}
