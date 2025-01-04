import { NativeModules, Platform } from "react-native";

const LINKING_ERROR =
  `The package 'react-native-google-signin-latest' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const GoogleSigninLatest = NativeModules.GoogleSigninLatest
  ? NativeModules.GoogleSigninLatest
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const IS_Android = Platform.OS === 'android';

type configureParams = {
  webClientId: string
  type?: "siwg" | "siwc"
}

function configure(options: configureParams) {
  if (!IS_Android) {
    return Promise.reject(new Error("Platform is Not Yet Supported"));
  }
  
  if (!options.webClientId) {
    throw new Error('webClientId is null');
  }

  if (!options.type) {
    options.type = 'siwc'
    console.warn('type is null');
  }

  console.log('options ', options)
  // console.log('configure DONE!')
  return GoogleSigninLatest.configure(options);
}

async function signIn() {
  if (!IS_Android) return Promise.reject(new Error("Platform is Not Yet Supported"));
  
  try {
    return GoogleSigninLatest.signIn();
  } catch (err) {
    return Promise.reject(err);
  }
}

async function signUp() {
  if (!IS_Android) return Promise.reject(new Error("Platform is Not Yet Supported"));
  
  try {
    return GoogleSigninLatest.signUp();
  } catch (err) {
    return Promise.reject(err);
  }
}

async function signOut(): Promise<void>  {
  if (!IS_Android) return Promise.reject(new Error("Platform is Not Yet Supported"));
  
  try {
    return GoogleSigninLatest.signOut();
  } catch (err) {
    return Promise.reject(err);
  }
}

export const GoogleSignin = {
  configure,
  signIn,
  signUp,
  signOut,
};