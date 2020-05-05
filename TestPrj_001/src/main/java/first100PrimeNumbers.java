import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class first100PrimeNumbers {

    public static void findPrimeNumbers() {
        int i = 1; // Number from where need to start finding prime numbers
        int j = i; //Temp variable
        int maxCheck = 100; // Count of prime number to find from 1
        boolean isPrime;

        //Empty String to print numbers
        String primeNumbersFound = "";
        //Array to store numbers
        List<Integer> primeNumbers = new ArrayList<Integer>();
        //Start loop 1 to maxCheck

        if (i > 0 && maxCheck > 0) {
            do {
                isPrime = CheckPrime(i);
                if (isPrime) {
                    primeNumbersFound = primeNumbersFound + i + " ";
                    primeNumbers.add(i);
                }
                i++;
            } while (primeNumbers.size() < maxCheck);
            System.out.println("Count of prime numbers found: " + primeNumbers.size());
            System.out.println("Prime numbers from " + j + " to " + (i - 1) + " are:");
            // Print prime numbers from i to maxCheck
            System.out.println(primeNumbersFound);
        } else {
            if (i == 0 || maxCheck == 0) {
                System.out.println("Number should be greater than 0");
            } else {
                System.out.println("Invalid number input: " + j + " or " + maxCheck);
            }
        }
    }

        static boolean CheckPrime ( int numberToCheck){
            int remainder;
            for (int i = 2; i <= numberToCheck / 2; i++) {
                remainder = numberToCheck % i;
                //if remainder is 0 then numberToCheck is not prime and break loop else continue loop
                if (remainder == 0) {
                    return false;
                }
            }
            return true;

        }

}
