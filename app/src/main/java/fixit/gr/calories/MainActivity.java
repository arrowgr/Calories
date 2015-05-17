package fixit.gr.calories;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "MainActivity";

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress;
    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    private SignInButton btnSignIn;
    private Button btnSignOut;
    private TextView txtArea;
    private ImageView imgProfilePic;
    // Fc stuff
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Button bLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Facebook initiliazer
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        InitializeFacebookStuff();
        InitiliazeGoogleStuff();

        bLogin = (Button) findViewById(R.id.bSignUp);
        bLogin.setOnClickListener(this);




    }

    private void InitiliazeGoogleStuff() {


        // GOOGLE STUFF
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        txtArea = (TextView) findViewById(R.id.tvArea);
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);

        // Button click listeners
        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


    }

    private void InitializeFacebookStuff() {
// Facebook callback manager
        callbackManager = CallbackManager.Factory.create();

        //defining login button
        loginButton = (LoginButton) findViewById(R.id.fb_login);
        loginButton.getLoginBehavior();
        //Request additional permission
        List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
        loginButton.setReadPermissions(permissionNeeds);
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Success Login ACCESS TOKEN");
                // accessToken = loginResult.getAccessToken();

                final GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {

                                    txtArea.append("FACEBOOK CONNECTION:\n\n" + "Name: " + String.valueOf(object.getString("name")) + "" +
                                            "\nAgeRange :" + String.valueOf(object.getString("age_range")) +
                                            "\nBirthday :" + String.valueOf(object.getString("birthday")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // Application code
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,age_range,about,birthday,address");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Cancel Login ACCESS TOKEN");
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "Error Login ACCESS TOKEN");
                // App code
            }
        });
        //login on click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Facebook Login button pressed", Toast.LENGTH_SHORT).show();

            }
        });

    }
    protected void onStart() {
        super.onStart();
        // alla 8elw na poros8esw akoma mia leitourgikothta ka8e fora pou 8a kanei start to activoty
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();


    }

    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        finish();

    }

    protected void onRestart() {
        super.onRestart();

        Toast.makeText(this, "MainActivity: onRestart()", Toast.LENGTH_LONG).show();


    }

    private void resolveSignInError() {
        if (mConnectionResult != null) {

            if (mConnectionResult.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
      //  Toast.makeText(getApplicationContext(), "Google Connection Fail :", Toast.LENGTH_SHORT).show();

        failure();

        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        callbackManager.onActivityResult(requestCode, responseCode, intent);

        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {

        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        if(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
            getProfileInformation();

            success();
        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    private void getProfileInformation() {
        try {
            // Sthn ousia an uparxei an8rwpos
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String name = person.getDisplayName();
                //String personPhotoUrl = person.getImage().getUrl();
                String nickname = person.getNickname();
                String birthday = person.getBirthday();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                txtArea.setText("GOOGLE CONNECTION:" +
                                "\nName: " + name
                                + "\nemail: " + email
                                + "\nBirthday :" + birthday
                                + "\nnickname :" + nickname
                );

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
            resolveSignInError();
            Toast.makeText(getApplicationContext(),
                    "Connect  data", Toast.LENGTH_LONG).show();
            success();

        }
    }

    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            //     Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
            //      mGoogleApiClient.connect();
            Toast.makeText(getApplicationContext(),
                    "Disconnect clear data", Toast.LENGTH_LONG).show();
            // na to doume
            failure();

        }
    }

    /**
     * Revoking access from google
     */
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mGoogleApiClient.connect();

                        }

                    });
        }
    }



    public void failure(){
        btnSignOut.setVisibility(View.GONE);
        btnSignIn.setVisibility(View.VISIBLE);
        txtArea.setText("");

    }
    public void success(){
        btnSignOut.setVisibility(View.VISIBLE);
        btnSignIn.setVisibility(View.GONE);

    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_sign_in) {
            signInWithGplus();

        } else if (v.getId() == R.id.btn_sign_out) {
            signOutFromGplus();

        }else if(v.getId()==R.id.bSignUp){
            Intent a = new Intent(this , SignUp.class);
            startActivity(a);
        }
    }

}
