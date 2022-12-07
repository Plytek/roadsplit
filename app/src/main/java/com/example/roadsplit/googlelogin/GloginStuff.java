package com.example.roadsplit.googlelogin;

import android.content.Intent;
import android.view.View;

public class GloginStuff {
 /*

           //Database db = new Database();
        //UserService userService = new UserService(db.getConnection());

        //userService.fetchHimK("karl@1245");

        //userService.fetchUserAccount(query);
        //UserAccount user = userService.getCurrentUser();

        //Log.d("useracc", user.toString());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

     final String query = "SELECT * FROM user_account";
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) Log.d("googleacc", "Schon eingeloggt");
        else Log.d("googleacc", "Noch nicht eingeloggt");
    }

    public void login(View v)
    {
        System.out.println("test");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signIn()
    {
        System.out.println("test");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

      @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.d("googleacc", "Erfolgreich eingeloggt");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("googleacc", "signInResult:failed code=" + e.getStatusCode());
        }
    }
    public void fetchUserAccount(String query)
    {
        currentUser = new UserAccount();
        try  {
            try(Connection conn = connection;
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);) {
                // Extract data from result set
                while (rs.next()) {
                    // Retrieve by column name
                    currentUser.setId(rs.getInt("id"));
                    currentUser.setNickname(rs.getString("nickname"));
                    currentUser.setUniquename(rs.getString("uniquename"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    */
}
