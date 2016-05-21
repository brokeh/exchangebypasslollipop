package com.wudi.ebpforl;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.lang.NoSuchMethodError;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;
import static de.robv.android.xposed.XposedHelpers.setIntField;

public class Main implements IXposedHookLoadPackage {
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.google.android.gm.exchange") || lpparam.packageName.equals("com.android.exchange"))
        {
            try
            {
                XposedBridge.log("Loaded app v3: " + lpparam.packageName);

                findAndHookMethod("com.android.exchange.adapter.ProvisionParser", lpparam.classLoader, "setPolicy","com.android.emailcommon.provider.Policy", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        setIntField(param.args[0], "mPasswordMode", 0);
                        setIntField(param.args[0], "mPasswordMinLength", 0);
                        setBooleanField(param.args[0], "mRequireRemoteWipe", false);
                        setBooleanField(param.args[0], "mRequireEncryption", false);
                        setBooleanField(param.args[0], "mRequireEncryptionExternal", false);
                        setBooleanField(param.args[0], "mRequireManualSyncWhenRoaming", false);
                        setBooleanField(param.args[0], "mDontAllowCamera", false);
                        setBooleanField(param.args[0], "mDontAllowAttachments", false);
                        setBooleanField(param.args[0], "mDontAllowHtml", false);
                        XposedBridge.log("All Set for setPolicy");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                });
            }
            catch (Exception e)
            {
                XposedBridge.log("Exception: " + e.getMessage());
            }
        }
        else if (lpparam.packageName.equals("com.microsoft.office.outlook"))
        {
            try
            {
                XposedBridge.log("Loaded app v3: " + lpparam.packageName);

                try
                {
	                findAndHookMethod("com.acompli.accore.util.OutlookDevicePolicy", lpparam.classLoader, "requiresDeviceManagement", new XC_MethodHook() {
	                    @Override
	                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
	                    }

	                    @Override
	                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
	                        Boolean ret = false;
	                        param.setResult(ret);
	                        XposedBridge.log("All Set for requiresDeviceManagement");
	                    }
	                });

	                findAndHookMethod("com.acompli.accore.util.OutlookDevicePolicy", lpparam.classLoader, "isPolicyApplied", new XC_MethodHook() {
	                    @Override
	                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
	                    }

	                    @Override
	                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
	                        Boolean ret = true;
	                        param.setResult(ret);
	                        XposedBridge.log("All Set for isPolicyApplied");
	                    }
	                });
	            }
	            catch (NoSuchMethodError e)
	            {
                	XposedBridge.log("Got NoSuchMethodError " + e.getMessage() + ". Might be a newer version of outlook. Trying different approach.");

	                findAndHookConstructor("com.acompli.accore.util.OutlookDevicePolicy", lpparam.classLoader, String.class, boolean.class, boolean.class, boolean.class, int.class, int.class, int.class, new XC_MethodHook() {
	                    @Override
	                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
	                    	param.args[1] = true; //isPolicyApplied
	                    	param.args[2] = false; //isPasswordRequired
	                        XposedBridge.log("All Set for constructor");
	                    }

	                    @Override
	                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
	                    }
	                });

	            }
            }
            catch (Exception e)
            {
                XposedBridge.log("Exception: " + e.getMessage());
            }
        }


    }
}