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
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private Button Percurso;
    private Button GravarPosicao;
    private Button Configurar;
    private ImageButton Sair;
    private ImageButton SeuLocal;
    private FirebaseAuth usuario = FirebaseAuth.getInstance(); // Intanciando o firebase de autenticação

    //O classe abaixo irá fornecer os métodos para interagir com o GPS bem como recuperar os dados do posicionamento
    private FusedLocationProviderClient servicoLocalizacao;

    //Já vem com o código original, serve para referenciar o Mapa que será montado na tela bem
    //como usar os métodos para posicionar, adicionar marcador e tudo mais
    private GoogleMap mMap;

    //Variáveis para armazenar os pontos cardeais recuperados pelo GPS
    private double latitude, longitude;

    //Variável para armazenar se o usuario clicou em permitir ou não
    private boolean permitiuGPS = false;

    //Variável para armazenar o ponto retornoado pelo GPS
    Location ultimaPosicao;

    //Variavel para definir o zoom no mapa
    float zoomLevel = 8.0f;

    //Variaveis para armazenar as coordenadas
    double myLatitude;
    double myLongitude;
    double Latitude;
    double Longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Percurso = findViewById(R.id.BT_percurso);
        GravarPosicao = findViewById(R.id.BT_gravar);
        Configurar = findViewById(R.id.BT_Configurar);
        Sair = findViewById(R.id.tx_sair);
        SeuLocal = findViewById(R.id.BT_SeuLocal);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        servicoLocalizacao = LocationServices.getFusedLocationProviderClient(this);
        //Verificar se o  usuário já deu permissão para o uso do GPS
        //No caso do GPS, quando o usuário clicar para permitir ou não o acesso aos dados de localização,
        //será executado o método onRequestPermissionsResults. Dentro desse método já podemos pegar
        //os dados de latitude e longitude.

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},120);
        }else{
            permitiuGPS = true;
        }

        //Recuperação do gerenciador de localização
        LocationManager gpsHabilitado = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Verificação se o GPS está habilitado, caso não esteja...
        if(!gpsHabilitado.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //... abre a tela de configurações na opção para habilitar o GPS ou não
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            Toast.makeText(getApplicationContext(), "Para este aplicativo é necessário habilitar o GPS", Toast.LENGTH_LONG).show();
        }

        // Botões de seleção tela proncipal
        Percurso.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Valores para Teste - Desenvolver a leitura de uma lista na activity Percurso Desejado.
                Latitude = -29.100;
                Longitude = -51.100;

                atribuiPosicao();
               // Intent tela_Percurso = new Intent(MapsActivity.this, PercursoPlanejado.class);
                //startActivity(tela_Percurso);
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
                zoomLevel = 18.5f;
                recuperarPosicaoAtual();


            }
        });
    }

    // Define um indicador no mapa
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        recuperarPosicaoAtual();
    }

    // Metodo para voltar para tela de login
    private void VoltarTelaDeLogin(){
        Intent intent = new Intent (MapsActivity.this, FormLogin.class );
        startActivity(intent);
        finish();
    }



    //Adiciona o botão para centralizar o mapa na posição atual. Esse botão é aquele parecido com um
    //alvo que fica no canto superior direito do mapa.
    private void adicionaComponentesVisuais() {
        //Se o objeto do mapa não existir, encerra o carregamento no return
        if (mMap == null) {
            return;
        }
        //Try/catch somente para não aparecer algum erro ao usuário com relação à permissão
        try {
            //Teste para verificar se o usuário já permitiu o acesso ao GPS, caso sim...
            if (permitiuGPS) {
                //Adiciona o botão que quando clicado vai para a posição atual do celular/GPS
                mMap.setMyLocationEnabled(true);
                //Habilita o botão para ser clicado
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else { //Se caso o usuário não permitiu o acesso aos dados de localização
                mMap.setMyLocationEnabled(false); //Remove o botão
                mMap.getUiSettings().setMyLocationButtonEnabled(false); //Desabilita o botão

                //Limpa a última posição recuperada pois não é possível acessar o GPS sem a permissão
                ultimaPosicao  = null;

                //Pede a permissão novamente
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},120);
                }
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void recuperarPosicaoAtual() {
        try {
            //Testa se a pessoa permitiu o uso dos dados de localização
            if (permitiuGPS) {
                Task locationResult = servicoLocalizacao.getLastLocation();
                //Assim que os dados estiverem recuperados
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(Task task) {
                        if (task.isSuccessful()) {
                            //Recupera os dados de localização da última posição
                            ultimaPosicao = (Location) task.getResult();

                            //Se for um valor válido
                            if(ultimaPosicao != null){

                                //Atribui os valores de latitude e longetudo as variaveis global da activity
                                myLatitude = ultimaPosicao.getLatitude();
                                myLongitude = ultimaPosicao.getLongitude();
                                LatLng myPosition = new LatLng(myLatitude, myLongitude);

                                mMap.addMarker(new MarkerOptions().position(myPosition).title("My Position"));
                                //Move a câmera para o ponto recuperado e aplica um Zoom de 15 (valor padrão)
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition,zoomLevel));



                            }
                        } else {
                            //Exibe um Toast se o valor que recuperou do GPS não é válido
                            Toast.makeText(getApplicationContext(), "Não foi possível recuperar a posição.", Toast.LENGTH_LONG).show();
                            //Escreve o erro no LogCat
                            Log.e("TESTE_GPS", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("TESTE_GPS", e.getMessage());
        }
    }

    private void atribuiPosicao() {
        //Se for um valor válido
        if (Latitude != 0 && Longitude != 0) {

            LatLng myPosition = new LatLng(Latitude, Longitude);

            mMap.addMarker(new MarkerOptions().position(myPosition).title("My Position"));
            //Move a câmera para o ponto recuperado e aplica um Zoom de 15 (valor padrão)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, zoomLevel));
        }
    }
}