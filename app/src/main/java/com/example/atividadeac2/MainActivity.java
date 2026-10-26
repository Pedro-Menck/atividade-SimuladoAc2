package com.example.atividadeac2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.atividadeac2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, Object> utilizador = new HashMap<>();
        utilizador.put("nome", "Pedro");
        utilizador.put("idade", 19);

        db.collection("utilizadores").add(utilizador);
    }

    private void salvarDados() {
        db.collection("utilizadores").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String nome = document.getString("nome");
                }
            }
        });
    }
}
