package com.inovationware.toolkit.features.cycles.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.inovationware.toolkit.features.cycles.model.domain.Period;
import com.inovationware.toolkit.features.cycles.model.value.Business;
import com.inovationware.toolkit.features.cycles.model.value.Health;
import com.inovationware.toolkit.features.cycles.model.value.Personal;
import com.inovationware.toolkit.features.cycles.model.value.Soul;

import java.util.List;

import lombok.Getter;

@Getter
public class Entity {

    private final String HEADLINE;
    private final String HEADLINE_IN_YORUBA;
    private final String HEADLINE_IN_BULGARIAN;
    private final String PERSONAL;
    private final String PERSONAL_IN_YORUBA;
    private final String PERSONAL_IN_BULGARIAN;
    private final String HEALTH;
    private final String HEALTH_IN_YORUBA;
    private final String HEALTH_IN_BULGARIAN;
    private final String BUSINESS;
    private final String BUSINESS_IN_YORUBA;
    private final String BUSINESS_IN_BULGARIAN;
    private final String SOUL;
    private final String SOUL_IN_YORUBA;
    private final String SOUL_IN_BULGARIAN;
    private final List<String> PERIOD_LISTING;
    private final Period PERIOD;
    private final String NAME;
    @Getter
    private final Personal personalToken;
    @Getter
    private final Soul soulToken;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Entity(int day, int month, String name) {
        Soul soul = new Soul(day, month, name);
        Personal personal = new Personal(day, month, name);

        this.NAME = name;
        this.PERIOD_LISTING = personal.getListing();
        this.PERIOD = personal.getPeriod();


        this.HEADLINE = soul.getHeadline() + "\n\n" + personal.getHeadline();
        this.HEADLINE_IN_YORUBA = soul.getHeadlineInYoruba() + "\n\n" + personal.getHeadlineInYoruba();
        this.HEADLINE_IN_BULGARIAN = soul.getHeadlineInBulgarian() + "\n\n" + personal.getHeadlineInBulgarian();

        this.PERSONAL = personal.getDetails();
        this.PERSONAL_IN_YORUBA = personal.getDetailsInYoruba();
        this.PERSONAL_IN_BULGARIAN = personal.getDetailsInBulgarian();

        this.SOUL = soul.getDetails();
        this.SOUL_IN_YORUBA = soul.getDetailsInYoruba();
        this.SOUL_IN_BULGARIAN = soul.getDetailsInBulgarian();

        Health health = new Health();
        this.HEALTH = health.constructDetails(this.PERIOD);
        this.HEALTH_IN_YORUBA = health.constructDetailsInYoruba(this.PERIOD);
        this.HEALTH_IN_BULGARIAN = health.constructDetailsInBulgarian(this.PERIOD);

        Business business = new Business();
        this.BUSINESS = business.constructDetails(this.PERIOD);
        this.BUSINESS_IN_YORUBA = business.constructDetailsInYoruba(this.PERIOD);
        this.BUSINESS_IN_BULGARIAN = business.constructDetailsInBulgarian(this.PERIOD);

        this.personalToken = personal;
        this.soulToken = soul;
    }


}
