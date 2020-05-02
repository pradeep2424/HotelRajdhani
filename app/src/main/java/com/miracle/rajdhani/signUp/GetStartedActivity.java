package com.miracle.rajdhani.signUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.miracle.rajdhani.R;
import com.miracle.rajdhani.activities.LocationGoogleMapActivity;
import com.miracle.rajdhani.dialog.DialogLoadingIndicator;
import com.miracle.rajdhani.model.UserDetails;
import com.miracle.rajdhani.utils.Application;
import com.miracle.rajdhani.utils.ConstantValues;
import com.miracle.rajdhani.utils.InternetConnection;
import com.miracle.rajdhani.utils.Utils;

import org.json.JSONObject;

import java.util.Arrays;

public class GetStartedActivity extends AppCompatActivity {
    DialogLoadingIndicator progressIndicator;
    RelativeLayout rlRootLayout;
    LinearLayout llConnectWithMobileNo;
    LinearLayout llSkip;
    LinearLayout llGoogle;
    LinearLayout llFacebook;
    TextView tvTermsNPolicy;

    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
//    FirebaseAuth.AuthStateListener mAuthListener;
    CallbackManager mCallbackManager;

    final int RC_SIGN_IN = 100;

//    final int RC_SIGN_IN_GOOGLE = 100;
//    final int RC_SIGN_IN_FACEBOOK = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        mAuth = FirebaseAuth.getInstance();

        init();
        componentEvents();
        initGoogleSignIn();
        initFacebookSignIn();
    }

    private void init() {
        progressIndicator = DialogLoadingIndicator.getInstance();
        rlRootLayout = findViewById(R.id.rl_rootLayout);
        llConnectWithMobileNo = findViewById(R.id.ll_connectWithMobileNo);
        llSkip = findViewById(R.id.ll_skip);
        llGoogle = findViewById(R.id.ll_google);
        llFacebook = findViewById(R.id.ll_facebook);

        tvTermsNPolicy = findViewById(R.id.tv_privacyAndTerms);

        if (tvTermsNPolicy != null) {
            tvTermsNPolicy.setMovementMethod(LinkMovementMethod.getInstance());
            Utils.removeUnderlines((Spannable) tvTermsNPolicy.getText());
        }

//        tvTitle = (TextView) findViewById(R.id.tv_title);
//        tvLogin = (TextView) findViewById(R.id.tv_login);
//        tvSignUp = (TextView) findViewById(R.id.tv_signUp);

//        Typeface custom_fonts = Typeface.createFromAsset(getAssets(), "fonts/ArgonPERSONAL-Regular.otf");
//        tvTitle.setTypeface(custom_fonts);
    }

    private void componentEvents() {
        llConnectWithMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMobileNumberActivity();
            }
        });

        llSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetStartedActivity.this, LocationGoogleMapActivity.class);
                intent.putExtra("CalledFrom", ConstantValues.ACTIVITY_ACTION_SKIP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadMobileNumberActivity() {
        Intent it = new Intent(GetStartedActivity.this, GetStartedMobileNumberActivity.class);
        startActivity(it);
        finish();
    }

    private void initGoogleSignIn() {
        //Then we need a GoogleSignInOptions object
        //And we need to build it as below

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        llGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InternetConnection.checkConnection(getApplicationContext())) {
                    llGoogle.setClickable(false);

                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);

                } else {
                    showNoInternetConnection();
                }
            }
        });
    }

    private void initFacebookSignIn() {
        mCallbackManager = CallbackManager.Factory.create();

        llFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetConnection.checkConnection(getApplicationContext())) {
                    llFacebook.setClickable(false);

                    LoginManager.getInstance().logInWithReadPermissions(
                            GetStartedActivity.this, Arrays.asList("email", "public_profile"));
                } else {
                    showNoInternetConnection();
                }
            }
        });

