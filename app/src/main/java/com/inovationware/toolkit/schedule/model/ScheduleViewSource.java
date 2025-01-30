package com.inovationware.toolkit.schedule.model;

import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public interface ScheduleViewSource {

    List<String> getApps();
    TextView getAnnounceTextView();
    TextView getHourDropDown();
    TextView getMinuteDropDown();
    TextView getMeridianDropDown();
    CheckBox getMonCheckbox();
    CheckBox getTueCheckbox();
    CheckBox getWedCheckbox();
    CheckBox getThuCheckbox();
    CheckBox getFriCheckbox();
    CheckBox getSatCheckbox();
    CheckBox getSunCheckbox();
    CheckBox getTimeoutCheckbox();
    TextView getTimeoutTextView();
}
