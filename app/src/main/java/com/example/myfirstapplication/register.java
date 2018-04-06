package com.example.myfirstapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class register extends AppCompatActivity {
    EditText e1,e2,e3,e4;
    TextView t1,t2,t3,t4;
    private static final String TAG ="signup" ;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String loginName,loginMail,loginPassword,loginPasswordReenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        e1 = (EditText) findViewById(R.id.editText3);
        e2= (EditText) findViewById(R.id.editText4);
        e3= (EditText) findViewById(R.id.editText5);
        e4=(EditText)findViewById(R.id.editText6);
        t1= (TextView) findViewById(R.id.textView5);
        t2=(TextView)findViewById(R.id.textView6);
        t3=(TextView)findViewById(R.id.textView7);
        t4=(TextView)findViewById(R.id.textView8);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }


    public void registerNewUser(View view) {
        loginName = e1.getText().toString();
        loginMail=e2.getText().toString();
        loginPassword= e3.getText().toString();
        loginPasswordReenter=e4.getText().toString();

        if(loginName.isEmpty())
        {
            e1.setError("Cannot be Empty!!");
        }
        else if(loginMail.isEmpty())
        {
            e2.setError("Cannot be Empty!!");
        }

        else if(!loginPassword.equals(loginPasswordReenter))
        {
            e4.setError("Password did not Match!!");
        }

        else if(loginPassword.length()< 8)
        {
            e3.setError("Min 8 characters");
        }

        else {


            mAuth.createUserWithEmailAndPassword(loginMail, loginPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            Toast.makeText(register.this, "SignUp Successful!!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(register.this,login.class);
                            startActivity(intent);

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(register.this, "Signup Failed!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }



    }
}
