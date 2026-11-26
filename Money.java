import java.text.NumberFormat;
import java.util.Locale;
public final class Money {
    private static final NumberFormat FORMAT = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));
    private Money() { }
    public static synchronized String format(double value) { return FORMAT.format(value); }
}
