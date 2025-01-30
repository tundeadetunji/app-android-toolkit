package com.inovationware.toolkit.cycles.service;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.inovationware.toolkit.code.domain.Language;
import com.inovationware.toolkit.cycles.library.DetailViewSource;
import com.inovationware.toolkit.cycles.library.LanguageViewSource;
import com.inovationware.toolkit.cycles.library.ProfileViewSource;
import com.inovationware.toolkit.cycles.model.Entity;
import com.inovationware.toolkit.cycles.model.domain.Cycle;
import com.inovationware.toolkit.cycles.model.domain.Period;
import com.inovationware.toolkit.cycles.library.CalendarLite;
import com.inovationware.toolkit.profile.strategy.ProfileManager;

import java.time.Month;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CyclesFacade {
    private static CyclesFacade instance;

    public static CyclesFacade getInstance() {
        if (instance == null) instance = new CyclesFacade();
        return instance;
    }

    private CyclesFacade() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Entity createEntity(ProfileViewSource resource) {
        return new Entity(Integer.parseInt(resource.getDayDropDown().getText().toString()), CalendarLite.getInstance().getIntFromMonth(resource.getMonthDropDown().getText().toString()), !resource.getProfileDropDown().getText().toString().isEmpty() ? resource.getProfileDropDown().getText().toString() : "Entity");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Entity createEntity(int month, int day, String name) {
        return new Entity(day, month, name);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DetailResource toLanguage(ProfileViewSource profileResource, Language language) {

        Entity mEntity = createEntity(profileResource);

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

    public boolean personalCyclesArePresent(DetailViewSource resource) {
        return !resource.getPersonalTextView().getText().toString().isEmpty();
    }

    public String createFilename(ProfileViewSource resource) {
        return (resource.getProfileDropDown().getText().toString().isEmpty() ? "Entity" : resource.getProfileDropDown().getText().toString()) + ".txt";
    }

    public String createTitleForSchedule(ProfileViewSource resource) {
        //Todo reflect if it's business or person, check wherever else applicable
        return resource.getProfileDropDown().getText().toString().isEmpty() ? "Entity" : resource.getProfileDropDown().getText().toString();
    }

    public String createDescriptionForSchedule(ProfileViewSource resource) {
        return "Yearly cycle for " + createTitleForSchedule(resource);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean userInputIsValid(ProfileViewSource resource) {
        if (resource.getDayDropDown().getText().toString().isEmpty() || resource.getMonthDropDown().getText().toString().isEmpty())
            return false;

        if (Integer.parseInt(resource.getDayDropDown().getText().toString()) > 30 &&
                resource.getMonthDropDown().getText().toString().equalsIgnoreCase(Month.SEPTEMBER.name()))
            return false;

        if (Integer.parseInt(resource.getDayDropDown().getText().toString()) > 30 &&
                resource.getMonthDropDown().getText().toString().equalsIgnoreCase(Month.APRIL.name()))
            return false;

        if (Integer.parseInt(resource.getDayDropDown().getText().toString()) > 30 &&
                        resource.getMonthDropDown().getText().toString().equalsIgnoreCase(Month.JUNE.name()))
            return false;

        if (Integer.parseInt(resource.getDayDropDown().getText().toString()) > 30 &&
                        resource.getMonthDropDown().getText().toString().equalsIgnoreCase(Month.NOVEMBER.name()))
            return false;

        if (Integer.parseInt(resource.getDayDropDown().getText().toString()) > 29 &&
                resource.getMonthDropDown().getText().toString().equalsIgnoreCase(Month.FEBRUARY.name()))
            return false;

        return true;
    }


    /**
     * Already has Title has first line.
     *
     * @param detailsResource
     * @param profilesResource
     * @param languageResource
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String scroll(DetailViewSource detailsResource, ProfileViewSource profilesResource, LanguageViewSource languageResource) {
        return title(detailsResource, languageResource) + "\n" + headline(detailsResource) + BREAK + dates(createEntity(profilesResource).getPERIOD_LISTING()) + BREAK + personal(detailsResource) + BREAK + health(detailsResource) + BREAK + business(detailsResource) + BREAK + soul(detailsResource);
    }

    private final String BREAK = "\n\n";

    public String headline(DetailViewSource resource) {
        return resource.getHeadlineTextView().getText().toString();
    }

    public String title(DetailViewSource detailsResource, LanguageViewSource languageResource) {
        Language language = languageResource.getLanguageDropDown().getText().toString().isEmpty() ? Language.English : Language.valueOf(languageResource.getLanguageDropDown().getText().toString());

        if (language == Language.English) {
            return "Cycles Information for " + headline(detailsResource).split("belongs")[0].trim();
        } /*else if (language == Language.Bulgarian) {
            return "Цикли Информация за " + headline(detailsResource).split("принадлежи")[0].trim();
        }*/

        return "Awọn iyika Alaye fun " + headline(detailsResource).split("je")[0].trim();
    }

    public String dates(List<String> listing) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < listing.size(); i++) {
            result.append(Period.toCanonicalString(i + 1)).append(": ").append(listing.get(i)).append("\n");
        }

        return result.toString();
    }


    public String personal(DetailViewSource resource) {
        return Cycle.Personal.name() + BREAK + resource.getPersonalTextView().getText().toString();
    }

    public String health(DetailViewSource resource) {
        return Cycle.Health.name() + BREAK + resource.getHealthTextView().getText().toString();
    }

    public String business(DetailViewSource resource) {
        return Cycle.Business.name() + BREAK + resource.getBusinessTextView().getText().toString();
    }

    public String soul(DetailViewSource resource) {
        return Cycle.Soul.name() + BREAK + resource.getSoulTextView().getText().toString();
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
