package dk.simonwinther;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProgessBar {

    private static final int MAX_BARS = 30;
    private static final int DIVIDE = 100;
    private final int progress;
    private final int paintedBars;

    public ProgessBar(int progress){
        this.progress = progress;
        this.paintedBars = MAX_BARS /DIVIDE*progress;
    }

    public CharSequence build(){
        return IntStream.range(0, paintedBars).mapToObj(i -> "&a|").collect(Collectors.joining())
                + IntStream.range(paintedBars + 1, MAX_BARS).mapToObj(i -> "&c|").collect(Collectors.joining());
    }

}
