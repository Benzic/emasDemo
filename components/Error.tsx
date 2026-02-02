import { Button, View } from 'react-native';

export default function App() {
    const testCrash = () => {
        // 主动抛出异常，触发 APM 崩溃采集
        throw new Error("Test APM Crash from RN JS Layer");
    };

    return (
        <View >
            <Button title="点击触发崩溃" onPress={testCrash} />
        </View>
    );
}