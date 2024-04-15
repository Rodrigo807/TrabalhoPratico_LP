import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // local dos .dat
        File folder = new File("C:\\Users\\rodri\\DadosProjetoLP");

        // Obter a lista de ficheiros DAT
        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".dat"));

        // local dos txt
        String outputFolderPath = "C:\\Users\\rodri\\DadosSaidaProjetoLP/";

        String totalNacionalFileName = "TotalNacional.txt";

        // Cria um objeto PrintWriter para escrever os resultados nacionais no ficheiro TXT
        try (PrintWriter totalNacionalWriter = new PrintWriter(new FileWriter(outputFolderPath + totalNacionalFileName))) {

            // Cria um mapa para armazenar os votos totais por partido a nível nacional
            Map<String, Integer> totalVotosPorPartidoNacional = new HashMap<>();

            // inicia brancos e nulos no ficheiro nacional
            totalVotosPorPartidoNacional.put("Brancos", 0);
            totalVotosPorPartidoNacional.put("Nulos", 0);

            // Verifica se a lista de ficheiros não é nula
            if (listOfFiles != null) {

                // Percorre a lista de ficheiros
                for (File file : listOfFiles) {

                    // Carrega os dados do ficheiro DAT
                    VotosCirculoEleitoral circuloEleitoral = carregarDados(file.getAbsolutePath());

                    // Verifica se os dados foram carregados com sucesso
                    if (circuloEleitoral != null) {

                        // define o nome do txt
                        String outputFileName = file.getName().replaceAll("\\.dat$", ".txt");

                        // Cria um objeto para escrever os resultados do circulo eleitural
                        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFolderPath + outputFileName))) {

                            // Exibe os dados agregados do círculo eleitoral no ficheiro TXT
                            mostrarDadosAgregados(circuloEleitoral, writer);

                            // Atualiza o mapa de votos totais por partido a nível nacional com os votos do círculo eleitoral
                            for (VotosConcelho votosConcelho : circuloEleitoral.getVotosPorConcelho().values()) {
                                for (Map.Entry<String, Integer> entry : votosConcelho.getVotosPorPartido().entrySet()) {
                                    totalVotosPorPartidoNacional.merge(entry.getKey(), entry.getValue(), Integer::sum);
                                }
                            }
                        } catch (IOException e) {
                            // Exibe uma mensagem de erro se houver um problema ao escrever os resultados do círculo eleitoral no ficheiro TXT
                            System.err.println("Erro ao escrever os resultados para " + outputFileName + ": " + e.getMessage());
                        }
                    }
                }
            }

            // Escreve os resultados nacionais no ficheiro TXT
            totalNacionalWriter.println("Resultados Nacionais:");
            totalNacionalWriter.println("Nº de votantes: " + getTotalVotantes(listOfFiles));
            totalNacionalWriter.println("Nº de votos válidos: " + getVotosValidos(totalVotosPorPartidoNacional));
            totalNacionalWriter.println("Nº de votos brancos: " + getVotosBrancos(totalVotosPorPartidoNacional));
            totalNacionalWriter.println("Nº de votos nulos: " + getVotosNulos(totalVotosPorPartidoNacional));
            totalNacionalWriter.println("\nResultados:");

            // Escreve os votos totais por partido no ficheiro TXT
            totalVotosPorPartidoNacional.forEach((partido, votos) ->
                    totalNacionalWriter.println(partido + " - " + votos));

        } catch (IOException e) {
            // Exibe uma mensagem de erro se houver um problema ao escrever os resultados nacionais no ficheiro TXT
            System.err.println("Erro ao escrever os resultados para " + totalNacionalFileName + ": " + e.getMessage());
        }
    }

    // Função para carregar os dados de um ficheiro DAT
    private static VotosCirculoEleitoral carregarDados(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            // Ler o objeto do ficheiro e retorná-lo
            return (VotosCirculoEleitoral) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Exibe uma mensagem de erro se houver um problema ao ler os dados do ficheiro
            System.err.println("Erro ao carregar os dados de " + filePath + ": " + e.getMessage());
            return null;
        }
    }

    // Função para exibir os dados agregados de um círculo eleitoral
    private static void mostrarDadosAgregados(VotosCirculoEleitoral circuloEleitoral, PrintWriter writer) {
        // Cria um objeto SomaVotos para calcular os resultados agregados do círculo eleitoral
        SomaVotos somaVotos = new SomaVotos(circuloEleitoral);

        // Exibe os dados agregados do círculo eleitoral no ficheiro TXT
        writer.println("Nome do círculo: " + circuloEleitoral.getNomeCirculo());
        writer.println("Nº de votantes: " + somaVotos.getTotalVotantes());
        writer.println("Nº de votos válidos: " + somaVotos.getVotosValidos());
        writer.println("Nº de votos brancos: " + somaVotos.getVotosBrancos());
        writer.println("Nº de votos nulos: " + somaVotos.getVotosNulos());
        writer.println("Resultados:");

        Map<String, Integer> totalVotosPorPartido = new HashMap<>();
        totalVotosPorPartido.put("Brancos", 0);
        totalVotosPorPartido.put("Nulos", 0);

        // Atualiza o mapa de votos totais por partido do círculo eleitoral com os votos dos concelhos
        for (VotosConcelho votosConcelho : circuloEleitoral.getVotosPorConcelho().values()) {
            for (Map.Entry<String, Integer> entry : votosConcelho.getVotosPorPartido().entrySet()) {
                totalVotosPorPartido.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        // Exibe os votos totais por partido do círculo eleitoral no ficheiro TXT
        totalVotosPorPartido.forEach((partido, votos) ->
                writer.println(partido + " - " + votos));
    }

    // Função que calcula e retorna o total de votantes em todos os ficheiros DAT
    private static int getTotalVotantes(File[] files) {
        int totalVotantes = 0;

        // Percorrer a lista de ficheiros
        for (File file : files) {
            // Carregar os dados do ficheiro DAT
            VotosCirculoEleitoral circuloEleitoral = carregarDados(file.getAbsolutePath());

            // Verificar se os dados foram carregados com sucesso
            if (circuloEleitoral != null) {
                // Criar um objeto SomaVotos para calcular os resultados agregados do círculo eleitoral
                SomaVotos somaVotos = new SomaVotos(circuloEleitoral);

                // Atualizar o total de votantes com os votantes do círculo eleitoral
                totalVotantes += somaVotos.getTotalVotantes();
            }
        }

        // Retornar o total de votantes
        return totalVotantes;
    }

// Função que retorna o número de votos válidos a partir de um mapa de votos nacionais
private static int getVotosValidos(Map<String, Integer> votosNacionais) {
    int votosValidos = 0;

    // Percorre o mapa de votos nacionais
    for (Integer valor : votosNacionais.values()) {
        // Atualiza o número de votos válidos com os votos do mapa
        votosValidos += valor;
    }

    // Retorna o número de votos válidos
    return votosValidos;
}

// Retorna o número de votos brancos a partir de um mapa de votos nacionais
private static int getVotosBrancos(Map<String, Integer> votosNacionais) {
    // Retornar o número de votos brancos do mapa de votos nacionais
    return votosNacionais.getOrDefault("Brancos", 0);
}

// Retorna o número de votos nulos a partir de um mapa de votos nacionais
private static int getVotosNulos(Map<String, Integer> votosNacionais) {
    // Retorna o número de votos nulos do mapa de votos nacionais
    return votosNacionais.getOrDefault("Nulos", 0);
}
}

// Calcula os resultados agregados de um círculo eleitoral
class SomaVotos {
private int totalVotantes;
private int votosValidos;
private int votosBrancos;
private int votosNulos;

// Construtor da classe
public SomaVotos(VotosCirculoEleitoral circuloEleitoral) {
    // Chama o método calcularResultados para calcular os resultados agregados
    calcularResultados(circuloEleitoral);
}

// Método responsável por calcular os resultados agregados
private void calcularResultados(VotosCirculoEleitoral circuloEleitoral) {
    totalVotantes = 0;
    votosValidos = 0;
    votosBrancos = 0;
    votosNulos = 0;

    // Percorre os concelhos do círculo eleitoral
    for (VotosConcelho votosConcelho : circuloEleitoral.getVotosPorConcelho().values()) {
        // Atualiza os votos válidos, brancos e nulos com os votos do concelho
        for (Integer votos : votosConcelho.getVotosPorPartido().values()) {
            votosValidos += votos;
        }

        votosBrancos += votosConcelho.getVotosPorPartido().getOrDefault("Brancos", 0);
        votosNulos += votosConcelho.getVotosPorPartido().getOrDefault("Nulos", 0);
    }

    // Atualiza o total de votantes com os votos válidos, brancos e nulos
    totalVotantes = votosValidos + votosBrancos + votosNulos;
}

public int getTotalVotantes() {
    return totalVotantes;
}

public int getVotosValidos() {
    return votosValidos;
}

public int getVotosBrancos() {
    return votosBrancos;
}

public int getVotosNulos() {
    return votosNulos;
}
}