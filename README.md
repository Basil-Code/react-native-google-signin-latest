# react-native-google-signin-latest

Google signin Using latest implementation (Credential Manager). <br />
**__NOTE:__ It's still **Android** only package. Any contribution will be welcomed :)

## Installation

```sh
npm install react-native-google-signin-latest
```

If you are using Yarn:

```sh
yarn add react-native-google-signin-latest
```

## Usage

```js
import { GoogleSignin } from 'react-native-google-signin-latest';
import { WEB_CLIENT_ID } from '@env';
// ...

useEffect(() => {
  // WEB_CLIENT_ID ==> Add this value to your .env in the example folder
  GoogleSignin.configure({ webClientId: WEB_CLIENT_ID })
}, []);

const handleSignIn = async () => {
  await GoogleSignin.signIn().then((res) => {
    console.log('GoogleSignin.signIn_res ', res);
  }).catch((err) => {
    console.error('GoogleSignin.signIn_err ', err);
  })
}

const handleSignInWithGoogleButton = async () => {
  await GoogleSignin.signInWithGoogleButton().then((res) => {
    console.log('GoogleSignin.signInWithGoogleButton_res ', res);
  }).catch((err) => {
    console.error('GoogleSignin.signInWithGoogleButton_err ', err);
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
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
