import org.apache.commons.lang3.StringUtils;

import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    private static final int COUNT_OF_THREADS = 100;
    private static final String WAY = "RLRFR";
    private static final int WAY_LENGTH = 100;

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(COUNT_OF_THREADS);
        for (int i = 0; i < COUNT_OF_THREADS; i++) {
            executorService.execute(new Thread(() -> {
                String way = generateRoute(WAY, WAY_LENGTH);
                Integer countOf_R = StringUtils.countMatches(way, 'R');
                synchronized (sizeToFreq) {
                    sizeToFreq.merge(countOf_R, 1, Integer::sum);
                }
            }));
        }

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        System.out.println(sizeToFreq.size());
        Map<Integer, Integer> sortedMap = sizeToFreq.entrySet()
                        .stream()
                        .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        System.out.print("Самое частое количество повторений " +
                sortedMap.entrySet().toArray()[0].toString().replace("=", " (встретилось "));
        System.out.println(" раз(а))");
        System.out.println("Другие размеры:");
        Integer firstElement = Integer.valueOf(sortedMap.entrySet().toArray()[0].toString().substring(0,
                sortedMap.entrySet().toArray()[0].toString().indexOf("=")));
        sortedMap.remove(firstElement);
        for (Map.Entry<Integer, Integer> entry : sortedMap.entrySet()) {
            System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)");
        }
        executorService.shutdown();
    }
}
