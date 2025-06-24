import { NativeModules, Platform } from "react-native";
import type { configureParams, GoogleSignInButtonError, GoogleUser, SignInError, SignInParams } from "../types";

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

const IS_ANDROID = Platform.OS === 'android';

function configure({
    webClientId,
    filterByAuthorizedAccounts = false,
    autoSelectEnabled = false
  }: configureParams) {
  if (!IS_ANDROID) {
    const error: SignInError = {
      code: 'PLATFORM_NOT_SUPPORTED',
      message: 'Google Sign-In is only supported on Android',
    };
    return Promise.reject(error);
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
}: SignInParams = {}): Promise<GoogleUser> {
  if (!IS_ANDROID) {
    return Promise.reject({
      code: 'PLATFORM_NOT_SUPPORTED',
      message: 'Google Sign-In is only supported on Android',
    } as SignInError);
  }

  try {
    const user = await GoogleSigninLatest.signIn({
      fallbackToSignInWithGoogleButton,
    });
    
    // Validate the response structure
    if (!user?.idToken) {
      throw {
        code: 'TOKEN_ERROR',
        message: 'Invalid user data received from native module',
        nativeError: user,
      };
    }
    
    return user;
  } catch (error) {
    return Promise.reject(normalizeSignInError(error));
  }
}

async function signInWithGoogleButton(): Promise<GoogleUser> {
  if (!IS_ANDROID) {
    return Promise.reject({
      code: 'PLATFORM_NOT_SUPPORTED',
      message: 'Google Sign-In is only supported on Android',
    } as GoogleSignInButtonError);
  }

  try {
    const user = await GoogleSigninLatest.signInWithGoogleButton();
    
    // Validate the response structure
    if (!user?.idToken) {
      throw {
        code: 'TOKEN_ERROR',
        message: 'Invalid user data received from native module',
        nativeError: user,
      };
    }
    
    return user;
  } catch (error) {
    return Promise.reject(normalizeSignInError(error));
  }
}

// Helper function for type-safe error normalization
function normalizeSignInError(error: unknown): SignInError {
  if (typeof error === 'object' && error !== null) {
    const err = error as { code?: string; message?: string };
    
    // Type guard for valid error codes
    const validCodes = [
      'USER_CANCELLED',
      'NO_CREDENTIALS',
      'TOKEN_ERROR',
      'CREDENTIAL_ERROR',
      'PLATFORM_NOT_SUPPORTED',
      'ACTIVITY_NOT_FOUND'
    ] as const;

    const code = validCodes.includes(err.code as any) 
      ? err.code as SignInError['code']
      : 'UNKNOWN_ERROR';

    return {
      code,
      message: err.message || 'Unknown sign-in error',
      nativeError: error
    };
  }

  if (error instanceof Error) {
    return {
      code: 'UNKNOWN_ERROR',
      message: error.message,
      nativeError: error
    };
  }

  return {
    code: 'UNKNOWN_ERROR',
    message: String(error),
    nativeError: error
  };
}

async function signOut(): Promise<void>  {
  if (!IS_ANDROID) {
    const error: SignInError = {
      code: 'PLATFORM_NOT_SUPPORTED',
      message: 'Google Sign-In is only supported on Android',
    };
    return Promise.reject(error);
  }
  
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