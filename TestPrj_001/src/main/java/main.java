import java.io.FileNotFoundException;

public class main {
    public static void main(String[] args) {
        try {
            APITest.APITesting();
            first100PrimeNumbers.findPrimeNumbers();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
