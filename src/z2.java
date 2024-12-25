import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class z2 {

    public static void main(String[] args) {
        //Імітуємо перевірку різних варіантів транспорту
        CompletableFuture<Route> trainRoute = getTrainRoute();
        CompletableFuture<Route> busRoute = getBusRoute();
        CompletableFuture<Route> flightRoute = getFlightRoute();

        //Обробляємо результати за допомогою allOf(), щоб дочекатися завершення всіх завдань
        CompletableFuture<Void> allRoutes = CompletableFuture.allOf(trainRoute, busRoute, flightRoute);

        CompletableFuture<Route> bestRoute = allRoutes.thenCompose(v -> {
            // Отримуємо результати всіх маршрутів
            Route train = trainRoute.join();
            Route bus = busRoute.join();
            Route flight = flightRoute.join();

            // Знаходимо найкращий маршрут за середнім між найдешевшим та найшвидшим
            return CompletableFuture.completedFuture(findBalancedRoute(train, bus, flight));
        });

        //Використання anyOf() для швидкого результату, якщо потрібно отримати перший маршрут
        CompletableFuture<Object> firstCompleted = CompletableFuture.anyOf(trainRoute, busRoute, flightRoute);

        // Виводимо результати
        bestRoute.thenAccept(route -> System.out.println("Найкращий маршрут: " + route));
        firstCompleted.thenAccept(route -> System.out.println("Перший знайдений маршрут: " + route));

        //Затримка, щоб дочекатися завершення асинхронних задач перед завершенням програми
        sleep(5);
    }

    private static Route findBalancedRoute(Route... routes) {
        Route cheapest = null;
        Route fastest = null;
        double minPrice = Double.MAX_VALUE;
        double minDuration = Double.MAX_VALUE;

        // Знаходимо найдешевший і найшвидший маршрути
        for (Route route : routes) {
            if (route.price < minPrice) {
                minPrice = route.price;
                cheapest = route;
            }
            if (route.duration < minDuration) {
                minDuration = route.duration;
                fastest = route;
            }
        }

        // Рахуємо середнє між найдешевшим і найшвидшим маршрутом
        double averagePrice = (cheapest.price + fastest.price) / 2;
        double averageDuration = (cheapest.duration + fastest.duration) / 2;

        // Знаходимо маршрут, найближчий до середніх значень
        Route balancedRoute = null;
        double bestScore = Double.MAX_VALUE;
        for (Route route : routes) {
            double score = Math.abs(route.price - averagePrice) + Math.abs(route.duration - averageDuration);
            if (score < bestScore) {
                bestScore = score;
                balancedRoute = route;
            }
        }

        return balancedRoute;
    }

    private static CompletableFuture<Route> getTrainRoute() {
        return CompletableFuture.supplyAsync(() -> {
            sleep(2); // Імітуємо затримку
            return new Route("Поїзд", 50, 5);
        });
    }

    private static CompletableFuture<Route> getBusRoute() {
        return CompletableFuture.supplyAsync(() -> {
            sleep(3);
            return new Route("Автобус", 30, 8);
        });
    }

    private static CompletableFuture<Route> getFlightRoute() {
        return CompletableFuture.supplyAsync(() -> {
            sleep(1);
            return new Route("Літак", 150, 2);
        });
    }

    private static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Клас для зберігання інформації про маршрут
    static class Route {
        String type;
        double price;
        double duration;

        public Route(String type, double price, double duration) {
            this.type = type;
            this.price = price;
            this.duration = duration;
        }

        @Override
        public String toString() {
            return "Тип: " + type + ", Ціна: " + price + ", Тривалість: " + duration + " годин";
        }
    }
}
