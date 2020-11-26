package com.firebase.projetofirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class FormCadastro extends AppCompatActivity {
 private EditText edit_email;
 private EditText edit_senha;
 private Button BT_cadastrar;
 private TextView msg_erro;

 private FirebaseAuth usuario = FirebaseAuth.getInstance(); // Intanciando o firebase de autenticação
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        getSupportActionBar().hide(); // Esconder a Action Bar
        IniciarComponentes();
        // criando um evento de click no botão cadastrar
      BT_cadastrar.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              if(edit_email.getText().toString().length()== 0){  // Se o email estiver vazio, vai mostrar uma mensagem de alerta para o usuario
                msg_erro.setText("Preencha o e-mail");

              }else if(edit_senha.getText().toString().length()== 0){ //Se a senha estiver vazia, vai mostrar uma mensagem de alerta para o usuario
                  msg_erro.setText("Preencha a senha");

              }else{
                  CadastrarUsuario(); // Metodo para cadastrar usuario
              }

          }
      });
    }

    private void IniciarComponentes(){
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        BT_cadastrar = findViewById(R.id.BT_cadastrar);
        msg_erro = findViewById(R.id.msg_erro);
    }
    private void CadastrarUsuario(){
        String email = edit_email.getText().toString();  // recebe a caixa de texto do email
        String senha = edit_senha.getText().toString(); // recebe a caixa de texto da senha

        // Criando um usuario com os campos email e senha no Firebase
        usuario.createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){  // Se o cadastro for realizado com sucesso vai abrir a tela de login
                    VoltaParaTelaAtivityFormLogin();
                    Toast.makeText(FormCadastro.this,"Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e) { // significa uma regra de senha
                        msg_erro.setText("Digite uma senha com no minimo 6 caracteres");
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        msg_erro.setText("Digite uma e-mail valido");
                    }catch (FirebaseAuthUserCollisionException e) {
                        msg_erro.setText("Esta conta já foi cadastrada");
                    }catch (FirebaseNetworkException e) {
                        msg_erro.setText("Sem conexão com a internet");
                    }catch (Exception e){
                      msg_erro.setText("Ao cadastrar usuario:"+ e.getMessage()); // para algum erro diferente destes
                     e.printStackTrace();
                    }
                }
            }
        });
    }
    private void VoltaParaTelaAtivityFormLogin(){
        Intent intent = new Intent(FormCadastro.this, FormLogin.class);
        startActivity(intent);
    }
}