import java.util.Random;

public class CrossoverOperators {
    private static final Random rand = new Random();

    // IV(A): Стандартный одноточечный кроссинговер
    public static Chromosome[] singlePointCrossover(Chromosome parent1, Chromosome parent2) {
        int length = parent1.getGenes().length;
        // Выбираем точку разреза от 1 до length-1
        int crossoverPoint = 1 + rand.nextInt(length - 1);

        int[] child1Genes = new int[length];
        int[] child2Genes = new int[length];

        for (int i = 0; i < length; i++) {
            if (i < crossoverPoint) {
                child1Genes[i] = parent1.getGenes()[i];
                child2Genes[i] = parent2.getGenes()[i];
            } else {
                child1Genes[i] = parent2.getGenes()[i]; // Обмен хвостами
                child2Genes[i] = parent1.getGenes()[i];
            }
        }

        return createChildren(child1Genes, child2Genes);
    }

    // IV(M): Кроссинговер на основе чисел Фибоначчи
    // Точки разрыва находятся на индексах, равных числам Фибоначчи
    public static Chromosome[] fibonacciCrossover(Chromosome parent1, Chromosome parent2) {
        int length = parent1.getGenes().length;
        int[] child1Genes = new int[length];
        int[] child2Genes = new int[length];

        // Числа Фибоначчи для длины 13 бит
        int[] fibPoints = {1, 2, 3, 5, 8};

        boolean swap = false;
        int fibIndex = 0;

        for (int i = 0; i < length; i++) {
            // Если текущий индекс совпадает с числом Фибоначчи, меняем родителя
            if (fibIndex < fibPoints.length && i == fibPoints[fibIndex]) {
                swap = !swap;
                fibIndex++;
            }

            if (!swap) {
                child1Genes[i] = parent1.getGenes()[i];
                child2Genes[i] = parent2.getGenes()[i];
            } else {
                child1Genes[i] = parent2.getGenes()[i];
                child2Genes[i] = parent1.getGenes()[i];
            }
        }

        return createChildren(child1Genes, child2Genes);
    }

    // IV(G): Частично соответствующий кроссинговер (PMX)
    // Адаптирован для бинарного вектора как двухточечный обмен участками
    public static Chromosome[] pmxCrossover(Chromosome parent1, Chromosome parent2) {
        int length = parent1.getGenes().length;
        int[] child1Genes = new int[length];
        int[] child2Genes = new int[length];

        // Выбираем две точки разреза
        int point1 = rand.nextInt(length);
        int point2 = rand.nextInt(length);

        if (point1 > point2) {
            int temp = point1; point1 = point2; point2 = temp;
        }

        for (int i = 0; i < length; i++) {
            // Внутри выбранного "окна" гены берутся от второго родителя
            if (i >= point1 && i <= point2) {
                child1Genes[i] = parent2.getGenes()[i];
                child2Genes[i] = parent1.getGenes()[i];
            } else {
                // Вне окна - от первого
                child1Genes[i] = parent1.getGenes()[i];
                child2Genes[i] = parent2.getGenes()[i];
            }
        }

        return createChildren(child1Genes, child2Genes);
    }

    // Вспомогательный метод для создания объектов потомков
    private static Chromosome[] createChildren(int[] genes1, int[] genes2) {
        Chromosome child1 = new Chromosome();
        child1.setGenes(genes1);
        child1.calculateFitness();

        Chromosome child2 = new Chromosome();
        child2.setGenes(genes2);
        child2.calculateFitness();

        return new Chromosome[]{child1, child2};
    }
}