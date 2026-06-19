import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Population {
    private List<Chromosome> individuals;

    public Population() {
        this.individuals = new ArrayList<>();
    }

    // II(A): Стратегия "одеяла" (равномерный случайный поиск)
    public void initializeBlanket(int populationSize) {
        individuals.clear();
        for (int i = 0; i < populationSize; i++) {
            individuals.add(new Chromosome()); // В конструкторе уже заложена случайная генерация
        }
    }

    // II(C): Стратегия "фокусировки" (генерация вокруг перспективной точки)
    public void initializeFocus(int populationSize, Chromosome seedChromosome) {
        individuals.clear();
        individuals.add(seedChromosome); // Добавляем "семя"

        for (int i = 1; i < populationSize; i++) {
            Chromosome clone = seedChromosome.cloneChromosome();
            // Применяем мутацию с высоким шансом, чтобы создать разброс вокруг центра
            GeneticOperators.simpleMutation(clone, 0.15);
            individuals.add(clone);
        }
    }

    // Метод для сортировки популяции по Fitness (полезно для Элитного отбора)
    public void sortByFitness() {
        individuals.sort(Comparator.comparingDouble(Chromosome::getFitness).reversed());
    }

    public List<Chromosome> getIndividuals() {
        return individuals;
    }

    // Получить лучшую особь
    public Chromosome getBest() {
        sortByFitness();
        return individuals.get(0);
    }
}