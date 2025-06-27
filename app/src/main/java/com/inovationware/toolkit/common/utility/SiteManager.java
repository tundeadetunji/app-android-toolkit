package com.inovationware.toolkit.common.utility;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.inovationware.toolkit.R;

import java.util.Arrays;

import static com.inovationware.toolkit.common.domain.DomainObjects.EMPTY_STRING;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_LAST_SEARCH_TERM_KEY;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_LAST_VISITED_SITE_KEY;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_SITES_KEY;

public final class SiteManager {

    //private final Context context;
    private static SiteManager instance;
    private ArrayAdapter<String> dropdownAdapter;
    private SharedPreferencesManager store;


    private SiteManager(Context context) {
        //this.context = context;
        store = SharedPreferencesManager.getInstance();
        if (Code.clean(store.getString(context,SHARED_PREFERENCES_SITES_KEY, EMPTY_STRING)).isEmpty()) {
            setDefaultSites(context);
        }
    }

    public static SiteManager getInstance(Context context) {
        if (instance == null) instance = new SiteManager(context);
        return instance;
    }

    public String[] getSites(Context context) {
        return store.getString( context,SHARED_PREFERENCES_SITES_KEY, EMPTY_STRING).split("\n");
    }

    public boolean exists(Context context,String site) {
        return Arrays.asList(getSites(context)).contains(Code.clean(site));
    }

    private String[] setDefaultSites(Context context) {
        StringBuilder initialList = new StringBuilder();
        initialList.append("https://docs.oracle.com\n");
        initialList.append("https://stackoverflow.com\n");
        initialList.append("https://www.quora.com\n");
        initialList.append("https://www.baeldung.com\n");
        initialList.append("https://www.glassdoor.com\n");
        initialList.append("https://www.wikipedia.org\n");
        initialList.append("https://news.google.com\n");
        store.setString( context,SHARED_PREFERENCES_SITES_KEY, initialList.toString());
        return getSites(context);
    }

    /*youtube.com, *LinkedIn learning, *https://www.wsj.com,  instagram, twitter, facebook); flag activity stay on for net timer mobile;
    Integrate to system (send to); settings screen; Net Timer Mobile button should show 'Modify Timer' regardless; increase pause between read loud from 2 to 3-5 seconds;
*/



    public String[] addSite(Context context, String site) {
        if (!siteIsValid(site)) return new String[]{};
        if (!Arrays.asList(getSites(context)).contains(Code.clean(site)))
            store.setString(context, SHARED_PREFERENCES_SITES_KEY, store.getString( context,SHARED_PREFERENCES_SITES_KEY, EMPTY_STRING) + "\n" + Code.clean(site));
        return getSites(context);
    }

    public String[] clearSites(Context context) {
        return setDefaultSites(context);
    }

    public void setDropDown(Context context, AutoCompleteTextView dropdown, String[] newList) {
        dropdownAdapter = new ArrayAdapter<String>(context, R.layout.default_drop_down, newList);
        dropdown.setAdapter(null);
        dropdown.setAdapter(dropdownAdapter);
    }

    public String getLastVisitedSite(Context context){
        return store.getString( context,SHARED_PREFERENCES_LAST_VISITED_SITE_KEY, "");
    }

    public void setLastVisitedSite(Context context, String site){
        if (siteIsValid(site)) store.setString(  context,SHARED_PREFERENCES_LAST_VISITED_SITE_KEY, Code.clean(site));
    }

    public void clearLastVisitedSite(Context context){
        store.setString(  context,SHARED_PREFERENCES_LAST_VISITED_SITE_KEY, "");
    }

    public String getLastSearchTerm(Context context){
        return store.getString(context,SHARED_PREFERENCES_LAST_SEARCH_TERM_KEY, "");
    }

    public void setLastSearchTerm(Context context, String term){
        store.setString(  context,SHARED_PREFERENCES_LAST_SEARCH_TERM_KEY, Code.clean(term));
    }

    public void clearLastSearchTerm(Context context){
        store.setString(  context,SHARED_PREFERENCES_LAST_SEARCH_TERM_KEY, "");
    }

    private boolean siteIsValid(String site){
        return Code.clean(site).startsWith("http");
    }
}
