package com.tencent.tws.pluginhost.plugindebug;

import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tws.pluginhost.R;
import com.tencent.tws.sharelib.SharePOJO;
import com.tws.plugin.content.PluginDescriptor;
import com.tws.plugin.core.PluginLauncher;
import com.tws.plugin.core.annotation.PluginContainer;
import com.tws.plugin.manager.PluginManagerHelper;

public class PluginDetailActivity extends Activity {

	private ViewGroup mRoot;
	private boolean mIsHostDependPlugin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugin_detail);

		setTitle("插件详情");

		mRoot = (ViewGroup) findViewById(R.id.root);

		String pluginId = getIntent().getStringExtra("plugin_id");
		if (pluginId == null) {
			Toast.makeText(this, "缺少plugin_id参数", Toast.LENGTH_SHORT).show();
			return;
		}

		final PluginDescriptor pluginDescriptor = PluginManagerHelper.getPluginDescriptorByPluginId(pluginId);
		if (pluginDescriptor != null) {
			initViews(pluginDescriptor);
		}

		if (pluginDescriptor != null && !PluginLauncher.instance().isRunning(pluginDescriptor.getPackageName())) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					PluginLauncher.instance().startPlugin(pluginDescriptor);
				}
			}).start();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initViews(PluginDescriptor pluginDescriptor) {
		TextView pluginIdView = (TextView) mRoot.findViewById(R.id.plugin_id);
		String packageName = pluginDescriptor.getPackageName();
		if (packageName.equalsIgnoreCase("com.pacewear.tws.phoneside.plugin.login")
				|| packageName.equalsIgnoreCase("com.pacewear.tws.phoneside.plugin.pair")) {
			mIsHostDependPlugin = true;
		}

		pluginIdView.setText("插件Id：" + packageName);

		TextView pluginVerView = (TextView) mRoot.findViewById(R.id.plugin_version);
		pluginVerView.setText("插件Version：" + pluginDescriptor.getVersion());

		TextView pluginDescipt = (TextView) mRoot.findViewById(R.id.plugin_description);
		pluginDescipt.setText("插件Description：" + pluginDescriptor.getDescription());

		TextView pluginInstalled = (TextView) mRoot.findViewById(R.id.plugin_installedPath);
		pluginInstalled.setText("插件安装路径：" + pluginDescriptor.getInstalledPath());

		TextView pluginStandalone = (TextView) mRoot.findViewById(R.id.isstandalone);
		pluginStandalone.setText("独立插件：" + (pluginDescriptor.isStandalone() ? "是" : "否"));

		LinearLayout pluginView = (LinearLayout) mRoot.findViewById(R.id.plugin_items);

		addButton(pluginView, pluginDescriptor.isStandalone(), pluginDescriptor.getFragments(), "Fragment",
				pluginDescriptor.getPackageName());

		addButton(pluginView, pluginDescriptor.isStandalone(), pluginDescriptor.getActivitys(), "Activity");

		addButton(pluginView, pluginDescriptor.isStandalone(), pluginDescriptor.getServices(), "Service");

		addButton(pluginView, pluginDescriptor.isStandalone(), pluginDescriptor.getReceivers(), "Receiver");
	}

	private void addButton(LinearLayout pluginView, final boolean isStandalone, HashMap<String, ?> map,
			final String type) {
		addButton(pluginView, isStandalone, map, type, null);
	}

	private void addButton(LinearLayout pluginView, final boolean isStandalone, HashMap<String, ?> map,
			final String type, final String packageName) {
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {

			final String className = keys.next();

			TextView tv = new TextView(this);
			// 这个判断仅仅是为了方便debug，在实际开发中，类型一定是已知的
			tv.append("插件类型：" + type);
			pluginView.addView(tv);

			tv = new TextView(this);
			tv.append("插件ClassName ： " + className);
			pluginView.addView(tv);

			if (!mIsHostDependPlugin) {
				Button btn = new Button(this);
				btn.setText("点击打开");
				btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						// 这个判断仅仅是为了方便debug，在实际开发中，类型一定是已知的
						if (type.equals("Service")) {

							Intent intent = new Intent();
							intent.setClassName(PluginDetailActivity.this, className);
							intent.putExtra("testParam", "testParam");
							if (!isStandalone) {
								intent.putExtra("paramVO", new SharePOJO("测试VO"));
							}
							startService(intent);
							// stopService(intent);

						} else if (type.equals("Receiver")) {// 这个判断仅仅是为了方便debug，在实际开发中，类型一定是已知的

							Intent intent = new Intent();
							intent.setClassName(PluginDetailActivity.this, className);
							intent.putExtra("testParam", "testParam");
							if (!isStandalone) {
								intent.putExtra("paramVO", new SharePOJO("测试VO"));
							}
							sendBroadcast(intent);

						} else if (type.equals("Activity")) {// 这个判断仅仅是为了方便debug，在实际开发中，类型一定是已知的

							Intent intent = new Intent();
							intent.setClassName(PluginDetailActivity.this, className);
							intent.putExtra("testParam", "testParam");
							if (!isStandalone) {
								intent.putExtra("paramVO", new SharePOJO("测试VO"));
							}
							startActivity(intent);

						} else if (type.equals("Fragment")) {
							// 插件中的Fragment分两类
							// 第一类是在插件提供的Activity中展示，就是一个普通的Fragment
							// 第二类是在宿主提供的Activity中展示，分为普通Fragment和特别处理过的fragment
							Intent pluginActivity = new Intent();
							pluginActivity.setClass(PluginDetailActivity.this, PluginTwsFragmentActivity.class);
							pluginActivity.putExtra(PluginFragmentActivity.FRAGMENT_ID_IN_PLUGIN, className);
							pluginActivity.putExtra(PluginContainer.FRAGMENT_PLUGIN_ID, packageName);
							pluginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(pluginActivity);
						}
					}
				});
				pluginView.addView(btn);
			}

			if (Build.VERSION.SDK_INT >= 14) {
				Space space = new Space(this);
				space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 25));
				pluginView.addView(space);
			}

		}
	}

}
