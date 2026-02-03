import * as ImagePicker from 'expo-image-picker';
import React, { useState } from 'react';
import { Alert, Button, Image, StyleSheet, View } from 'react-native';

export default function CameraTakePhotoAndDisplay() {
    // 定义状态存储拍照后的图片 URI（核心：图片展示需要依赖这个 URI）
    const [photoUri, setPhotoUri] = useState<string | null>(null);

    // 核心方法：调用相机拍照并获取图片 URI
    const takePhotoWithCamera = async () => {
        try {
            // 1. 请求相机权限（必须步骤，否则无法调用相机）
            const { status } = await ImagePicker.requestCameraPermissionsAsync();
            if (status !== 'granted') {
                Alert.alert('权限不足', '请授予相机权限才能正常拍照');
                return;
            }

            // 2. 调用相机，启动拍照界面
            const result = await ImagePicker.launchCameraAsync({
                // 配置选项：按需调整
                allowsEditing: false, // 是否允许拍照后编辑图片（裁剪等）
                aspect: [4, 3], // 图片比例（宽:高）
                quality: 0.8, // 图片质量（0-1，1 为最高质量，文件最大）
                mediaTypes: ImagePicker.MediaTypeOptions.Images, // 只允许拍摄图片（排除视频）
            });

            // 3. 处理拍照结果：用户未取消拍照，则获取图片 URI
            if (!result.canceled) {
                // result.assets[0].uri 是图片的本地存储路径（核心：用于 Image 组件展示）
                setPhotoUri(result.assets[0].uri);
            }
        } catch (error) {
            console.error('拍照失败：', error);
            Alert.alert('错误', '拍照过程中出现异常，请重试');
        }
    };

    return (
        <View style={styles.container}>
            {/* 拍照按钮 */}
            <Button title="点击调用相机拍照" onPress={takePhotoWithCamera} />

            {/* 展示拍照后的图片：只有 photoUri 不为 null 时才渲染 Image 组件 */}
            {photoUri && (
                <Image
                    source={{ uri: photoUri }} // 注意：本地图片 URI 需放在对象的 uri 属性中
                    style={styles.photo}
                    resizeMode="contain" // 保持图片比例，完整展示在组件内
                />
            )}
        </View>
    );
}

// 样式配置
const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#f5f5f5',
        padding: 20,
    },
    photo: {
        width: '100%',
        height: 400,
        marginTop: 20,
        borderRadius: 10,
        backgroundColor: '#eee',
    },
});