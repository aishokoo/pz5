import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class z1 {

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        // Імітуємо асинхронні джерела даних
        CompletableFuture<String> source1 = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            logTime(startTime, "Джерело 1 завершено");
            return "Дані з джерела 1";
        });

        CompletableFuture<String> source2 = CompletableFuture.supplyAsync(() -> {
            sleep(1500);
            logTime(startTime, "Джерело 2 завершено");
            return "Дані з джерела 2";
        });

        CompletableFuture<String> source3 = CompletableFuture.supplyAsync(() -> {
            sleep(500);
            logTime(startTime, "Джерело 3 завершено");
            return "Дані з джерела 3";
        });

        CompletableFuture<String> source4 = CompletableFuture.supplyAsync(() -> {
            sleep(1200);
            logTime(startTime, "Джерело 4 завершено");
            return "Дані з джерела 4";
        });

        CompletableFuture<String> source5 = CompletableFuture.supplyAsync(() -> {
            sleep(700);
            logTime(startTime, "Джерело 5 завершено");
            return "Дані з джерела 5";
        });

        // Обробка з thenCompose()
        CompletableFuture<String> composedResult = source1.thenCompose(data1 ->
                CompletableFuture.supplyAsync(() -> {
                    logTime(startTime, "Обробка з thenCompose завершена");
                    return data1 + " + оброблені додатково";
                })
        );

        // Комбінування результатів з thenCombine()
        CompletableFuture<String> combinedResult = source1.thenCombine(source2, (data1, data2) -> {
            logTime(startTime, "Комбінування thenCombine завершено");
            return data1 + " та " + data2;
        });

        // Виконання всіх завдань з allOf()
        CompletableFuture<Void> allSources = CompletableFuture.allOf(source1, source2, source3, source4, source5);

        CompletableFuture<String> allResults = allSources.thenApply(v -> {
            try {
                String result1 = source1.get();
                String result2 = source2.get();
                String result3 = source3.get();
                String result4 = source4.get();
                String result5 = source5.get();
                logTime(startTime, "Обробка allOf завершена");
                return "Комбіновані дані: " + result1 + ", " + result2 + ", " + result3 + ", " + result4 + ", " + result5;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        // Отримання першого завершеного завдання з anyOf()
        CompletableFuture<Object> anyResult = CompletableFuture.anyOf(source1, source2, source3, source4, source5).thenApply(result -> {
            logTime(startTime, "Перше завершене завдання anyOf");
            return result;
        });

        try {
            // Демонстрація результатів
            System.out.println("Результат thenCompose(): " + composedResult.get());
            System.out.println("Результат thenCombine(): " + combinedResult.get());
            System.out.println("Результат allOf(): " + allResults.get());
            System.out.println("Результат anyOf(): " + anyResult.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Метод для логування часу виконання
    private static void logTime(long startTime, String message) {
        long elapsedTime = System.nanoTime() - startTime;
        System.out.printf("%s через %.2f мс%n", message, elapsedTime / 1_000_000.0);
    }

    // Допоміжний метод для імітації затримки
    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
