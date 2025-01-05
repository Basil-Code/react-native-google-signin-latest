import { NativeModules, Platform } from "react-native";
import type { configureParams, signInParams } from "../types";

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

function configure({
    webClientId,
    filterByAuthorizedAccounts = false,
    autoSelectEnabled = false
  }: configureParams) {
  if (!IS_Android) {
    return Promise.reject(new Error("Platform is Not Yet Supported"));
  }
  
  if (!webClientId) {
    throw new Error('webClientId is null');
  }

  // console.log('options ', options)
  // console.log('configure DONE!')
  return GoogleSigninLatest.configure(
    {
      webClientId,
      filterByAuthorizedAccounts,
      autoSelectEnabled
    }
  );
}

async function signIn({
    fallbackToSignInWithGoogleButton = false
  }: signInParams = {}) {
  if (!IS_Android) return Promise.reject(new Error("Platform is Not Yet Supported"));
  
  try {
    return GoogleSigninLatest.signIn({
      fallbackToSignInWithGoogleButton
    });
  } catch (err) {
    return Promise.reject(err);
  }
}

async function signInWithGoogleButton() {
  if (!IS_Android) return Promise.reject(new Error("Platform is Not Yet Supported"));
  // const params = options ? options : {}
  try {
    return GoogleSigninLatest.signInWithGoogleButton();
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
  signInWithGoogleButton,
  signOut,
};