import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Запускаем интерфейс в специальном потоке диспетчеризации событий Swing
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        // Создаем главное окно
        JFrame frame = new JFrame("Генетический алгоритм (Вариант №11)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null); // Центрируем окно на экране

        // Панель с сеточной разметкой для элементов управления
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Создаем текстовые поля с дефолтными значениями
        JLabel lblPopSize = new JLabel("Размер популяции:");
        JTextField txtPopSize = new JTextField("10");

        JLabel lblGenerations = new JLabel("Количество поколений:");
        JTextField txtGenerations = new JTextField("50");

        JLabel lblCrossover = new JLabel("Вероятность кроссинговера (0.1 - 1.0):");
        JTextField txtCrossover = new JTextField("0.7");

        JLabel lblMutation = new JLabel("Вероятность мутации (0.1 - 1.0):");
        JTextField txtMutation = new JTextField("0.2");

        // Кнопка запуска и статусная строка
        JButton btnStart = new JButton("Запустить алгоритм");
        JLabel lblStatus = new JLabel("Статус: Ожидание ввода данных", SwingConstants.CENTER);
        lblStatus.setForeground(Color.BLUE);

        // Добавляем компоненты на панель
        panel.add(lblPopSize);      panel.add(txtPopSize);
        panel.add(lblGenerations);  panel.add(txtGenerations);
        panel.add(lblCrossover);    panel.add(txtCrossover);
        panel.add(lblMutation);     panel.add(txtMutation);
        panel.add(new JLabel(""));  // Пустая ячейка для выравнивания
        panel.add(btnStart);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(lblStatus, BorderLayout.SOUTH);

        // Обработка клика по кнопке
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Считываем и парсим данные из полей
                    int popSize = Integer.parseInt(txtPopSize.getText().trim());
                    int generations = Integer.parseInt(txtGenerations.getText().trim());
                    double crossoverRate = Double.parseDouble(txtCrossover.getText().trim());
                    double mutationRate = Double.parseDouble(txtMutation.getText().trim());

                    // Базовая проверка валидности данных
                    if (popSize < 2 || generations < 1 || crossoverRate < 0 || crossoverRate > 1 || mutationRate < 0 || mutationRate > 1) {
                        JOptionPane.showMessageDialog(frame, "Проверьте корректность диапазонов (вероятности от 0 до 1, популяция >= 2)!", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Меняем состояние интерфейса на время расчетов
                    lblStatus.setText("Статус: Выполняются расчеты и генерация графиков...");
                    lblStatus.setForeground(Color.ORANGE);
                    btnStart.setEnabled(false);

                    // Запускаем вычисления в отдельном потоке, чтобы окно приложения не зависало («Не отвечает»)
                    new Thread(() -> {
                        // Основной запуск алгоритма
                        runExperiment(popSize, generations, crossoverRate, mutationRate, true);

                        // Эксперимент 1: Мутация
                        String[] mutationLabels = {"5%", "10%", "20%", "50%", "100%"};
                        double[] mutationValues = new double[5];
                        double[] mutationRates = {0.05, 0.1, 0.2, 0.5, 1.0};
                        for (int i = 0; i < mutationRates.length; i++) {
                            mutationValues[i] = runExperiment(popSize, generations, crossoverRate, mutationRates[i], false);
                        }
                        ChartGenerator.saveParameterChart(mutationLabels, mutationValues, "Вероятность мутации", "Значение f(x)", "Влияние мутации на точность", "experiment_mutation.png");

                        // Эксперимент 2: Популяция
                        String[] popLabels = {"5", "10", "20", "50", "100"};
                        double[] popValues = new double[5];
                        int[] popSizes = {5, 10, 20, 50, 100};
                        for (int i = 0; i < popSizes.length; i++) {
                            popValues[i] = runExperiment(popSizes[i], generations, crossoverRate, mutationRate, false);
                        }
                        ChartGenerator.saveParameterChart(popLabels, popValues, "Размер популяции", "Значение f(x)", "Влияние размера популяции", "experiment_population.png");

                        // Возвращаем управление GUI-потоку по завершении
                        SwingUtilities.invokeLater(() -> {
                            lblStatus.setText("Статус: Расчет окончен. Графики сохранены!");
                            lblStatus.setForeground(new Color(0, 128, 0)); // Зеленый цвет
                            btnStart.setEnabled(true);

                            // Всплывающее уведомление об успешном завершении
                            JOptionPane.showMessageDialog(frame, "Алгоритм успешно выполнен!\nВсе графики сохранены в папку проекта.", "Готово", JOptionPane.INFORMATION_MESSAGE);
                        });
                    }).start();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Ошибка! Пожалуйста, вводите только числа.\nДля дробных чисел используйте точку (например, 0.7).", "Ошибка формата чисел", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Делаем окно видимым
        frame.setVisible(true);
    }

    private static double runExperiment(int populationSize, int maxGenerations,
                                        double crossoverRate, double mutationRate,
                                        boolean saveGraphs) {
        Random rand = new Random();
        Population population = new Population();
        List<Double> historyX = new ArrayList<>();
        List<Double> historyY = new ArrayList<>();
        List<Double> convergenceHistory = new ArrayList<>();

        population.initializeBlanket(populationSize);

        historyX.add(population.getBest().getXValue());
        historyY.add(population.getBest().getFunctionValue());
        convergenceHistory.add(population.getBest().getFunctionValue());

        for (int gen = 1; gen <= maxGenerations; gen++) {
            List<Chromosome> individuals = population.getIndividuals();

            Chromosome parent1 = Selection.rouletteWheelSelection(individuals);
            Chromosome parent2 = Selection.rouletteWheelSelection(individuals);

            Chromosome[] children;
            if (rand.nextDouble() < crossoverRate) {
                children = CrossoverOperators.fibonacciCrossover(parent1, parent2);
            } else {
                children = new Chromosome[]{parent1.cloneChromosome(), parent2.cloneChromosome()};
            }

            for (Chromosome child : children) {
                GeneticOperators.simpleMutation(child, mutationRate);
                GeneticOperators.translocationMutation(child, 0.05);
            }

            population.sortByFitness();
            if (populationSize >= 2) {
                individuals.set(populationSize - 1, children[0]);
                individuals.set(populationSize - 2, children[1]);
            }

            Chromosome best = population.getBest();
            historyX.add(best.getXValue());
            historyY.add(best.getFunctionValue());
            convergenceHistory.add(best.getFunctionValue());
        }

        Chromosome best = population.getBest();

        if (saveGraphs) {
            ChartGenerator.saveFunctionChart(best.getXValue(), best.getFunctionValue(),
                    historyX, historyY, "optimization_result.png");
            ChartGenerator.saveConvergenceChart(convergenceHistory, "convergence.png");
        }

        return best.getFunctionValue();
    }
}