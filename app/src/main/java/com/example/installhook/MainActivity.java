package com.example.installhook;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainActivity extends AppCompatActivity implements IXposedHookLoadPackage {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
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