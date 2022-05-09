package com.example.installhook;

import android.os.Bundle;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log(lpparam.packageName);
        if (!"com.android.packageinstaller".equals(lpparam.packageName)) {
            return;
        }

        Class<?> aClass = lpparam.classLoader.loadClass("com.android.packageinstaller.oplus.common.FeatureOption");

        // 关闭安全检测
        XposedHelpers.setStaticBooleanField(aClass, "sIsSecurityPayEnable", false);
        // 允许多应用
        XposedHelpers.setStaticBooleanField(aClass, "sIsMultiAppSupported", true);
        // 不跳过细节
        XposedHelpers.setStaticBooleanField(aClass, "sIsSkipAppdetail", true);
        // 允许未授权的安装
        XposedHelpers.setStaticBooleanField(aClass, "sIsUnknownSourceAppInstallFeature", true);
        // 使用原生安装程序
        XposedHelpers.setStaticBooleanField(aClass, "sIsClosedSuperFirewall", true);

        // 自动开始安装
        XposedHelpers.findAndHookMethod("com.android.packageinstaller.PackageInstallerActivity", lpparam.classLoader, "bindUi", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object mAlert = XposedHelpers.getObjectField(param.thisObject, "mAlert");
                Object button = XposedHelpers.callMethod(mAlert, "getButton", -1);
                XposedHelpers.callMethod(button, "callOnClick");
                super.afterHookedMethod(param);
            }
        });

        // 安装成功自动点击完成
        XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.android.packageinstaller.InstallSuccess"), "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object mAlert = XposedHelpers.getObjectField(param.thisObject, "mAlert");
                Object button = XposedHelpers.callMethod(mAlert, "getButton", -2);
                XposedHelpers.callMethod(button, "callOnClick");
                super.afterHookedMethod(param);
            }
        });

    }
}
