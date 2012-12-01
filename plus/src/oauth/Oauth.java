package oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;

/*
 * For reference -- https://developers.google.com/accounts/docs/OAuth2WebServer#tokenrevoke
 */

/*
 * http://stackoverflow.com/questions/10294124/getting-null-refresh-token (Get Refresh Token).
 * -- set parameter String genericUrl = authoriseUrl.setAccessType("offline").build();
 * 
 * When building the request Url, you should set the Access Type :

 requestUrl = new GoogleAuthorizationCodeRequestUrl(googleClientId, callBackUrl, scopes).setAccessType("offline").build();
 As described in this page  setting this parameter is recommended :

 [...] We recommend that you explicitly set the access_type parameter to offline 
 because we anticipate that when the online value is introduced, it will be as the default behavior. 
 This could cause unexpected changes in your application since it would affect the way that your application is allowed to refresh access tokens. 
 By explicitly setting the parameter value to offline, you can avoid any changes in your application's functionality. [...]
 *-------------------------------------------------------------------------------------------------------------------------------------------------
 * Re-Prompt the refresh token: 
 * http://stackoverflow.com/questions/10736781/google-oauth2-re-acquiring-a-refresh-token-for-an-authorized-user-on-a-web-serv
 * Another case of RTFM.

 from https://developers.google.com/accounts/docs/OAuth2WebServer :

 Important: When your application receives a refresh token, it is important to store that refresh token for future use. 
 If your application loses the refresh token, it will have to re-prompt the user for consent before obtaining another refresh token. 
 If you need to re-prompt the user for consent, include the approval_prompt parameter in the authorization code request, and set the value to force.
 * 
 */

public class Oauth {
	private static final String CLIENT_ID = "965390586728.apps.googleusercontent.com";
	private static final String REDIRECT_URI = "https://www.amar.com/oauth2callback";
	private static final List<String> SCOPES = Arrays
			.asList(new String[] { "https://www.google.com/m8/feeds" });
	// .asList(new String[] { "https://www.googleapis.com/auth/urlshortener" });
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final String CLIENT_SECRETS = "KgGMc8V5dnuOBg_uBLH8DhkF";

	public static void main(String[] args) {
		BufferedReader bufferedReader = null;
		try {
			GoogleAuthorizationCodeRequestUrl authoriseUrl = new GoogleAuthorizationCodeRequestUrl(
					CLIENT_ID, REDIRECT_URI, SCOPES);
			String genericUrl = authoriseUrl.setApprovalPrompt("force")
					.setAccessType("offline").build();
			System.out.println("Generic URL : " + genericUrl);

			System.out.println("Type the code you receive here: ");
			bufferedReader = new BufferedReader(
					new InputStreamReader(System.in));
			String authorizationCode = bufferedReader.readLine();
			GoogleAuthorizationCodeTokenRequest tokenRequest = new GoogleAuthorizationCodeTokenRequest(
					HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRETS,
					authorizationCode, REDIRECT_URI);
			GoogleTokenResponse googleTokenResponse = tokenRequest.execute();
			String accessToken = googleTokenResponse.getAccessToken();
			System.out.println("Access Token: " + accessToken);
			System.out.println("Refresh Token: "
					+ googleTokenResponse.getRefreshToken());
			System.out.println("Token Type: "
					+ googleTokenResponse.getTokenType());
			System.out.println("Expires In: "
					+ googleTokenResponse.getExpiresInSeconds());
			// GoogleRefreshTokenRequest refreshTokenRequest = new
			// GoogleRefreshTokenRequest(
			// HTTP_TRANSPORT, JSON_FACTORY,
			// googleTokenResponse.getRefreshToken(), CLIENT_ID,
			// CLIENT_SECRETS);
			// String refreshToken = refreshTokenRequest.getRefreshToken();
			// System.out.println("Refresh Token1: " + refreshToken);
			ContactsService service = new ContactsService("Contact");
			URL feedUrl = new URL(
					"https://www.google.com/m8/feeds/contacts/default/full?oauth_token="
							+ accessToken);
			ContactFeed resultFeed = service
					.getFeed(feedUrl, ContactFeed.class);
			System.out.println(resultFeed.getTitle().getPlainText());
		} catch (IOException e) {
			throw new RuntimeException(
					"Exception while reading a line from standard input: ", e);
		} catch (ServiceException e) {
			throw new RuntimeException(
					"Exception while accessing the service: ", e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (Exception ignore) {
					// ignore
				}
			}
		}
	}
}
