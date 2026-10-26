package com.seu.pacote;

import com.google.firebase.firestore.Exclude;
import java.io.Serializable;

public class Media implements Serializable {
    @Exclude private String id;
    private String nome;
    private String tipo;
    private String genero;
    private int ano;
    private int nota;
    private boolean assistido;

    public Media() {}

    public Media(String nome, String tipo, String genero, int ano, int nota, boolean assistido) {
        this.nome = nome;
        this.tipo = tipo;
        this.genero = genero;
        this.ano = ano;
        this.nota = nota;
        this.assistido = assistido;
    }

    @Exclude public String getId() { return id; }
    @Exclude public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public boolean isAssistido() { return assistido; }
    public void setAssistido(boolean assistido) { this.assistido = assistido; }
}
