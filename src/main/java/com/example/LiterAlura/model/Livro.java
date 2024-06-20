package com.example.LiterAlura.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String titulo;
    private String autores;
    private String idiomas;
    private Integer downloads;
    private String autorNascimento;
    private String autorMorte;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Autor autor;

    @Override
    public String toString() {
        return ", title='" + titulo + '\'' +
                        ", authors='" + autores + '\'' +
                        ", languages='" + idiomas + '\'' +
                        ", downloads=" + downloads +
                        ", authorBirthDate='" + autorNascimento + '\'' +
                        ", authorDeathDate='" + autorMorte + "'";
    }

    private LocalDate parseLocalDate(String dataString) {
        try {
            int ano = Integer.parseInt(dataString);
            return LocalDate.of(ano, 1, 1);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public LocalDate getParsedAutorNascimento() {
        return parseLocalDate(autorNascimento);
    }
    public LocalDate getParsedAutorMorte() {
        return parseLocalDate(autorMorte);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutores() {
        return autores;
    }

    public void setAutores(String autores) {
        this.autores = autores;
    }

    public String getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(String idiomas) {
        this.idiomas = idiomas;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public String getAutorNascimento() {
        return autorNascimento;
    }

    public void setAutorNascimento(String autorNascimento) {
        this.autorNascimento = autorNascimento;
    }

    public String getAutorMorte() {
        return autorMorte;
    }

    public void setAutorMorte(String autorMorte) {
        this.autorMorte = autorMorte;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }
}
