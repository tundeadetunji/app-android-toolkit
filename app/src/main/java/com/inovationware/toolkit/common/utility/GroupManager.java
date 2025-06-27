package com.inovationware.toolkit.common.utility;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.inovationware.toolkit.R;

import java.util.Arrays;

import static com.inovationware.toolkit.common.domain.DomainObjects.EMPTY_STRING;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_DEFAULT_DEVICE;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_MACHINES_KEY;

public class GroupManager {

    private static GroupManager instance;
    private ArrayAdapter<String> dropdownAdapter;
    private SharedPreferencesManager store;
    private GroupManager() {
        store = SharedPreferencesManager.getInstance();
    }


    public static GroupManager getInstance() {
        if (instance == null) instance = new GroupManager();
        return instance;
    }

    public String[] list(Context context) {
        return store.getString(context, SHARED_PREFERENCES_MACHINES_KEY, EMPTY_STRING).split("\n");
    }

    public String[] list(Context context, boolean excludeGroup) {
        if (excludeGroup) return list(context);

        /*if (Arrays.asList(list(context)).contains(clean(store.getID(context)))){
            return list(context);
        }*/

        String[] saved = list(context);
        //String[] result = new String[list(context).length + 1];
        String[] result =Arrays.copyOf(saved, saved.length + 1);
        result[result.length - 1] = store.getID(context);
        return result;
    }

    /*private boolean exists(String machine) {
        return Arrays.asList(list()).contains(clean(machine));
    }*/

    public String[] addNew(Context context,String machine) {
        if (!Arrays.asList(list(context)).contains(Code.clean(machine)))
            store.setString(context,  SHARED_PREFERENCES_MACHINES_KEY, store.getString( context,SHARED_PREFERENCES_MACHINES_KEY, EMPTY_STRING) + "\n" + Code.clean(machine));
        return list(context);
    }

    public String[] removeAll(Context context) {
        performRemoveAllMachines(context);
        return new String[]{};
    }

    public String[] removeAll(Context context,TextView machinesDropDown) {
        performRemoveAllMachines(context);
        machinesDropDown.setText("");
        return new String[]{};
    }

    void performRemoveAllMachines(Context context) {
        store.setString(context, SHARED_PREFERENCES_MACHINES_KEY, "");
        clearDefaultTarget(context);
    }

    public void setDropDown(Context context,AutoCompleteTextView dropdown, String[] newList) {
        dropdownAdapter = new ArrayAdapter<String>(context,R.layout.default_drop_down, newList);
        //dropdown.setAdapter(null);
        dropdown.setAdapter(dropdownAdapter);
    }

    public void setDropDown(Context context,AutoCompleteTextView dropdown, String[] newList, String text) {
        dropdown.setText(text);
        setDropDown(context,dropdown, newList);
    }

    public String getDefaultDevice(Context context) {
        return store.getString( context,SHARED_PREFERENCES_DEFAULT_DEVICE, EMPTY_STRING);
    }

    public void setDefaultDevice(Context context,String machine, TextView defaultTargetTextView) {
        if (isValid(machine)) {
            store.setString(   context,SHARED_PREFERENCES_DEFAULT_DEVICE, Code.clean(machine));
            defaultTargetTextView.setText(Code.clean(machine));
        }
    }

    public void clearDefaultTarget(Context context) {
        store.setString(   context,SHARED_PREFERENCES_DEFAULT_DEVICE, "");
    }

    private boolean isValid(String machine) {
        return !Code.isNothing(Code.clean(machine));
    }

    int machineCount(Context context) {
        return list(context).length;
    }

}
