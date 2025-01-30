package com.inovationware.toolkit.cycles.library;

import android.widget.TextView;

/**
 * Your Activity implements this interface, passed as ActivityName.this wherever needed as argument in CyclesFacade. DropDown is equivalent to Spinner.
 */
public interface LanguageViewSource {
    TextView getLanguageDropDown();
}
