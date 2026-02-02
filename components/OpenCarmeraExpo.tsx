import * as ImagePicker from 'expo-image-picker';
import React from 'react';
import { Alert, Button, Image, StyleSheet, View } from 'react-native';

export default function QuickCamera() {
    const [photoUri, setPhotoUri] = React.useState<string | null>(null);

    // 打开相机拍照
    const launchCamera = async () => {
        // 1. 请求相机权限
        const { status } = await ImagePicker.requestCameraPermissionsAsync();
        if (status !== 'granted') {
            Alert.alert('权限不足', '请开启相机权限才能拍照');
            return;
        }

        // 2. 打开相机
        const result = await ImagePicker.launchCameraAsync({
            mediaTypes: ImagePicker.MediaTypeOptions.Images, // 仅允许拍照（不录像）
            allowsEditing: true, // 允许编辑（裁剪等）
            quality: 0.8, // 照片质量
        });

        // 3. 处理拍照结果（未取消则保存 uri）
        if (!result.canceled && result.assets.length > 0) {
            setPhotoUri(result.assets[0].uri);
        }
    };

    return (
        <View style={styles.container}>
            {/* 照片预览 */}
            {photoUri && (
                <Image source={{ uri: photoUri }} style={styles.photo} />
            )}

            {/* 打开相机按钮 */}
            <Button title="打开相机快速拍照" onPress={launchCamera} />
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 20,
    },
    photo: {
        width: 300,
        height: 300,
        marginBottom: 20,
        borderRadius: 10,
    },
});