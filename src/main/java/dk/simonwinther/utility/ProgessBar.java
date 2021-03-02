package dk.simonwinther.utility;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProgessBar {

    private static final int MAX_BARS = 30;
    private static final int DIVIDE = 100;

    public static String buildProgressBar(int balance, int cost){
        float progress = balance >= cost ? 100 : ((float)balance/cost)*100;
        int paintedBars = Math.round(((float)MAX_BARS/DIVIDE)*progress);
        String color = progress == 100 ? "§a" : progress >= 50 ? "§6" : "§c";
        return IntStream.range(0, paintedBars).mapToObj(num -> color+"|").collect(Collectors.joining())
                +IntStream.range(paintedBars, MAX_BARS).mapToObj(t -> "§8|").collect(Collectors.joining())
                +" §8("+color+progress+"%§8)";
    }

}
