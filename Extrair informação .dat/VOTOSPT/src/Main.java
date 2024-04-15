import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        VotosCirculoEleitoral circuloEleitoral = carregarDados();

        if (circuloEleitoral != null) {
            mostrarDadosAgregados(circuloEleitoral);
        }

        VotosCirculoEleitoral somaVotos = carregarDados();

        if (circuloEleitoral != null) {
            SomaVotos resultado = new SomaVotos(circuloEleitoral);
            System.out.println("\n");
            System.out.println("Número de votantes: " + resultado.getTotalVotantes());
            System.out.println("Número de votos válidos: " + resultado.getVotosValidos());
            System.out.println("Número de votos brancos: " + resultado.getVotosBrancos());
            System.out.println("Número de votos nulos: " + resultado.getVotosNulos());
        }
    }

    private static VotosCirculoEleitoral carregarDados() {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:\\Users\\rodri\\circulo_coimbra.dat"))) {
            return (VotosCirculoEleitoral) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar os dados: " + e.getMessage());
            return null;
        }
    }

    private static void mostrarDadosAgregados(VotosCirculoEleitoral circuloEleitoral) {
        System.out.println("\nCírculo Eleitoral: " + circuloEleitoral.getNomeCirculo());
        Map<String, Integer> totalVotosPorPartido = new HashMap<>();

        for (VotosConcelho votosConcelho : circuloEleitoral.getVotosPorConcelho().values()) {
            for (Map.Entry<String, Integer> entry : votosConcelho.getVotosPorPartido().entrySet()) {
                totalVotosPorPartido.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        totalVotosPorPartido.forEach((partido, votos) ->
                System.out.println("Partido: " + partido + " - Votos: " + votos));
    }
}
