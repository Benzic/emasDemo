import React, { useState } from 'react';
import { Alert, Button, Image, NativeModules, StyleSheet, View } from 'react-native';

// 获取原生端暴露的相机模块（与原生端注册的模块名一致，必须同名）
const { RNCameraModule } = NativeModules;

export default function NativeCameraDemo() {
    const [photoUri, setPhotoUri] = useState<string | null>(null);

    // 调用原生相机方法
    const takePhotoWithNativeCamera = async () => {
        try {
            // 调用原生端暴露的 takePhoto 方法，通过回调获取图片 URI
            RNCameraModule.takePhoto((error: string, uri: string) => {
                if (error) {
                    Alert.alert('拍照失败', error);
                    return;
                }
                // 更新状态，展示图片
                setPhotoUri(uri);
            });
        } catch (error) {
            console.error('RN 调用原生相机异常：', error);
            Alert.alert('错误', '调用相机失败，请重试');
        }
    };

    return (
        <View style={styles.container}>
            <Button title="调用原生相机拍照" onPress={takePhotoWithNativeCamera} />

            {/* 展示拍照后的图片 */}
            {photoUri && (
                <Image
                    source={{ uri: photoUri }}
                    style={styles.photo}
                    resizeMode="contain"
                />
            )}
        </View>
    );
}

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