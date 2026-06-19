import java.util.Random;

public class Chromosome {
    // Длина хромосомы (13 бит - число Фибоначчи, отличная длина для ваших операторов)
    private static final int GENE_LENGTH = 13;
    private static final double MIN_X = 3.0;
    private static final double MAX_X = 7.0;

    private int[] genes;
    private double fitness;
    private double xValue;
    private double functionValue;

    // Конструктор: случайная генерация (для стратегии "одеяла")
    public Chromosome() {
        genes = new int[GENE_LENGTH];
        Random rand = new Random();
        for (int i = 0; i < GENE_LENGTH; i++) {
            genes[i] = rand.nextBoolean() ? 1 : 0;
        }
        calculateFitness();
    }

    // Декодирование: перевод бинарного массива в вещественное число на отрезке [3, 7]
    private void decodeGenes() {
        int decimalValue = 0;
        for (int i = 0; i < GENE_LENGTH; i++) {
            decimalValue += genes[i] * Math.pow(2, GENE_LENGTH - 1 - i);
        }
        // Масштабируем десятичное значение на наш интервал [3, 7]
        int maxDecimal = (int) Math.pow(2, GENE_LENGTH) - 1;
        xValue = MIN_X + ((double) decimalValue / maxDecimal) * (MAX_X - MIN_X);
    }

    // Вычисление целевой функции f(x) = x^2 - 5x + 6 и функции приспособленности (Fitness)
    public void calculateFitness() {
        decodeGenes();
        functionValue = Math.pow(xValue, 2) - 5 * xValue + 6;

        // Так как нам нужен МИНИМУМ функции (f(x) -> min), 
        // а алгоритм селекции ищет МАКСИМУМ приспособленности (Fitness -> max),
        // инвертируем значение. Добавляем 1.0 в знаменатель, чтобы избежать деления на 0.
        fitness = 1.0 / (1.0 + functionValue);
    }

    // --- Геттеры и Сеттеры ---

    public int[] getGenes() {
        return genes;
    }

    public void setGenes(int[] genes) {
        this.genes = genes;
    }

    public double getFitness() {
        return fitness;
    }

    public double getXValue() {
        return xValue;
    }

    public double getFunctionValue() {
        return functionValue;
    }

    // --- Вспомогательные методы ---

    // Красивый вывод генотипа в виде строки (нужно для отладки и консоли)
    public String getGenesString() {
        StringBuilder sb = new StringBuilder();
        for (int gene : genes) {
            sb.append(gene);
        }
        return sb.toString();
    }

    // Метод глубокого копирования хромосомы (важно для кроссинговера и микроэволюции)
    public Chromosome cloneChromosome() {
        Chromosome clone = new Chromosome();
        // Копируем сам массив генов, а не ссылку на него
        System.arraycopy(this.genes, 0, clone.genes, 0, GENE_LENGTH);
        clone.calculateFitness(); // Пересчитываем характеристики для клона
        return clone;
    }
}