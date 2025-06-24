export type configureParams = {
  webClientId: string
  /** Default value is **false** */
  filterByAuthorizedAccounts?: boolean
  /** Default value is **false** */
  autoSelectEnabled?: boolean
}

export type signInWithGoogleButtonParams = {}

export type GoogleUser = {
  idToken: string;
  email: string;
  displayName?: string;
  givenName?: string;
  familyName?: string;
  photoUrl?: string;
};

export type SignInError = {
  code:
    | 'USER_CANCELLED'
    | 'NO_CREDENTIALS'
    | 'TOKEN_ERROR'
    | 'CREDENTIAL_ERROR'
    | 'UNKNOWN_ERROR'
    | 'PLATFORM_NOT_SUPPORTED'
    | 'ACTIVITY_NOT_FOUND';
  message: string;
  nativeError?: any;
};

export type GoogleSignInButtonError = {
  code:
    | 'USER_CANCELLED'
    | 'NO_CREDENTIALS'
    | 'TOKEN_ERROR'
    | 'CREDENTIAL_ERROR'
    | 'UNKNOWN_ERROR'
    | 'PLATFORM_NOT_SUPPORTED'
    | 'ACTIVITY_NOT_FOUND';
  message: string;
  nativeError?: any;
};

export type SignInParams = {
  /** Default value is **false** */
  fallbackToSignInWithGoogleButton?: boolean;
};