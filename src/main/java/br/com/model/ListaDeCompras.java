package br.com.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListaDeCompras {
    private List<Produto> produtos;

    public ListaDeCompras() {
        produtos = new ArrayList<>();
    }

    // Adiciona um produto à lista
    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
    }

    // Remove um produto da lista pelo nome
    public void removerProduto(String nome) {
        produtos.removeIf(p -> p.getNome().equalsIgnoreCase(nome));
    }

    public void salvarEmArquivoTexto(String nomeArquivo){
        if(!produtos.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
                for (Produto produto : produtos) {
                    writer.write(produto.getNome() + " - " + produto.getQuantidade() + " - " +
                            produto.getPreco());
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
            }
        }else{
            System.out.println("Lista vazia!");
        }
    }

    public void carregarDeArquivoTexto(String nomeArquivo) {
        produtos.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(" - ");
                produtos.add(new Produto(partes[0], Integer.parseInt(partes[1]), Double.parseDouble(partes[2])));
            }
        }catch (IOException e) {
            System.out.println("Erro ao carregar o arquivo: "+e.getMessage());
        }
    }

    public void salvarEmArquivoBinario(String nomeArquivo) {
        if(!produtos.isEmpty()){
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
                oos.writeObject(produtos);
            } catch (IOException e) {
                System.out.println("Erro ao salvar o arquivo: "+e.getMessage());
            }
        }else{
            System.out.println("Lista vazia!");
        }
    }

    @SuppressWarnings("unchecked") // Suprime avisos de operações não verificadas, esta anotação é usada para silenciar aviso do compilador.
    public void carregarDeArquivoBinario(String nomeArquivo) {
        produtos.clear();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomeArquivo))) {
            produtos = (List<Produto>) ois.readObject();
        } catch (ClassNotFoundException | IOException e){
            System.out.println("Erro ao salvar o arquivo: "+e.getMessage());
        }
    }

    public void salvarEmArquivoJson(String nomeArquivo) {
        if(!produtos.isEmpty()){
            try  {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Formata o JSON para ser legível
                objectMapper.writeValue(new File(nomeArquivo), produtos);
            } catch (IOException e) {
                System.out.println("Erro ao salvar o arquivo: "+e.getMessage());
            }
        }else{
            System.out.println("Lista vazia!");
        }

    }

    public void carregarDeArquivoJson(String nomeArquivo)  {
        produtos.clear();
        try  {
            ObjectMapper objectMapper = new ObjectMapper();
            //getTypeFactory(): acessa o TypeFactory, que é responsável por construir tipos genéricos e complexos que Jackson não consegue inferir automaticamente (como listas, mapas...)
            //constructCollectionType(): cria um tipo genérico que representa uma coleção (List) de elementos do tipo Produto.
            produtos = objectMapper.readValue(new File(nomeArquivo), objectMapper.getTypeFactory().constructCollectionType(List.class, Produto.class));
        } catch (IOException e){
            System.out.println("Erro ao salvar o arquivo: "+e.getMessage());
        }
    }

    // Filtra produtos com quantidade mínima usando streams
    public List<Produto> filtrarPorQuantidadeMinima(int quantidadeMinima) {
        return produtos.stream()
                .filter(p -> p.getQuantidade() >= quantidadeMinima)
                .collect(Collectors.toList());
    }

    // Calcula o valor total da lista usando streams
    public double calcularValorTotal() {
        return produtos.stream()
                .mapToDouble(p -> p.getQuantidade() * p.getPreco())
                .sum();
    }

    // Imprime a lista de produtos em ordem alfabética
    public void imprimirLista() {
        produtos.stream()
                .sorted(Comparator.comparing(p -> p.getNome().toLowerCase())) // Ordena por nome
                .forEach(System.out::println);
    }


    @Override
    public String toString() {
        if (produtos.isEmpty()) {
            return "Lista de compras vazia.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Lista de Compras ---\n");

        // Itera sobre os produtos e adiciona cada um ao StringBuilder
        for (int i = 0; i < produtos.size(); i++) {
            sb.append((i + 1)).append(". ").append(produtos.get(i).toString()).append("\n");
        }
        return sb.toString();
    }
}
