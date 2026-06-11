package utils;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;

import java.io.InputStreamReader;
import java.util.Collections;
public class GoogleSignIn {

        private static final String CLIENT_SECRET_JSON = "/client_secret_260850152517-u6nps4qevk3hned0a4hr52tejqlliudv.apps.googleusercontent.com.json"; // put your file in src/main/resources
        private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

        public static Credential authorize() throws Exception {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(GoogleSignIn.class.getResourceAsStream(CLIENT_SECRET_JSON)));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, Collections.singletonList("https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email"))
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8899).build();
            String redirectUri = receiver.getRedirectUri();
            String authorizeUrl = flow.newAuthorizationUrl()
                    .setRedirectUri(redirectUri)
                    .set("prompt", "consent") // <-- This forces Google to show the sign-in screen every time
                    .build();

            //they say Open browser manually

            java.awt.Desktop.getDesktop().browse(java.net.URI.create(authorizeUrl));
            String code = receiver.waitForCode();
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();
            // they say Exchange code for credentials
            return flow.createAndStoreCredential(tokenResponse, "user");
            //return flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            //return new com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        }

        public static GoogleUserInfo getUserInfo(Credential credential) throws Exception {
            Oauth2 oauth2 = new Oauth2.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    credential)
                    .setApplicationName("travelpro")
                    .build();

            Userinfo userInfo = oauth2.userinfo().get().execute();

            return new GoogleUserInfo(
                    userInfo.getEmail(),
                    userInfo.getGivenName(),
                    userInfo.getFamilyName(),
                    userInfo.getPicture()
            );
        }

        public static class GoogleUserInfo {
            private final String email;
            private final String givenName;
            private final String familyName;
            private final String pictureUrl;

            public GoogleUserInfo(String email, String givenName, String familyName, String pictureUrl) {
                this.email = email;
                this.givenName = givenName;
                this.familyName = familyName;
                this.pictureUrl = pictureUrl;
            }

            public String getEmail() { return email; }
            public String getGivenName() { return givenName; }
            public String getFamilyName() { return familyName; }
            public String getPictureUrl() { return pictureUrl; }
        }
    }

