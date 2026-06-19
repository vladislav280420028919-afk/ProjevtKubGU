import java.util.List;
import java.util.Random;

public class Selection {
    private static final Random rand = new Random();

    // III(B) & VI(A): Пропорциональная селекция (Рулетка)
    public static Chromosome rouletteWheelSelection(List<Chromosome> individuals) {
        double totalFitness = 0;
        for (Chromosome ind : individuals) {
            totalFitness += ind.getFitness();
        }

        // Крутим рулетку
        double randomValue = rand.nextDouble() * totalFitness;
        double runningSum = 0;

        for (Chromosome ind : individuals) {
            runningSum += ind.getFitness();
            if (runningSum >= randomValue) {
                return ind; // Возвращаем выбранного родителя
            }
        }

        // Резервный возврат последней особи (на случай погрешности округления)
        return individuals.get(individuals.size() - 1);
    }
}