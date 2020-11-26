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
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class FormLogin extends AppCompatActivity {

    private TextView textCadastrar;
    private TextView msg_erro;
    private EditText edit_email;
    private EditText edit_senha;
    private Button BT_entrar;

   private FirebaseAuth usuario = FirebaseAuth.getInstance(); // Intanciando o firebase de autenticação

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        getSupportActionBar().hide(); // Esconder a Action Bar
        IniciarComponentes();

        textCadastrar = findViewById(R.id.textCadastrar);
        textCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirTelaDeCadastro();
            }
        });

        //Evento de click no botão entrar
       BT_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edit_email.getText().toString().length()== 0){  // Se o email estiver vazio, vai mostrar uma mensagem de alerta para o usuario
                    msg_erro.setText("Preencha o e-mail");

                }else if(edit_senha.getText().toString().length()== 0){ //Se a senha estiver vazia, vai mostrar uma mensagem de alerta para o usuario
                 msg_erro.setText("Preencha a senha");

                }else{
                    LogarUsuario(); // Metodo para Logar o  usuario
                }
           }
       });
    }
   private void LogarUsuario(){
        String email = edit_email.getText().toString();  // recebe a caixa de texto do email
        String senha = edit_senha.getText().toString(); // recebe a caixa de texto da senha

        // Logar um usuario com os campos email e senha no Firebase
        usuario.signInWithEmailAndPassword(email,senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {  // Se o usuario fizer a autrenticação com sucesso, vai abrir a tela principal
                    IrParaTelaPrincipal();
                    Toast.makeText(FormLogin.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) { // significa uma regra de senha
                        msg_erro.setText("Senha Inválida");
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        msg_erro.setText("Email ou senha estão incorretos");
                    } catch (FirebaseNetworkException e) {
                        msg_erro.setText("Sem conexão com a internet");
                    } catch (Exception e) {
                        msg_erro.setText("Erro ao logar usuario:" + e.getMessage()); // para algum erro diferente destes
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    // Verificar o usuario logado
    protected void onstart(){  // Se o usuario atual for diferente de nulo, ou seja se tiver algum usuario logado vai abrir a tela principal
        super.onStart();
        FirebaseUser usuarioAtual = usuario.getCurrentUser();
        if (usuarioAtual != null){
            IrParaTelaPrincipal();
        }
    }
    private void IrParaTelaPrincipal(){
        Intent intent = new Intent (FormLogin.this,TelaPrincipal.class);
        startActivity(intent);
    }
    private void IniciarComponentes(){
        textCadastrar = findViewById(R.id.textCadastrar);
        msg_erro = findViewById(R.id.msg_erro);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        BT_entrar = findViewById(R.id.BT_entrar);
    }
    private void AbrirTelaDeCadastro(){
        Intent intent = new Intent (this, FormCadastro.class);
        startActivity(intent);
    }
}