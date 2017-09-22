package pe.usil.android.alertausil;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {
    private LoginFragment mainActivityFragment;
    PackageInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            info = getPackageManager().getPackageInfo("pe.usil.android.alertausil", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key: ", something);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Name not found", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        if (savedInstanceState == null) {
            mainActivityFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, mainActivityFragment).commit();
        } else {
            mainActivityFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        }
    }
}
