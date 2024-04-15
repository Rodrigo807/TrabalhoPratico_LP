public class SomaVotos {
    private int totalVotantes;
    private int votosValidos;
    private int votosBrancos;
    private int votosNulos;

    public SomaVotos(VotosCirculoEleitoral circuloEleitoral) {
        calcularResultados(circuloEleitoral);
    }

    private void calcularResultados(VotosCirculoEleitoral circuloEleitoral) {
        for (VotosConcelho votosConcelho : circuloEleitoral.getVotosPorConcelho().values()) {
            for (Integer votos : votosConcelho.getVotosPorPartido().values()) {
                votosValidos += votos;
            }
            // Aqui, assumimos que os votos brancos e nulos são tratados como "partidos" específicos
            // dentro do mapa de votosPorPartido. Esses valores devem ser ajustados conforme a sua implementação.
            votosBrancos += votosConcelho.getVotosPorPartido().getOrDefault("Brancos", 0);
            votosNulos += votosConcelho.getVotosPorPartido().getOrDefault("Nulos", 0);
        }

        totalVotantes = votosValidos + votosBrancos + votosNulos;
    }

    // Métodos getters para acessar os resultados
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
