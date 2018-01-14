package com.rick.tws.pluginhost.main;

import android.content.Context;

import com.rick.tws.framework.HostProxy;
import com.rick.tws.pluginhost.main.widget.Hotseat;
import com.tws.plugin.content.PluginDescriptor;
import com.tws.plugin.core.PluginApplication;
import com.tws.plugin.core.PluginLauncher;
import com.tws.plugin.core.PluginLoader;
import com.tws.plugin.manager.PluginManagerHelper;
import com.tws.plugin.util.ProcessUtil;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import qrom.component.log.QRomLog;

/**
 * Created by Administrator on 2017/11/6 0006.
 */
public class HostApplication extends PluginApplication {
    private static final String TAG = "HostApplication";

    private String mFouceTabClassId = Hotseat.HOST_HOME_FRAGMENT;

    @Override
    public void onCreate() {
        super.onCreate();

        if (ProcessUtil.isPluginProcess(this)) {
            // 提前启动宿主Host的依赖插件[比如：手表助手DM的启动依赖登录插件]
            startAppDependentPlugin();

            // 随DM启动的插件 时机调整到application的onCreate里面
            startNeedPowerbootPlugin();

            //加载插件
            PluginLoader.loadPlugins(this);
        }
    }

    private void startAppDependentPlugin() {
        // 宿主的启动依赖一些插件，需要提前加载好这些插件
//        final String loginPackageName = "com.*.plugin.login";
//        final String loginPluginFileName = "TwsPluginLogin.apk";
//        boolean hasLoginPlugin = false;
//
//        Collection<PluginDescriptor> plugins = PluginManagerHelper.getPlugins();
//        Iterator<PluginDescriptor> itr = plugins.iterator();
//        while (itr.hasNext()) {
//            final PluginDescriptor pluginDescriptor = itr.next();
//            QRomLog.d(TAG, "plugin packageName=" + pluginDescriptor.getPackageName());
//            String packageName = pluginDescriptor.getPackageName();
//            if (packageName.equals(loginPackageName)) {
//                PluginLauncher.instance().startPlugin(pluginDescriptor);
//                hasLoginPlugin = true;
//            }
//            if (hasLoginPlugin) {
//                return;
//            }
//        }
//        if (!hasLoginPlugin) {
//            boolean isInstallPluginDirLogin = PluginLoader.copyAndInstall("plugins" + File.separator
//                    + loginPluginFileName);
//            QRomLog.d(TAG, "isInstallPluginDirLogin = " + isInstallPluginDirLogin);
//            PluginLauncher.instance().startPlugin(loginPackageName);
//        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        HostProxy.setApplication(this);
    }

    public String getFouceTabClassId() {
        return mFouceTabClassId;
    }
}
