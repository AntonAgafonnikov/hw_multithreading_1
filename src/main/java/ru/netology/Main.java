package ru.netology;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws Exception {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

        // Узнаем количество ядер процессора для оптимальной работы в режиме многопоточности
        int cores = Runtime.getRuntime().availableProcessors();
        // Создадим пул потоков и список из FutureTask
        ExecutorService threadPool = Executors.newFixedThreadPool(cores);
        List<Future<Integer>> futureList = new ArrayList<>();

        for (String text : texts) {
            // Перемещаем общую логику программы в лямбду интерфейса Callable
            Callable<Integer> logic = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };
            // Отправляем задачу на выполнение в пул потоков и добавляем её в общий список задач
            Future<Integer> futureTask = threadPool.submit(logic);
            futureList.add(futureTask);
        }

        // Пробежимся по списку futureList и найдём максимальный интервал значений
        int max = 0;
        for (Future<Integer> i : futureList) {
            max = Math.max(i.get(), max);
        }
        System.out.println("Максимальный интервал значений: " + max);

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
        // Завершим работу пула потоков
        threadPool.shutdown();

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}