package scholardesign.awei.com.flowercapturingrecognize.Utils;

import android.content.pm.PackageManager;

import com.awei.photopicker.GlobalApplication;

/**
 * Created by dell on 2017/3/29.
 */

public class Device {
    public static int getVersionCode() {
        int versionCode = 0;
        try {
            versionCode = GlobalApplication
                    .getContext()
                    .getPackageManager()
                    .getPackageInfo(GlobalApplication.getContext().getPackageName(),
                            0).versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }

    public static int getVersionCode(String packageName) {
        int versionCode = 0;
        try {
            versionCode = GlobalApplication.getContext().getPackageManager()
                    .getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }
}
