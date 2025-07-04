import { useEffect } from 'react';
import { Text, View, StyleSheet, TouchableOpacity } from 'react-native';
import { GoogleSignin, type GoogleSignInButtonError, type SignInError } from 'react-native-google-signin-latest';
import { WEB_CLIENT_ID } from '@env';

export default function App() {

  useEffect(() => {
    // WEB_CLIENT_ID ==> Add this value to your .env in the example folder
    GoogleSignin.configure({ webClientId: WEB_CLIENT_ID })
  }, []);

  const handleSignIn = async () => {
    await GoogleSignin.signIn({fallbackToSignInWithGoogleButton: true}).then((res) => {
      console.log('GoogleSignin.signIn_res ', res);
    }).catch((err: SignInError) => {
      console.error(`signIn_Error code: ${err?.code} message: ${err?.message}`,);
    })
  }

  const handleSignInWithGoogleButton = async () => {
    await GoogleSignin.signInWithGoogleButton().then((res) => {
      console.log('GoogleSignin.signInWithGoogleButton_res ', res);
    }).catch((err: GoogleSignInButtonError) => {
      console.error(`signInWithGoogleButton_Error code: ${err?.code} message: ${err?.message}`,);
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
      <TouchableOpacity style={{marginTop: 50, backgroundColor: 'white', borderRadius: 100, padding: 20}} onPress={handleSignInWithGoogleButton}>
        <Text style={{color: '#000', fontWeight: 800}}>Google Sign In With Google Button</Text>
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
