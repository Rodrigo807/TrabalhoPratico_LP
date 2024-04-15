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
        // Diretório dos arquivos .dat
        File folder = new File("C:\\Users\\rodri\\DadosProjetoLP");
        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".dat"));

        // Caminho da pasta de saída (assegure-se de que termina com uma barra)
        String outputFolderPath = "C:\\Users\\rodri\\DadosSaidaProjetoLP/";

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                VotosCirculoEleitoral circuloEleitoral = carregarDados(file.getAbsolutePath());

                if (circuloEleitoral != null) {
                    // Gera um nome de arquivo de saída baseado no nome do arquivo de entrada
                    String outputFileName = file.getName().replaceAll("\\.dat$", ".txt");
                    try (PrintWriter writer = new PrintWriter(new FileWriter(outputFolderPath + outputFileName))) {
                        mostrarDadosAgregados(circuloEleitoral, writer);
                    } catch (IOException e) {
                        System.err.println("Erro ao escrever os resultados para " + outputFileName + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private static VotosCirculoEleitoral carregarDados(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (VotosCirculoEleitoral) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar os dados de " + filePath + ": " + e.getMessage());
            return null;
        }
    }

    private static void mostrarDadosAgregados(VotosCirculoEleitoral circuloEleitoral, PrintWriter writer) {
        // Criando um objeto SomaVotos a partir do circuloEleitoral
        SomaVotos resultado = new SomaVotos(circuloEleitoral);

        writer.println("Nome do círculo: " + circuloEleitoral.getNomeCirculo());
        writer.println("Nº de votantes: " + resultado.getTotalVotantes());
        writer.println("Nº de votos válidos: " + resultado.getVotosValidos());
        writer.println("Nº de votos brancos: " + resultado.getVotosBrancos());
        writer.println("Nº de votos nulos: " + resultado.getVotosNulos());
        writer.println("Resultados:");

        Map<String, Integer> totalVotosPorPartido = new HashMap<>();
        // Aqui, preenchemos o mapa com votos válidos dos partidos, excluindo "Brancos" e "Nulos"
        for (VotosConcelho votosConcelho : circuloEleitoral.getVotosPorConcelho().values()) {
            for (Map.Entry<String, Integer> entry : votosConcelho.getVotosPorPartido().entrySet()) {
                if (!entry.getKey().equals("Brancos") && !entry.getKey().equals("Nulos")) {
                    totalVotosPorPartido.merge(entry.getKey(), entry.getValue(), Integer::sum);
                }
            }
        }

        totalVotosPorPartido.forEach((partido, votos) ->
                writer.println(partido + " - " + votos));
    }
}
