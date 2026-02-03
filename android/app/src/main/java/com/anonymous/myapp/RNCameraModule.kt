package com.anonymous.myapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RNCameraModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext), ActivityEventListener {

    // 常量定义
    private val TAG = "RNCameraModule"
    private val REQUEST_CAMERA_PERMISSION = 1001
    private val REQUEST_TAKE_PHOTO = 1002

    // 存储 RN 回调和当前图片路径（可空类型，符合 Kotlin 空安全）
    private var cameraCallback: Callback? = null
    private var currentPhotoPath: String? = null

    init {
        // 注册 Activity 事件监听器，接收相机返回结果
        reactContext.addActivityEventListener(this)
    }

    // 必须实现：返回模块名（RN 端通过该名称调用）
    override fun getName(): String {
        return "RNCameraModule"
    }

    // 暴露给 RN 调用的方法：takePhoto
    @ReactMethod
    fun takePhoto(callback: Callback) {
        this.cameraCallback = callback

        // 1. 检查相机权限
        val currentActivity = reactApplicationContext.currentActivity
        if (currentActivity == null) {
            cameraCallback?.invoke("无法获取当前页面上下文，无法调用相机", null)
            return
        }

        if (ContextCompat.checkSelfPermission(
                reactApplicationContext,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 申请相机权限：使用非空的 currentActivity（已做空判断）
            ActivityCompat.requestPermissions(
                currentActivity,
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
            return
        }

        // 2. 权限已授予，启动系统相机
        launchCamera(currentActivity)
    }

    // 辅助方法：启动系统相机（传入非空的 currentActivity，避免后续空判断）
    private fun launchCamera(currentActivity: Activity) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // 确保有相机应用可以处理该 Intent
        if (takePictureIntent.resolveActivity(reactApplicationContext.packageManager) == null) {
            cameraCallback?.invoke("没有可用的相机应用", null)
            return
        }

        // 3. 创建图片文件，用于存储拍摄的照片
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            cameraCallback?.invoke("创建图片文件失败：${ex.message}", null)
            return
        }

        if (photoFile == null) {
            cameraCallback?.invoke("创建图片文件失败，文件为空", null)
            return
        }

        // 4. 获取图片 URI（Android 7.0+ 使用 FileProvider）
        val photoURI: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                reactApplicationContext,
                "${reactApplicationContext.packageName}.fileprovider",
                photoFile
            )
        } else {
            Uri.fromFile(photoFile)
        }

        currentPhotoPath = photoFile.absolutePath
        // 5. 将图片 URI 传入 Intent，相机拍摄后保存到该路径
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        // 6. 启动相机 Activity（使用非空的 currentActivity）
        currentActivity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
    }

    // 辅助方法：创建唯一的图片文件（彻底修正：timeStamp_ → timeStamp，无下划线）
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // 生成唯一文件名
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
        val imageFileName = "JPEG_$timeStamp" // 彻底修正：移除多余下划线，解决 Unresolved reference
        // 获取应用私有图片目录
        val storageDir = reactApplicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: throw IOException("无法获取应用私有图片目录")

        // 创建临时文件
        return File.createTempFile(
            imageFileName,  /* 前缀 */
            ".jpg",         /* 后缀 */
            storageDir      /* 存储目录 */
        )
    }

    // 关键修正：匹配 ActivityEventListener 接口的 NON-NULL 方法签名（无 ?，非可空）
    override fun onActivityResult(
        activity: Activity,       // 非空：Activity 而非 Activity?
        requestCode: Int,
        resultCode: Int,
        data: Intent?             // 注意：data 允许为 null（相机拍照无返回数据，仅保存文件）
    ) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // 拍照成功，回调 RN 传递图片 URI（file:// 格式）
                    val imageUri = "file://${currentPhotoPath ?: ""}"
                    cameraCallback?.invoke(null, imageUri)
                }
                Activity.RESULT_CANCELED -> {
                    // 用户取消拍照
                    cameraCallback?.invoke("用户取消拍照", null)
                }
                else -> {
                    cameraCallback?.invoke("拍照失败，未知返回结果", null)
                }
            }
        }
    }

    // 关键修正：匹配 ActivityEventListener 接口的 NON-NULL 方法签名（无 ?，非可空）
    override fun onNewIntent(intent: Intent) {
        // 无需实现任何逻辑，仅满足接口实现要求
    }
}