//        btnFBSignIn.setReadPermissions("email");
//        btnFBSignIn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                showDialog();
                handleFacebookSignIn(loginResult);
            }

            @Override
            public void onCancel() {
                llFacebook.setClickable(true);
            }

            @Override
            public void onError(FacebookException error) {
                llFacebook.setClickable(true);
            }
        });

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//
//                    name = user.getDisplayName();
////                    startActivity(new Intent(SignUpActivity.this, ProfileActivity.class));
//
//                    Log.i(TAG, "###Toast" + user.getDisplayName());
////                    Toast.makeText(SignUpActivity.this, "" + user.getDisplayName(), Toast.LENGTH_LONG).show();
//                } else {
//                    Log.i(TAG, "###Toast" + "something went wrong");
////                    Toast.makeText(SignUpActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
//                }
//            }
//        };
    }

    private void disableSocialMediaButtons() {
        llGoogle.setClickable(false);
        llFacebook.setClickable(false);
    }

    private void enableSocialMediaButtons() {
        llGoogle.setClickable(true);
        llFacebook.setClickable(true);
    }

    private void showNoInternetConnection() {
        Snackbar.make(rlRootLayout, getResources().getString(R.string.no_internet),
                Snackbar.LENGTH_LONG).show();

//        Snackbar.make(rootLayout, getResources().getString(R.string.no_internet),
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction("RETRY", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        isShowLinkingMsg();
//                    }
//                })
//                .setActionTextColor(getResources().getColor(R.color.colorSnackbarButtonText))
//                .show();
    }

    private void handleGoogleSignIn(GoogleSignInAccount acct) {
        showDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                String displayName = user.getDisplayName();
                                String email = user.getEmail();
                                String photoURL = user.getPhotoUrl().toString();
//                                String phone = user.getPhoneNumber();

                                String fullname = displayName;
                                String[] parts = fullname.split("\\s+");
                                String firstname = parts[0]; // FirstName
                                String lastname = parts[1]; //  LastName

                                UserDetails userProfile = new UserDetails();
                                userProfile.setFirstName(firstname);
                                userProfile.setLastName(lastname);
                                userProfile.setFullName(user.getDisplayName());
                                userProfile.setEmail(email);
                                userProfile.setUserPhoto(photoURL);
                                Application.userDetails = userProfile;

                                dismissDialog();
                                loadMobileNumberActivity();

//                                signUp(socialMediaEmailId, ConstantValues.mediaTypeGoogle, firstname, lastname);

                            } else {
                                dismissDialog();
                                signOutFirebaseAccounts();
                                llGoogle.setClickable(true);
                            }

                        } else {
                            dismissDialog();
                            signOutFirebaseAccounts();
                            llGoogle.setClickable(true);
                            Log.w("ERROR : ", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }


//    private void handleFacebookSignIn(LoginResult loginResult) {
//        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = mAuth.getCurrentUser();
//
//                            if (user != null) {
//                                String displayName = user.getDisplayName();
//                                String email = user.getEmail();
//                                String photoURL = user.getPhotoUrl().toString();
////                                String phone = user.getPhoneNumber();
//
//                                String fullname = displayName;
//                                String[] parts = fullname.split("\\s+");
//                                String firstname = parts[0]; // FirstName
//                                String lastname = parts[1]; //  LastName
//
//                                UserDetails userProfile = new UserDetails();
//                                userProfile.setName(user.getDisplayName());
//                                userProfile.setEmail(email);
//                                userProfile.setUserPhoto(photoURL);
//                                Application.userDetails = userProfile;
//
////                                signUp(socialMediaEmailId, ConstantValues.mediaTypeGoogle, firstname, lastname);
//
//                            } else {
//                                signOutFirebaseAccounts();
//                                llFacebook.setClickable(true);
//                            }
//
//                        } else {
//                            signOutFirebaseAccounts();
//                            llFacebook.setClickable(true);
//                            Toast.makeText(GetStartedActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void handleFacebookSignIn(LoginResult loginResult) {
        showDialog();

        GraphRequest data_request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                        try {
//                            String id = jsonObject.getString("id");

                            String url = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                            String email = jsonObject.getString("email");
                            String displayName = jsonObject.getString("name");

                            String[] parts = displayName.split("\\s+");
                            String firstname = parts[0]; // FirstName
                            String lastname = parts[1]; //  LastName

                            UserDetails userProfile = new UserDetails();
                            userProfile.setFullName(displayName);
                            userProfile.setFirstName(firstname);
                            userProfile.setLastName(lastname);
                            userProfile.setEmail(email);
                            userProfile.setUserPhoto(url);
                            Application.userDetails = userProfile;

                            dismissDialog();
                            loadMobileNumberActivity();
//
//                            signUp(socialMediaEmailId, ConstantValues.mediaTypeFB, firstname, lastname);

                        } catch (Exception e) {
                            e.printStackTrace();
                            dismissDialog();
                            signOutFirebaseAccounts();
                            llFacebook.setClickable(true);
                        }
                    }
                });

        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture");
//        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    private void signOutFirebaseAccounts() {
        FirebaseAuth.getInstance().signOut();
//        LoginManager.getInstance().logOut();
        mGoogleSignInClient.signOut();
    }

    public void showDialog() {
        progressIndicator.showProgress(GetStartedActivity.this);
    }

    public void dismissDialog() {
        if (progressIndicator != null) {
            progressIndicator.hideProgress();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CANCELED) {
            llGoogle.setClickable(true);
            llFacebook.setClickable(true);

            } else if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                handleGoogleSignIn(account);
            } catch (ApiException e) {
                llGoogle.setClickable(true);
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

//        else if (selectedModeForSignUp.equalsIgnoreCase(ConstantValues.mediaTypeFB)) {
//            mCallbackManager.onActivityResult(requestCode, responseCode, intent);
//
//        }
        else {
            llGoogle.setClickable(true);
            llFacebook.setClickable(true);
        }
    }


}
