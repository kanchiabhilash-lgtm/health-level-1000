package com.kanchi.healthlevel1000;

import android.app.Activity;
import android.content.Intent;
import androidx.activity.result.ActivityResult;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

@CapacitorPlugin(name = "NativeGoogleAuth")
public class NativeGoogleAuthPlugin extends Plugin {
    private GoogleSignInClient googleSignInClient;

    @PluginMethod
    public void signIn(PluginCall call) {
        String serverClientId = call.getString("serverClientId", "");

        GoogleSignInOptions.Builder gsoBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile();

        if (serverClientId != null && !serverClientId.trim().isEmpty()) {
            gsoBuilder.requestIdToken(serverClientId.trim());
        }

        GoogleSignInOptions gso = gsoBuilder.build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        // Sign out before launching so user can always choose/switch their account
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(call, signInIntent, "handleSignInResult");
        });
    }

    @ActivityCallback
    private void handleSignInResult(PluginCall call, ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_CANCELED) {
            call.reject("Sign-in cancelled by user");
            return;
        }

        Intent data = result.getData();
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                JSObject ret = new JSObject();
                ret.put("email", account.getEmail() != null ? account.getEmail() : "");
                ret.put("displayName", account.getDisplayName() != null ? account.getDisplayName() : "");
                ret.put("idToken", account.getIdToken() != null ? account.getIdToken() : "");
                ret.put("id", account.getId() != null ? account.getId() : "");
                if (account.getPhotoUrl() != null) {
                    ret.put("photoUrl", account.getPhotoUrl().toString());
                } else {
                    ret.put("photoUrl", "");
                }
                call.resolve(ret);
            } else {
                call.reject("Google sign-in account is null");
            }
        } catch (ApiException e) {
            call.reject("Google Sign-In failed with status code: " + e.getStatusCode() + " (" + e.getMessage() + ")");
        }
    }

    @PluginMethod
    public void signOut(PluginCall call) {
        if (googleSignInClient != null) {
            googleSignInClient.signOut().addOnCompleteListener(task -> call.resolve());
        } else {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
            GoogleSignIn.getClient(getActivity(), gso).signOut().addOnCompleteListener(task -> call.resolve());
        }
    }
}
