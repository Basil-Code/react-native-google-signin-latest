import { useEffect } from 'react';
import { Text, View, StyleSheet, TouchableOpacity } from 'react-native';
import { GoogleSignin } from 'react-native-google-signin-latest';
import { WEB_CLIENT_ID } from '@env';

export default function App() {

  useEffect(() => {
    // WEB_CLIENT_ID ==> Add this value to your .env in the example folder
    GoogleSignin.configure({ webClientId: WEB_CLIENT_ID, type: 'siwg' })
  }, []);

  const handleSignIn = async () => {
    GoogleSignin.configure({ webClientId: WEB_CLIENT_ID, type: 'siwc' })
    await GoogleSignin.signIn().then((res) => {
      console.log('GoogleSignin.signIn_res ', res);
    }).catch((err) => {
      console.error('GoogleSignin.signIn_err ', err);
    })
  }

  const handleSignUp = async () => {
    GoogleSignin.configure({ webClientId: WEB_CLIENT_ID, type: 'siwg' })
    await GoogleSignin.signUp().then((res) => {
      console.log('GoogleSignin.signUp_res ', res);
    }).catch((err) => {
      console.error('GoogleSignin.signUp_err ', err);
    })
  }

  const handleSignOut = async () => {
    await GoogleSignin.signOut().then((res) => {
      console.log('GoogleSignin.signOut_res ', res);
    }).catch((err) => {
      console.error('GoogleSignin.signOut_err ', err);
    })
  }

  return (
    <View style={styles.container}>
      <TouchableOpacity style={{marginTop: 50, backgroundColor: 'white', borderRadius: 100, padding: 20}} onPress={handleSignIn}>
        <Text style={{color: '#000', fontWeight: 800}}>Google Sign In</Text>
      </TouchableOpacity>
      <TouchableOpacity style={{marginTop: 50, backgroundColor: 'white', borderRadius: 100, padding: 20}} onPress={handleSignUp}>
        <Text style={{color: '#000', fontWeight: 800}}>Google Sign Up</Text>
      </TouchableOpacity>
      <TouchableOpacity style={{marginTop: 50, backgroundColor: 'white', borderRadius: 100, padding: 20}} onPress={handleSignOut}>
        <Text style={{color: '#000', fontWeight: 800}}>Google Sign Out</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
