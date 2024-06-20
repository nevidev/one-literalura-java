package com.example.LiterAlura.principal;

import com.example.LiterAlura.model.Autor;
import com.example.LiterAlura.model.Livro;
import com.example.LiterAlura.service.ConsumoApi;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {

    private final ConsumoApi consumoApi;

    private final Scanner sc = new Scanner(System.in);

    public Principal(ConsumoApi consumoApi) {
        this.consumoApi = consumoApi;

    }

    public void exibeMenu() {
        int x = -1;
        while (x != 0) {
            System.out.println("\n*********** LiterAlura ***********");
            System.out.println("\n******** Escolha uma opção ********");
            System.out.println("1 - Buscar livro pelo título");
            System.out.println("2 - Listar livros registrados");
            System.out.println("3 - Listar autores registrados");
            System.out.println("4 - Listar autores vivos em determinado ano");
            System.out.println("5 - Listar livros registrados por idioma");
            System.out.println("0 - Sair");

            x = sc.nextInt();
            sc.nextLine();

            switch (x) {
                case 1:
                    acharLivro();
                    break;
                case 2:
                    mostrarLivrosRegistrados();
                    break;
                case 3:
                    mostrarAutoresRegistrados();
                    break;
                case 4:
                    mostrarAutorPorAno();
                    break;
                case 5:
                    livrosPorIdioma();
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void acharLivro() {
        try {
            System.out.println("Digite o título do livro: ");
            String titulo = sc.nextLine();
            List<Livro> livro = consumoApi.livroPorTitulo(titulo);
            if (livro.isEmpty()) {
                System.out.println("Não há livro com esse título");
            } else {
                livro.forEach(System.out::println);
            }
        } catch (InputMismatchException e) {
            System.out.println("Nome inválido");
        }
    }

    private void mostrarLivrosRegistrados() {
        List<Livro> livro = consumoApi.getLivrosRegistrados();
        if (livro.isEmpty()) {
            System.out.println("Não há livros registrados");
        } else {
            System.out.println("Livros registrados: ");
            livro.forEach(livros -> {
                System.out.println("Título: " + livros.getTitulo());
                System.out.println("Autor: " + livros.getAutores());
                System.out.println("Idioma: " + livros.getIdiomas());
                System.out.println("Número de downloads: " + livros.getDownloads());
                System.out.println();
            });
        }
    }

    private void mostrarAutoresRegistrados() {
        List<Livro> livrosRegistrados = consumoApi.getLivrosRegistrados();
        if (livrosRegistrados.isEmpty()) {
            System.out.println("Não há livros registrados");
        } else {
            Map<String, Autor> autorUnico = new HashMap<>();
            for (Livro livros : livrosRegistrados) {
                Autor autor = livros.getAutor();
                if (autor != null && !autorUnico.containsKey(autor.getNome())) {
                    autorUnico.put(autor.getNome(), autor);
                }
            }

            for (Map.Entry<String, Autor> entry : autorUnico.entrySet()) {
                Autor autor = entry.getValue();
                System.out.println("Nome: " + autor.getNome());
                System.out.println("Nascimento: " + (autor.getAnoNascimento() != null ? autor.getAnoNascimento() :
                        "Desconhecida"));
                System.out.println("Falecimento: " + (autor.getAnoMorte() != null ? autor.getAnoMorte() :
                        "Desconhecida"));
            }
        }
    }

    private void mostrarAutorPorAno() {
        System.out.println("Digite um ano: ");
        int anoInicio = sc.nextInt();
        sc.nextLine();
        int anoAtual = LocalDate.now().getYear();

        List<Autor> autor = consumoApi.getAutorPorRangeDeAno(anoInicio, anoAtual);
        if (autor.isEmpty()) {
            System.out.println("Não encontramos autores vivos nesse ano");
        } else {
            System.out.println("Autores vivos desde o ano " + anoInicio + " até o ano " + anoAtual + ": ");
            boolean autorVivo = false;
            for (Autor autores : autor) {
                Integer anoMorte = autores.getAnoMorte();
                if (anoMorte == null || anoMorte > anoAtual) {
                    autorVivo = true;
                    System.out.println("Nome: " + autores.getNome());
                    System.out.println("Nascimento: " + (autores.getAnoNascimento() != null ?
                            autores.getAnoNascimento() : "Desconhecida"));
                    System.out.println("Falecimento: " + (anoMorte)!= null ? anoMorte : "Vivo");
                    System.out.println();
                }
            }
            if (!autorVivo) {
                System.out.println("Não há autores dentro do tempo especificado");
            }
        }
    }
    private void livrosPorIdioma() {
        System.out.println("Digite um idioma (en - inglês, fr - francês, pt - português e es - espanhol): ");
        String idioma = sc.nextLine();
        List<Livro> livrosIdioma = consumoApi.getLivrosRegistrados().stream()
                .filter(livro -> livro.getIdiomas().equalsIgnoreCase(idioma))
                .collect(Collectors.toList());
        if (livrosIdioma.isEmpty()) {
            System.out.println("Não há livros com esse idioma");
        } else {
            livrosIdioma.forEach(System.out::println);
        }
    }
}
