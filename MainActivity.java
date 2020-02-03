package com.example.lab1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab1.repository.DBManagerUser;
import com.example.lab1.view.DrugListActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private DBManagerUser dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 100);
            }
        });

        dbManager = new DBManagerUser(this);
        dbManager.open();
        dbManager.create_local_user("admin","admin",0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 100) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            dbManager.create_local_user(account.getDisplayName(),account.getFamilyName(),1);


            final TextView username = findViewById(R.id.editUsername);
            username.setText(account.getDisplayName());
            final TextView password = findViewById(R.id.editTextPassword);
            password.setText(account.getFamilyName());

            Toast.makeText(this,"An account was created with your family name as password",Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Sign in failed:"+e.getMessage())
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }

    public void login(View view)
    {
        // check user data exists in db
        final TextView username = findViewById(R.id.editUsername);
        if(username.getText().toString().isEmpty())
        {
            username.setError("Username required");
            return;
        }

        final TextView password = findViewById(R.id.editTextPassword);
        if(password.getText().toString().isEmpty())
        {
            password.setError("Password required");
            return;
        }

        Cursor cursor = dbManager.findUser(username.getText().toString());
        int local = Integer.parseInt(cursor.getString(cursor.getColumnIndex("local")));
        String pass = cursor.getString(cursor.getColumnIndex("password"));
        if(cursor.isNull(0))
        {
            Toast.makeText(MainActivity.this, "Inexistent account!", Toast.LENGTH_LONG).show();
        }
        else {
            if (pass.equals(password.getText().toString())) {
                Intent intent = new Intent(MainActivity.this, DrugListActivity.class);
                intent.putExtra("local",local);
                startActivity(intent);
            } else if (!pass.equals(password.getText().toString())) {
                password.setError("Incorrect password");
            } else {
                username.setError("Incorrect user");
            }
        }
    }
}
