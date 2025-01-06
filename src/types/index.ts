export type configureParams = {
  webClientId: string
  /** Default value is **false** */
  filterByAuthorizedAccounts?: boolean
  /** Default value is **false** */
  autoSelectEnabled?: boolean
}

export type signInWithGoogleButtonParams = {}

export type signInParams = {
  /** Default value is **false** */
  fallbackToSignInWithGoogleButton?: boolean
}