/*
 * Copyright (c) 2012 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package plus;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.MemoryCredentialStore;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;

/**
 * Implements OAuth authentication.
 *
 * @author Yaniv Inbar
 * @author Will Norris
 * @author Jenny Murphy
 * @author Lee Denison
 */
public class Auth {

  private static final String GOOGLE_OAUTH2_AUTH_URI = "https://accounts.google.com/o/oauth2/auth";
  private static final String GOOGLE_OAUTH2_TOKEN_URI = "https://accounts.google.com/o/oauth2/token";
  
  private static AuthorizationCodeFlow authorizationCodeFlow = null;

  /**
   * The credential store key for the invoking user.
   * 
   * We only have one user (the user invoking the program) so we only have one set of credentials
   * to store.
   */
  public static final String DEFAULT_USER = "default";
  
  /**
   * Send the user through the OAuth flow to authorize the application and update
   * the refresh and auth tokens.
   *
   * <p>Access to the AuthorizationCodeFlow credential store should be synchronized
   * in multi-threaded applications.</p>ß
   *
   * @throws IOException unable to complete OAuth flow
   */
  public static void authorize() throws IOException {
    if ("".equals(ConfigHelper.CLIENT_ID)) {
      System.err.println("Please specify your OAuth Client ID in src/main/resources/config.properties.");
      System.exit(1);
    }

    // build the authorization URL
    AuthorizationCodeRequestUrl authorizationUrl = getFlow().newAuthorizationUrl();
    authorizationUrl.setRedirectUri(ConfigHelper.REDIRECT_URI);

    String authorizationUrlString = authorizationUrl.build();

    // launch in browser
    System.out.println("Attempting to open a web browser to start the OAuth2 flow");
    Util.openBrowser(authorizationUrlString);

    // request code from user
    System.out.print("Once you authorize please enter the code here: ");
    String code = new Scanner(System.in).nextLine();

    // Exchange code for an access token
    TokenResponse response = getFlow().newTokenRequest(code)
        .setRedirectUri(ConfigHelper.REDIRECT_URI).execute();
    
    getFlow().createAndStoreCredential(response, DEFAULT_USER);
  }

  public static AuthorizationCodeFlow getFlow() {
    if (null == authorizationCodeFlow) {
        authorizationCodeFlow = new AuthorizationCodeFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                Util.TRANSPORT,
                Util.JSON_FACTORY,
                new GenericUrl(GOOGLE_OAUTH2_TOKEN_URI),
                new ClientParametersAuthentication(ConfigHelper.CLIENT_ID, ConfigHelper.CLIENT_SECRET),
                ConfigHelper.CLIENT_ID,
                GOOGLE_OAUTH2_AUTH_URI)
            .setCredentialStore(new MemoryCredentialStore())
            .setScopes(Arrays.asList(ConfigHelper.SCOPES))
            .build();
    }
        
    return authorizationCodeFlow;
  }
}
