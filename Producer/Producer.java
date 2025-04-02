public class Producer {
    public static void main(String[] args) {
        // cli input
        // ex. java Producer 4 videos1 videos2
        if (args.length < 2) {
            System.out.println("Usage: java Producer <nThreads> <dir1> <dir2> ...");
            System.exit(1);
        }
        int numProducers = Integer.parseInt(args[0]);
        if (args.length < numProducers + 1) {
            System.out.println("Provide " + numProducers + " directories for producer threads.");
            System.exit(1);
        }
    }
}