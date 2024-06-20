package com.example.LiterAlura.service;

import com.example.LiterAlura.model.Autor;
import com.example.LiterAlura.model.Livro;
import com.example.LiterAlura.repository.AutorRepository;
import com.example.LiterAlura.repository.LivroRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsumoApi {

    private static final String BASE_URL = "http://gutendex.com/books/?search=";

    private final AutorRepository autorRepository;

    private final LivroRepository livroRepository;

    @Autowired
    public ConsumoApi(AutorRepository autorRepository, LivroRepository livroRepository) {
        this.autorRepository = autorRepository;
        this.livroRepository = livroRepository;
    }

    public List<Livro> livroPorTitulo(String title) {
        if (title != null && !title.isEmpty()) {
            String url = buildSearchUrl(title);
            String jsonResponse = obtainData(url);
            List<Livro> booksList = parseBooksFromJson(jsonResponse).stream()
                    .filter(book -> book.getTitulo().equalsIgnoreCase(title))
                    .limit(1)
                    .collect(Collectors.toList());
            livroRepository.saveAll(booksList);
            return booksList;
        } else {
            return Collections.emptyList();
        }
    }

    public List<Livro> getLivrosRegistrados() {
        return livroRepository.findAll();
    }

    private String buildSearchUrl(String title) {
        return BASE_URL + title.replaceAll(" ", "%20");
    }

    private String obtainData(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Livro> parseBooksFromJson(String json) {
        List<Livro> booksList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(json);
            JsonNode resultNode = rootNode.path("results");
            Iterator<JsonNode> elements = resultNode.elements();

            while (elements.hasNext()) {
                JsonNode bookNode = elements.next();
                Livro book = new Livro();
                book.setTitulo(bookNode.path("title").asText());

                JsonNode autorNode = bookNode.path("authors").elements().next();
                String autorNome = autorNode.path("name").asText();
                Integer anoNascimento = autorNode.path("birth_year").isInt() ? autorNode.path("birth_year").intValue() :
                        null;
                Integer anoMorte = autorNode.path("death_year").isInt() ? autorNode.path("death_year").intValue() :
                        null;

                Autor autor = autorRepository.procurarPorNome(autorNome);
                if (autor == null) {
                    autor = new Autor();
                    autor.setNome(autorNome);
                    autor.setAnoNascimento(Integer.valueOf(anoNascimento));
                    autor.setAnoMorte(Integer.valueOf(anoMorte));
                    autorRepository.save(autor);
                }
                book.setAutores(String.valueOf(autor));

                var autores = bookNode.path("authors").elements().hasNext() ?
                        bookNode.path("authors").elements().next().path("name").asText() : "Unknown";
                book.setAutores(autores);

                String language = bookNode.path("languages").elements().hasNext() ?
                        bookNode.path("languages").elements().next().asText() : "Unknown";
                book.setIdiomas(language);

                book.setDownloads(bookNode.path("download_count").asInt());

                booksList.add(book);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return booksList;
    }

    public List<Autor> getAutorPorRangeDeAno(int anoInicio, int anoFim) {
        List<Autor> allAuthors = autorRepository.findAll();
        return allAuthors.stream()
                .filter(author -> {
                    Integer birthYear = author.getAnoNascimento();
                    Integer deathYear = author.getAnoMorte();
                    return birthYear != null && birthYear <= anoFim && (deathYear == null || deathYear > anoInicio);
                })
                .collect(Collectors.toList());
    }
}
