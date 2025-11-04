import java.util.*;

public class Bankers {
    private static final int MAXP = 10; // maximum supported processes

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int totalResources = 0;
            int numProcesses = 0;

            // read and validate total resources
            while (true) {
                System.out.print("Enter the total resources: ");
                if (scanner.hasNextInt()) {
                    totalResources = scanner.nextInt();
                    if (totalResources < 0) {
                        System.out.println("total resources must be non-negative");
                        continue;
                    }
                    break;
                } else {
                    scanner.nextLine();
                    System.out.println("invalid input - please enter a non-negative integer");
                }
            }

            // read and validate number of processes
            while (true) {
                System.out.print("Enter the no. of process: ");
                if (scanner.hasNextInt()) {
                    numProcesses = scanner.nextInt();
                    if (numProcesses < 1 || numProcesses > MAXP) {
                        System.out.println("number of processes must be between 1 and " + MAXP);
                        continue;
                    }
                    break;
                } else {
                    scanner.nextLine();
                    System.out.println("invalid input - please enter an integer between 1 and " + MAXP);
                }
            }

            scanner.nextLine(); // consume newline after number input

            String[] processIds = new String[MAXP];
            int[] maxNeed = new int[MAXP];
            int[] currentHoldings = new int[MAXP];
            int[] need = new int[MAXP];
            int[] perm = new int[MAXP];

            // read process ids (validate non-empty and unique)
            for (int i = 0; i < numProcesses; ++i) {
                while (true) {
                    System.out.print("Enter process ID for Process" + (i + 1) + ": ");
                    String id = scanner.nextLine().trim();

                    if (id.isEmpty()) {
                        System.out.println("process id cannot be empty");
                        continue;
                    }

                    boolean dup = false;
                    for (int j = 0; j < i; ++j) {
                        if (processIds[j] != null && processIds[j].equals(id)) {
                            dup = true;
                            break;
                        }
                    }
                    if (dup) {
                        System.out.println("process id must be unique");
                        continue;
                    }
                    processIds[i] = id;
                    break;
                }
            }

            // read maximum needs for each process (validate >= 0)
            for (int i = 0; i < numProcesses; ++i) {
                while (true) {
                    System.out.print("Enter maximum need of " + processIds[i] + ": ");
                    if (scanner.hasNextInt()) {
                        maxNeed[i] = scanner.nextInt();
                        scanner.nextLine(); // consume newline
                        if (maxNeed[i] < 0) {
                            System.out.println("maximum need must be non-negative");
                            continue;
                        }
                        break;
                    } else {
                        scanner.nextLine();
                        System.out.println("invalid input - please enter a non-negative integer");
                    }
                }
            }

            // read current holdings for each process (validate 0 <= holding <= maxNeed)
            while (true) {
                boolean ok = true;
                int sumHold = 0;
                for (int i = 0; i < numProcesses; ++i) {
                    while (true) {
                        System.out.print("Enter the currently holding of " + processIds[i] + ": ");
                        if (scanner.hasNextInt()) {
                            currentHoldings[i] = scanner.nextInt();
                            scanner.nextLine(); // consume newline
                            if (currentHoldings[i] < 0) {
                                System.out.println("current holding must be non-negative");
                                continue;
                            }
                            if (currentHoldings[i] > maxNeed[i]) {
                                System.out.println("current holding cannot exceed maximum need (" + maxNeed[i] + ")");
                                continue;
                            }
                            break;
                        } else {
                            scanner.nextLine();
                            System.out.println("invalid input - please enter a non-negative integer");
                        }
                    }
                    sumHold += currentHoldings[i];
                }
                if (sumHold > totalResources) {
                    System.out.println("sum of current holdings (" + sumHold + ") exceeds total resources (" + totalResources + ") - please re-enter holdings");
                    ok = false;
                }
                if (ok) break;
            }

            int sumCurrentHoldings = 0;
            for (int i = 0; i < numProcesses; ++i) {
                need[i] = maxNeed[i] - currentHoldings[i];
                sumCurrentHoldings += currentHoldings[i];
            }

            int initialAvailable = totalResources - sumCurrentHoldings;

            for (int i = 0; i < numProcesses; ++i) perm[i] = i;

            System.out.println("\n Possible combinations of process");

            // generate all permutations and test each ordering
            do {
                int available = initialAvailable;
                boolean safe = true;

                for (int idx = 0; idx < numProcesses; ++idx) {
                    int p = perm[idx];
                    if (need[p] <= available) {
                        available += currentHoldings[p];
                    } else {
                        safe = false;
                        break;
                    }
                }

                // print permutation using provided ids, prefixed by a space
                System.out.print(" ");
                for (int i = 0; i < numProcesses; ++i) {
                    System.out.print(processIds[perm[i]]);
                    if (i + 1 < numProcesses) System.out.print(" ");
                }
                System.out.println(" - " + (safe ? "SAFE" : "UNSAFE"));

            } while (nextPermutation(perm, numProcesses));
        }
    }

    // generate next permutation in lexicographic order
    private static boolean nextPermutation(int[] arr, int n) {
        // find the largest index i such that arr[i] < arr[i + 1]
        int i = n - 2;
        while (i >= 0 && arr[i] >= arr[i + 1]) {
            i--;
        }

        if (i == -1) {
            return false; // no next permutation
        }

        // find the largest index j > i such that arr[i] < arr[j]
        int j = n - 1;
        while (j > i && arr[j] <= arr[i]) {
            j--;
        }

        // swap arr[i] and arr[j]
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;

        // reverse the suffix starting at arr[i + 1]
        reverse(arr, i + 1, n - 1);

        return true;
    }

    // reverse array elements from start to end (inclusive)
    private static void reverse(int[] arr, int start, int end) {
        while (start < end) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
    }
}