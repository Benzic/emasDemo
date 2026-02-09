// app/index.js - Expo Router 根路由（必填）
import { Text, View } from 'react-native';

export default function Home() {
  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Text>Expo Router Home</Text>
    </View>
  );
}