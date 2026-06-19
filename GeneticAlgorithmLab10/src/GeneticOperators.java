import java.util.Random;

public class GeneticOperators {
    private static final Random rand = new Random();

    // V(A): Простая (точечная) мутация
    public static void simpleMutation(Chromosome chromosome, double mutationRate) {
        int[] genes = chromosome.getGenes();
        boolean mutated = false;

        for (int i = 0; i < genes.length; i++) {
            if (rand.nextDouble() < mutationRate) {
                genes[i] = (genes[i] == 0) ? 1 : 0; // Инвертируем бит
                mutated = true;
            }
        }

        if (mutated) {
            chromosome.setGenes(genes);
            chromosome.calculateFitness(); // Пересчитываем значение f(x)
        }
    }

    // V(H): Мутация Транслокацией
    public static void translocationMutation(Chromosome chromosome, double mutationRate) {
        if (rand.nextDouble() > mutationRate) return;

        int[] genes = chromosome.getGenes();
        int length = genes.length;

        // Выбираем случайный участок для вырезания
        int start = rand.nextInt(length);
        int end = rand.nextInt(length);
        if (start > end) {
            int temp = start; start = end; end = temp;
        }

        // Длина вырезаемого участка
        int segmentLength = end - start + 1;
        int[] segment = new int[segmentLength];
        System.arraycopy(genes, start, segment, 0, segmentLength);

        // Временный массив без вырезанного участка
        int[] tempGenes = new int[length - segmentLength];
        System.arraycopy(genes, 0, tempGenes, 0, start);
        System.arraycopy(genes, end + 1, tempGenes, start, length - end - 1);

        // Выбираем новую позицию для вставки
        int insertPos = rand.nextInt(tempGenes.length + 1);

        // Собираем новую хромосому
        int[] newGenes = new int[length];
        System.arraycopy(tempGenes, 0, newGenes, 0, insertPos);
        System.arraycopy(segment, 0, newGenes, insertPos, segmentLength);
        System.arraycopy(tempGenes, insertPos, newGenes, insertPos + segmentLength, tempGenes.length - insertPos);

        chromosome.setGenes(newGenes);
        chromosome.calculateFitness();
    }
}