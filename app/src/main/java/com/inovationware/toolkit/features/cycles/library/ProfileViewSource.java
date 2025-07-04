package com.inovationware.toolkit.features.cycles.library;

import android.widget.TextView;

/**
 * Your Activity implements this interface, passed as ActivityName.this wherever needed as argument in CyclesFacade. DropDown is equivalent to Spinner.
 */
public interface ProfileViewSource {
    TextView getDayDropDown();
    TextView getMonthDropDown();
    TextView getProfileDropDown();
}
