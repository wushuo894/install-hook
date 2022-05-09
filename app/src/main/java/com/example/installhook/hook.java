package com.example.installhook;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log(lpparam.packageName);
        if (!"com.android.packageinstaller".equals(lpparam.packageName)) {
            return;
        }

        XposedHelpers.findAndHookMethod("com.android.packageinstaller.PackageInstallerActivity", lpparam.classLoader, "bindUi", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object mAlert = XposedHelpers.getObjectField(param.thisObject, "mAlert");
                Object button = XposedHelpers.callMethod(mAlert, "getButton", -1);
                XposedHelpers.callMethod(button,"callOnClick");
                super.afterHookedMethod(param);
            }
        });

        XposedHelpers.findAndHookMethod("com.android.packageinstaller.InstallSuccess", lpparam.classLoader, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object mAlert = XposedHelpers.getObjectField(param.thisObject, "mAlert");
                Object button = XposedHelpers.callMethod(mAlert, "getButton", -1);
                XposedHelpers.callMethod(button,"callOnClick");
                super.afterHookedMethod(param);
            }
        });
    }

}
