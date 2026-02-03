package com.anonymous.myapp

import android.app.Application
import android.content.res.Configuration

import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeApplicationEntryPoint.loadReactNative
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.ReactHost
import com.facebook.react.common.ReleaseLevel
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint
import com.facebook.react.defaults.DefaultReactNativeHost

import expo.modules.ApplicationLifecycleDispatcher
import expo.modules.ReactNativeHostWrapper

// 新增：1. 导入你的自定义 RNCameraPackage（关键，必须添加）
import com.anonymous.myapp.RNCameraPackage

// 新增：APM 相关的 import
import com.aliyun.emas.apm.Apm
import com.aliyun.emas.apm.ApmOptions
import com.aliyun.emas.apm.crash.ApmCrashAnalysisComponent
import com.aliyun.emas.apm.mem.monitor.ApmMemMonitorComponent
import com.aliyun.emas.apm.performance.ApmPerformanceComponent
import com.aliyun.emas.apm.remote.log.ApmRemoteLogComponent

class MainApplication : Application(), ReactApplication {

  override val reactNativeHost: ReactNativeHost = ReactNativeHostWrapper(
    this,
    object : DefaultReactNativeHost(this) {
      override fun getPackages(): List<ReactPackage> =
        PackageList(this).packages.apply {
          // Packages that cannot be autolinked yet can be added manually here, for example:
          // add(MyReactNativePackage())

          // 新增：2. 注册自定义 RNCameraPackage（关键，这一行是核心修改）
          add(RNCameraPackage())
        }

      override fun getJSMainModuleName(): String = ".expo/.virtual-metro-entry"

      override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

      override val isNewArchEnabled: Boolean = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
    }
  )

  override val reactHost: ReactHost
    get() = ReactNativeHostWrapper.createReactHost(applicationContext, reactNativeHost)

  override fun onCreate() {
    super.onCreate()
    DefaultNewArchitectureEntryPoint.releaseLevel = try {
      ReleaseLevel.valueOf(BuildConfig.REACT_NATIVE_RELEASE_LEVEL.uppercase())
    } catch (e: IllegalArgumentException) {
      ReleaseLevel.STABLE
    }
    loadReactNative(this)
    ApplicationLifecycleDispatcher.onApplicationCreate(this)

    // 新增：APM 初始化代码（替换成你的 APP_KEY、APP_SECRET 等信息）
    Apm.preStart(ApmOptions.Builder()
      .setApplication(this)
      .setAppKey("335660979")
      .setAppSecret("f8d01adfc6e940659df051d0bf5748d5")
      .setAppRsaSecret("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDcgM646GNGU2B5ADm/29e164oICmC3rqmcVg3uJ76IM41dl7GfuC57rgbMybIDyOKARTbmZXH4ihlo5U4g78HpixUUaYC1OYLaY1KQl5xdq77e1YRW+D3VItbTkRYu7JT8wdwu0N7YEhmvtyZYXJxh8amYhSrw4anBhTEcjwmEAQIDAQAB")
      .addComponent(ApmCrashAnalysisComponent::class.java)
      .addComponent(ApmMemMonitorComponent::class.java)
      .addComponent(ApmRemoteLogComponent::class.java)
      .addComponent(ApmPerformanceComponent::class.java)
      .openDebug(true)
      .build()
    )
    // 第二步：开启监控（start）
    Apm.start()
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    ApplicationLifecycleDispatcher.onConfigurationChanged(this, newConfig)
  }
}