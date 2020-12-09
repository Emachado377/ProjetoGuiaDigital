package com.firebase.projetofirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PercursoPlanejado extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percurso_planejado);

        getSupportActionBar().hide(); // Esconder a Action Bar
    }
}