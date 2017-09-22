package pe.usil.android.alertausil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;

public class MainActivity extends AppCompatActivity {
    private MainActivityFragment mainActivityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (AccessToken.getCurrentAccessToken() == null) {
            goLoginActivity();
        } else {
            if (savedInstanceState == null) {
                mainActivityFragment = new MainActivityFragment();
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, mainActivityFragment).commit();
            } else {
                mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
            }
        }
    }

    private void goLoginActivity() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
