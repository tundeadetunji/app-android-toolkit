package com.inovationware.toolkit.common.utility;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApkClient {
    private static ApkClient instance;
    public static ApkClient getInstance(){
        if(instance == null) instance = new ApkClient();
        return instance;
    }
    private ApkClient(){}

    private Map<String, String> cachedMap = null;

    public String[] packageNameListing(PackageManager pm, boolean includeSystemApps){
        Set<String> temp = packageUriListing(pm, includeSystemApps).keySet();
        String[] result = temp.toArray(new String[0]);
        Arrays.sort(result);
        return result;
    }

    public String getUri(String appName){
        return cachedMap.containsKey(appName) ? cachedMap.get(appName) : appName;
    }


    public Map<String, String> packageUriListing(PackageManager pm, boolean includeSystemApps) {
        if (cachedMap == null)
            cachedMap = getListing(pm, includeSystemApps);

        return cachedMap;
    }
    private Map<String, String> getListing(PackageManager pm, boolean includeSystemApps) {
        Map<String, String> result = new HashMap<>();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_META_DATA);

        if (includeSystemApps) {
            for (PackageInfo packageInfo : packages) {
                if (!result.containsKey(getAppName(packageInfo.applicationInfo, pm)))
                    result.put(getAppName(packageInfo.applicationInfo, pm), packageInfo.packageName);
            }
        } else {
            for (PackageInfo packageInfo : packages) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    if (!result.containsKey(getAppName(packageInfo.applicationInfo, pm)))
                        result.put(getAppName(packageInfo.applicationInfo, pm), packageInfo.packageName);
                }
            }
        }

        return result;
    }

    private String getAppName(ApplicationInfo info, PackageManager pm) {
        CharSequence label = pm.getApplicationLabel(info);
        return label != null ? label.toString() : info.packageName;
    }

}
