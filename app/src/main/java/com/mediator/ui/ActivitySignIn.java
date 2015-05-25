package com.mediator.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mediator.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by luispablo on 25/05/15.
 */
public class ActivitySignIn extends Activity {

    @InjectView(R.id.textEmail)
    TextView textEmail;
    @InjectView(R.id.editEmail)
    EditText editEmail;
    @InjectView(R.id.textPassword)
    TextView textPassword;
    @InjectView(R.id.editPassword)
    EditText editPassword;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.inject(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.title_wait_please);
    }

    @OnClick(R.id.buttonSignIn)
    public void onClickSignIn() {
        progressDialog.setMessage(getString(R.string.signing_in));
        progressDialog.show();

        ParseUser.logInInBackground(editEmail.getText().toString(), editPassword.getText().toString(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                progressDialog.dismiss();

                if (user != null) {
                    Intent intent = new Intent(ActivitySignIn.this, ActivityMain.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ActivitySignIn.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @OnClick(R.id.buttonSignUp)
    public void onClickSignUp() {
        progressDialog.setMessage(getString(R.string.signing_up));
        progressDialog.show();

        ParseUser user = new ParseUser();
        user.setUsername(editEmail.getText().toString());
        user.setPassword(editPassword.getText().toString());
        user.setEmail(editEmail.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                progressDialog.dismiss();

                if (e == null) {
                    Intent intent = new Intent(ActivitySignIn.this, ActivityMain.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ActivitySignIn.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
