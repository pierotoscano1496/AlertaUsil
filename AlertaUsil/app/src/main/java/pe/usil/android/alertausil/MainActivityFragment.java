package pe.usil.android.alertausil;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainActivityFragment extends Fragment {
    private static final int RECOGNIZE_SPEECH_ACTIVITY = 10;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 11;
    //Declaración de componentes
    private TabHost tabHost;
    private TextView txtUserName;
    private Button btnLogout;
    private EditText etNumTel1;
    private ImageButton btnGetMessage;
    private EditText etNumTel2;
    private EditText etTextMessage;
    private ImageButton btnSendSosSms;
    private ImageView imgFoto;
    private EditText etNumTel3;
    private ImageButton btnTomarFoto;
    private WebView webBusqueda;

    LocationManager locationManager;
    Double longitud;
    Double latitud;


    public MainActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Profile perfil = Profile.getCurrentProfile();
        String userName = perfil.getFirstName();

        View view = inflater.inflate(R.layout.fragment_main_activity, container, false);

        //Iniciar el tabhost
        Resources resources = getResources();
        tabHost = (TabHost) view.findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Tab1");
        spec.setContent(R.id.Tab1);
        spec.setIndicator("VOZ");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Tab2");
        spec.setContent(R.id.Tab2);
        spec.setIndicator("SMS");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Tab3");
        spec.setContent(R.id.Tab3);
        spec.setIndicator("FOTO");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Tab4");
        spec.setContent(R.id.Tab4);
        spec.setIndicator("NOTICIA");
        tabHost.addTab(spec);

        //Inicialización de componentes
        txtUserName = (TextView) view.findViewById(R.id.txtUserName);
        etNumTel1 = (EditText) view.findViewById(R.id.etNumTel1);
        btnGetMessage = (ImageButton) view.findViewById(R.id.btnGetMessage);
        etNumTel2 = (EditText) view.findViewById(R.id.etNumTel2);
        etTextMessage = (EditText) view.findViewById(R.id.etTextMessage);
        btnSendSosSms = (ImageButton) view.findViewById(R.id.btnSendSosSms);
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        imgFoto = (ImageView) view.findViewById(R.id.imgFoto);
        etNumTel3 = (EditText) view.findViewById(R.id.etNumTel3);
        btnTomarFoto = (ImageButton) view.findViewById(R.id.btnTomarFoto);
        webBusqueda = (WebView) view.findViewById(R.id.webBusqueda);

        //Colocación del nombre del usuario
        txtUserName.setText("Hola, " + userName);

        //Inicio del webview
        webBusqueda.setWebViewClient(new MyWebViewClient());
        WebSettings settings = webBusqueda.getSettings();
        settings.setJavaScriptEnabled(true);
        webBusqueda.loadUrl("https://www.google.com/search?q=robos+en+lima");

        //Inicio de localización geográfica
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //Eventos para los botones
        btnGetMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String numTelefono = etNumTel1.getText().toString();
                    if (numTelefono != "") {
                        Intent intentActionRecognizeSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intentActionRecognizeSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-MX");
                        startActivityForResult(intentActionRecognizeSpeech, RECOGNIZE_SPEECH_ACTIVITY);
                    } else {
                        Toast.makeText(getActivity().getBaseContext(), "Escriba un número de teléfono", Toast.LENGTH_LONG).show();
                    }
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getActivity().getBaseContext(), "Su dispositivo no soporta el reconocimiento de voz", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSendSosSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMessage = etTextMessage.getText().toString();
                String numTelefono = etNumTel2.getText().toString();
                if (numTelefono != "" && textoMessage != "") {
                    try {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getActivity().getBaseContext(), "No tiene permisos para enviar mensajes", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, 225);
                        } else {
                            SmsManager smsManager = SmsManager.getDefault();
                            String textoSms = textoMessage;
                            if (setUpdateLocation()) {
                                textoSms = textoSms + "\nLatitud: " + latitud + "\nLongitud: " + longitud;
                                smsManager.sendTextMessage(numTelefono, null, textoSms, null, null);
                                Toast.makeText(getActivity().getBaseContext(), "Mensaje enviado", Toast.LENGTH_LONG).show();
                            } else {
                                smsManager.sendTextMessage(numTelefono, null, textoSms, null, null);
                                Toast.makeText(getActivity().getBaseContext(), "Mensaje enviado, pero sin localización", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity().getBaseContext(), "Error al enviar mensaje", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity().getBaseContext(), "Complete los campos", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                goLoginActivity();
            }
        });

        return view;
    }

    private void goLoginActivity() {
        Intent intent = new Intent(getActivity(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //Actualiza la ubicación del usuario
    public boolean setUpdateLocation() {
        boolean updatedLocation = false;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            Toast.makeText(getActivity().getBaseContext(), "No tiene permisos para localizar su ubicación", Toast.LENGTH_LONG).show();
        } else {
            String providerInfo = null;
            if (LocationManager.NETWORK_PROVIDER != null) {
                providerInfo = LocationManager.NETWORK_PROVIDER;
            } else if (LocationManager.GPS_PROVIDER != null) {
                providerInfo = LocationManager.GPS_PROVIDER;
            }
            Location location = locationManager.getLastKnownLocation(providerInfo);
            if (location != null) {
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                updatedLocation = true;
            } else {
                Toast.makeText(getActivity().getBaseContext(), "Error al localizar.", Toast.LENGTH_LONG).show();
            }
        }
        return updatedLocation;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case RECOGNIZE_SPEECH_ACTIVITY://Si la actividad fué el reconocimiento de voz
                    if (data != null) {
                        ArrayList<String> speech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String speechedWord = speech.get(0);
                        if (speechedWord.equalsIgnoreCase("sos") || speechedWord.equalsIgnoreCase("Auxilio") || speechedWord.equalsIgnoreCase("Ayuda")) {
                            try {
                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                    Toast.makeText(getActivity().getBaseContext(), "No tiene permisos para enviar mensajes", Toast.LENGTH_LONG).show();
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, 225);
                                } else {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    String textoSms = speechedWord;
                                    if (setUpdateLocation()) {
                                        textoSms = textoSms + "\nLatitud: " + latitud + "\nLongitud: " + longitud;
                                        smsManager.sendTextMessage(etNumTel1.getText().toString(), null, textoSms, null, null);
                                        Toast.makeText(getActivity().getBaseContext(), "Mensaje enviado", Toast.LENGTH_LONG).show();
                                    } else {
                                        smsManager.sendTextMessage(etNumTel1.getText().toString(), null, textoSms, null, null);
                                        Toast.makeText(getActivity().getBaseContext(), "Mensaje enviado, pero sin localización", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(getActivity().getBaseContext(), "Error al enviar mensaje", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity().getBaseContext(), "No se entendió lo que dijo", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE://Si la actividad fué tomar foto
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imgFoto.setImageBitmap(imageBitmap);
                    try {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getActivity().getBaseContext(), "No tiene permisos para enviar mensajes", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, 225);
                        } else {
                            SmsManager smsManager = SmsManager.getDefault();
                            String textoSms = "¡¡AYUDA!!";
                            if (setUpdateLocation()) {
                                textoSms = textoSms + "\nLatitud: " + latitud + "\nLongitud: " + longitud;
                                smsManager.sendTextMessage(etNumTel3.getText().toString(), null, textoSms, null, null);
                                Toast.makeText(getActivity().getBaseContext(), "Mensaje enviado", Toast.LENGTH_LONG).show();
                            } else {
                                smsManager.sendTextMessage(etNumTel3.getText().toString(), null, textoSms, null, null);
                                Toast.makeText(getActivity().getBaseContext(), "Mensaje enviado, pero sin localización", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity().getBaseContext(), "Error al enviar mensaje", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
