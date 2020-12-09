package com.firebase.projetofirebase;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button Percurso;
    private Button GravarPosicao;
    private Button Configurar;
    private ImageButton Sair;
    private ImageButton SeuLocal;
    private FirebaseAuth usuario = FirebaseAuth.getInstance(); // Intanciando o firebase de autenticação
    double latitude;// = -29.1678;
    double longitude;// =-51.1794;
    int permissao_OK = 1; // 1 - sem permissão  2- com permissão
    int posicaoLocal = 1; // 1- posição inicial  2- Posição do usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Percurso = findViewById(R.id.BT_percurso);
        GravarPosicao = findViewById(R.id.BT_gravar);
        Configurar = findViewById(R.id.BT_Configurar);
        Sair = findViewById(R.id.tx_sair);
        SeuLocal = findViewById(R.id.BT_SeuLocal);
        
        pedirPermissoes();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);


        Percurso.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent tela_Percurso = new Intent(MapsActivity.this, PercursoPlanejado.class);
                startActivity(tela_Percurso);
            }
        });

        GravarPosicao.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent tela_Percurso = new Intent(MapsActivity.this, GravarPosicao.class);
                startActivity(tela_Percurso);
            }
        });

        Configurar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent tela_Percurso = new Intent(MapsActivity.this, ConfigurarPerfil.class);
                startActivity(tela_Percurso);
            }
        });
        Sair.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                usuario.signOut();
                VoltarTelaDeLogin();
            }
        });
        SeuLocal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                posicaoLocal = 2;
                configurarServico();
            }

        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) { // Define um indicador no mapa
        mMap = googleMap;
        float zoomLevel = 16.0f;
        if (permissao_OK == 2) {
            if (posicaoLocal == 2) {
                // Add a marker in Sydney and move the camera
                // LatLng sydney = new LatLng(-29.1678, -51.1794);
                LatLng sydney = new LatLng(59.1678, -51.1794);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            } else if (posicaoLocal == 1) {
                LatLng posAtual = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(posAtual).title("Sua Posição Atual"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posAtual, zoomLevel));
            }

        } else {
            pedirPermissoes();
        }
    }

    // Metodo para voltar para tela de login
    private void VoltarTelaDeLogin(){
        Intent intent = new Intent (MapsActivity.this, FormLogin.class );
        startActivity(intent);
        finish();
    }

    private void pedirPermissoes() { // Busca as informações de permissões no "Manifest"
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            System.out.println("Problema no manifest");
        }
        else{
            configurarServico();
            System.out.println("Pedir Permissões");
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissao_OK = 2;
                    configurarServico();

                } else {
                    Toast.makeText(this, "Não vai funcionar!!!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void configurarServico(){  // Busca a informação do nosso dispositivo e exibe no aplicativo

        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() { // locationListener fica atento as mudanças do sistema de localização
                public void onLocationChanged(Location location) { // Quando mudar a localização  esse metodo chama o metodo de atualização
                    atualizar(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) { } //quando muda o status da localização ( falha GPS, sinal fraco ...)

                public void onProviderEnabled(String provider) { }

                public void onProviderDisabled(String provider) { }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch(SecurityException ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void atualizar(Location location) // Executa quando o "onLocationChanged" identifica a mudança de localiação do individuo
    {
        //Double latPoint = location.getLatitude();
        // Double lngPoint = location.getLongitude();

        Double myLatitude = location.getLatitude();
        Double myLongitude = location.getLongitude();
        LatLng myPosition = new LatLng(myLatitude, myLongitude);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(myPosition).title("My Position"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition,
                13.5f));

        System.out.println("atualizar");
    }

}