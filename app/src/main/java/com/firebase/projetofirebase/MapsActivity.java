package com.firebase.projetofirebase;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

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
    private FirebaseAuth usuario = FirebaseAuth.getInstance(); // Intanciando o firebase de autenticação

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Percurso = findViewById(R.id.BT_percurso);
        GravarPosicao = findViewById(R.id.BT_gravar);
        Configurar = findViewById(R.id.BT_Configurar);
        Sair = findViewById(R.id.tx_sair);

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

    }

   @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
       LatLng sydney = new LatLng(-29.1678, -51.1794);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    // Metodo para voltar para tela de login
    private void VoltarTelaDeLogin(){
        Intent intent = new Intent (MapsActivity.this, FormLogin.class );
        startActivity(intent);
        finish();
    }
}