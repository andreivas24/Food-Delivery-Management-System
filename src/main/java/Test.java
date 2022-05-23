import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
public class Test implements Serializable {

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException {
        /*List<String> x = new ArrayList<>();
        x.add("Andrei");
        x.add("Andrei");
        x.add("Luka");
        x = x.stream().distinct().collect(Collectors.toList());
        System.out.println(x);*/
        //LocalDateTime ld = LocalDateTime.now();
        //System.out.println(ld);
        String x = "2022-05-08 14:00";
        String y = "2022-05-08 16:01";
        String[] yy = y.split(" ")[1].split(":");
        String[] xx = x.split(" ")[1].split(":");
        int min1 = Integer.parseInt(xx[0]) * 60 + Integer.parseInt(xx[1]);
        int min2 = Integer.parseInt(yy[0]) * 60 + Integer.parseInt(yy[1]);
        System.out.println(min2 - min1);
    }
}

