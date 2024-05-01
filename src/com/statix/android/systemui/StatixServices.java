/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui;

import android.app.AlarmManager;
import android.content.Context;

import com.android.systemui.Dumpable;
import com.android.systemui.VendorServices;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.power.domain.interactor.PowerInteractor;
import com.android.systemui.shade.NotificationShadeWindowView;
import com.android.systemui.shade.ShadeViewController;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.util.wakelock.WakeLockLogger;

import com.statix.android.systemui.elmyra.ElmyraService;
import com.statix.android.systemui.res.R;
import com.statix.android.systemui.smartpixels.SmartPixelsReceiver;

import java.io.PrintWriter;
import java.util.ArrayList;

import javax.inject.Inject;

@SysUISingleton
public class StatixServices extends VendorServices {

    private final ArrayList<Object> mServices = new ArrayList<>();
    private final ActivityStarter mActivityStarter;
    private final AlarmManager mAlarmManager;
    private final AssistManager mAssistManager;
    private final FlashlightController mFlashlightController;
    private final NotificationShadeWindowView mNotificationShadeWindowView;
    private final PowerInteractor mPowerInteractor;
    private final ShadeViewController mShadeViewController;
    private final WakeLockLogger mWakelockLogger;

    private final Context mContext;

    @Inject
    public StatixServices(
            Context context,
            ActivityStarter activityStarter,
            AlarmManager alarmManager,
            AssistManager assistManager,
            FlashlightController flashlightController,
            NotificationShadeWindowView notificationShadeWindowView,
            PowerInteractor powerInteractor,
            ShadeViewController shadeViewController,
            WakeLockLogger wakeLockLogger) {
        super();
        mActivityStarter = activityStarter;
        mAlarmManager = alarmManager;
        mAssistManager = assistManager;
        mContext = context;
        mFlashlightController = flashlightController;
        mNotificationShadeWindowView = notificationShadeWindowView;
        mPowerInteractor = powerInteractor;
        mShadeViewController = shadeViewController;
        mWakelockLogger = wakeLockLogger;
    }

    @Override
    public void start() {
        addService(new SmartPixelsReceiver(mContext));
        if (mContext.getPackageManager().hasSystemFeature("android.hardware.context_hub")
                && mContext.getPackageManager()
                        .hasSystemFeature("android.hardware.sensor.assist")) {
            addService(new ElmyraService(mContext, mAssistManager, mFlashlightController));
        }
    }

    @Override
    public void dump(PrintWriter printWriter, String[] strArr) {
        for (int i = 0; i < mServices.size(); i++) {
            if (mServices.get(i) instanceof Dumpable) {
                ((Dumpable) mServices.get(i)).dump(printWriter, strArr);
            }
        }
    }

    private void addService(Object obj) {
        if (obj != null) {
            mServices.add(obj);
        }
    }
}
