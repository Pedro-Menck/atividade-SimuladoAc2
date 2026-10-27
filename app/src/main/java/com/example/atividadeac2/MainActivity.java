package com.example.atividadeac2;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.seu.pacote.Media;
import com.seu.pacote.MediaAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MediaAdapter.OnItemClickListener {

    private EditText edtNome, edtAno, edtNota;
    private Spinner spnTipo, spnGenero, spnFiltroTipo;
    private CheckBox chkAssistido, chkFiltroAssistido;
    private Button btnSalvar;
    private RecyclerView recyclerView;

    private MediaAdapter adapter;
    private final List<Media> listaCompleta = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String mediaEdicaoId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarViews();
        configurarSpinners();
        configurarRecyclerView();
        configurarListeners();
        ouvirDadosFirestore();
    }

    private void inicializarViews() {
        edtNome = findViewById(R.id.edtNome);
        spnTipo = findViewById(R.id.spnTipo);
        spnGenero = findViewById(R.id.spnGenero);
        edtAno = findViewById(R.id.edtAno);
        edtNota = findViewById(R.id.edtNota);
        chkAssistido = findViewById(R.id.chkAssistido);
        btnSalvar = findViewById(R.id.btnSalvar);

        spnFiltroTipo = findViewById(R.id.spnFiltroTipo);
        chkFiltroAssistido = findViewById(R.id.chkFiltroAssistido);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void configurarSpinners() {
        String[] tipos = new String[]{"Filme", "Série"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipo.setAdapter(adapterTipo);

        String[] generos = new String[]{"Ação", "Comédia", "Drama", "Ficção Científica", "Terror", "Romance", "Animação", "Documentário", "Outro"};
        ArrayAdapter<String> adapterGenero = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, generos);
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGenero.setAdapter(adapterGenero);

        String[] tiposFiltro = new String[]{"Todos", "Filme", "Série"};
        ArrayAdapter<String> adapterFiltroTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposFiltro);
        adapterFiltroTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFiltroTipo.setAdapter(adapterFiltroTipo);
    }

    private void configurarRecyclerView() {
        adapter = new MediaAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void configurarListeners() {
        btnSalvar.setOnClickListener(v -> salvarMedia());

        spnFiltroTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        chkFiltroAssistido.setOnCheckedChangeListener((buttonView, isChecked) -> aplicarFiltros());
    }

    private void salvarMedia() {
        String nome = edtNome.getText().toString().trim();
        String anoStr = edtAno.getText().toString().trim();
        String notaStr = edtNota.getText().toString().trim();

        if (nome.isEmpty()) {
            edtNome.setError("Informe o nome do filme/série");
            edtNome.requestFocus();
            return;
        }

        if (anoStr.isEmpty()) {
            edtAno.setError("Informe o ano de lançamento");
            edtAno.requestFocus();
            return;
        }

        if (notaStr.isEmpty()) {
            edtNota.setError("Informe uma nota de 0 a 10");
            edtNota.requestFocus();
            return;
        }

        int ano, nota;
        try {
            ano = Integer.parseInt(anoStr);
        } catch (NumberFormatException e) {
            edtAno.setError("Ano inválido");
            edtAno.requestFocus();
            return;
        }

        try {
            nota = Integer.parseInt(notaStr);
            if (nota < 0 || nota > 10) {
                edtNota.setError("Nota deve ser entre 0 e 10");
                edtNota.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtNota.setError("Nota inválida");
            edtNota.requestFocus();
            return;
        }

        String tipo = spnTipo.getSelectedItem() != null ? spnTipo.getSelectedItem().toString() : "Filme";
        String genero = spnGenero.getSelectedItem() != null ? spnGenero.getSelectedItem().toString() : "Outro";
        boolean assistido = chkAssistido.isChecked();

        Media media = new Media(nome, tipo, genero, ano, nota, assistido);

        btnSalvar.setEnabled(false);

        if (mediaEdicaoId != null) {
            db.collection("medias").document(mediaEdicaoId)
                    .set(media)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity.this, "Mídia atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                        limparFormulario();
                        btnSalvar.setEnabled(true);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Erro ao atualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnSalvar.setEnabled(true);
                    });
        } else {
            db.collection("medias")
                    .add(media)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(MainActivity.this, "Mídia salva com sucesso!", Toast.LENGTH_SHORT).show();
                        limparFormulario();
                        btnSalvar.setEnabled(true);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnSalvar.setEnabled(true);
                    });
        }
    }

    private void ouvirDadosFirestore() {
        db.collection("medias").addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(MainActivity.this, "Erro ao carregar dados: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (value != null) {
                listaCompleta.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Media media = doc.toObject(Media.class);
                    media.setId(doc.getId());
                    listaCompleta.add(media);
                }
                aplicarFiltros();
            }
        });
    }

    private void aplicarFiltros() {
        String filtroTipo = spnFiltroTipo.getSelectedItem() != null ? spnFiltroTipo.getSelectedItem().toString() : "Todos";
        boolean soAssistidos = chkFiltroAssistido.isChecked();

        List<Media> listaFiltrada = new ArrayList<>();
        for (Media m : listaCompleta) {
            boolean atendeTipo = filtroTipo.equalsIgnoreCase("Todos") || m.getTipo().equalsIgnoreCase(filtroTipo);
            boolean atendeAssistido = !soAssistidos || m.isAssistido();

            if (atendeTipo && atendeAssistido) {
                listaFiltrada.add(m);
            }
        }
        adapter.updateList(listaFiltrada);
    }

    private void limparFormulario() {
        edtNome.setText("");
        edtAno.setText("");
        edtNota.setText("");
        spnTipo.setSelection(0);
        spnGenero.setSelection(0);
        chkAssistido.setChecked(false);
        mediaEdicaoId = null;
        btnSalvar.setText("Salvar");
    }

    @Override
    public void onItemClick(Media media) {
        preencherFormularioParaEdicao(media);
    }

    @Override
    public void onItemLongClick(Media media) {
        new AlertDialog.Builder(this)
                .setTitle("Opções para: " + media.getNome())
                .setItems(new String[]{"Editar", "Excluir"}, (dialog, which) -> {
                    if (which == 0) {
                        preencherFormularioParaEdicao(media);
                    } else if (which == 1) {
                        excluirMedia(media);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void preencherFormularioParaEdicao(Media media) {
        edtNome.setText(media.getNome());
        edtAno.setText(String.valueOf(media.getAno()));
        edtNota.setText(String.valueOf(media.getNota()));
        chkAssistido.setChecked(media.isAssistido());
        mediaEdicaoId = media.getId();
        btnSalvar.setText("Atualizar");

        selecionarItemSpinner(spnTipo, media.getTipo());
        selecionarItemSpinner(spnGenero, media.getGenero());

        edtNome.requestFocus();
        Toast.makeText(this, "Editando: " + media.getNome(), Toast.LENGTH_SHORT).show();
    }

    private void selecionarItemSpinner(Spinner spinner, String valor) {
        if (valor == null) return;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(valor)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void excluirMedia(Media media) {
        if (media.getId() == null) return;

        db.collection("medias").document(media.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Mídia excluída com sucesso!", Toast.LENGTH_SHORT).show();
                    if (java.util.Objects.equals(media.getId(), mediaEdicaoId)) {
                        limparFormulario();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Erro ao excluir: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
