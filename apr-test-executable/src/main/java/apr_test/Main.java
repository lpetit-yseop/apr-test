package apr_test;

/*
 * Start in headless mode:
 * java -cp apr-test-executable.jar -Djava.awt.headless=true apr_test.Main 
 */
public class Main {
    public static void main(String[] args) {
        final long sleepTime = 20 * 1000;
        System.out.println("start");
        final long startTime = System.currentTimeMillis(); 
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            final long stopTime = System.currentTimeMillis();
            System.err.println("Error, slept only " + (stopTime - startTime) + " ms.");
        };
        System.out.println("stop");
    }
}
