#include <iostream>
#include <algorithm>
#include <string>

using namespace std;

int main() {
    const int MAXP = 10; // maximum supported processes

    int totalResources; // total number of resources in the system
    int numProcesses; // total number of processes (user input)

    // read and validate total resources
    while (true) {
        cout << "Enter the total resources: ";
        if (!(cin >> totalResources)) {
            cin.clear();
            string dummy;
            getline(cin, dummy);
            cout << "invalid input - please enter a non-negative integer\n";
            continue;
        }
        if (totalResources < 0) {
            cout << "total resources must be non-negative\n";
            continue;
        }
        break;
    }

    // read and validate number of processes
    while (true) {
        cout << "Enter the no. of process: ";
        if (!(cin >> numProcesses)) {
            cin.clear();
            string dummy;
            getline(cin, dummy);
            cout << "invalid input - please enter an integer between 1 and " << MAXP << "\n";
            continue;
        }
        if (numProcesses < 3 || numProcesses > MAXP) {
            cout << "number of processes must be between 3 and " << MAXP << "\n";
            continue;
        }
        break;
    }

    string processIds[MAXP];
    int maxNeed[MAXP];
    int currentHoldings[MAXP];
    int need[MAXP];
    int perm[MAXP];

    // read process ids (validate non-empty and unique)
    for (int i = 0; i < numProcesses; ++i) {
        while (true) {
            cout << "Enter process ID for Process" << (i + 1) << ": ";
            if (!(cin >> processIds[i])) {
                cin.clear();
                string dummy;
                getline(cin, dummy);
                cout << "invalid input - please enter a non-empty id\n";
                continue;
            }
            if (processIds[i].empty()) {
                cout << "process id cannot be empty\n";
                continue;
            }
            bool dup = false;
            for (int j = 0; j < i; ++j) if (processIds[j] == processIds[i]) { dup = true; break; }
            if (dup) {
                cout << "process id must be unique\n";
                continue;
            }
            break;
        }
    }

    // read maximum needs for each process (validate >= 0)
    for (int i = 0; i < numProcesses; ++i) {
        while (true) {
            cout << "Enter maximum need of " << processIds[i] << ": ";
            if (!(cin >> maxNeed[i])) {
                cin.clear();
                string dummy;
                getline(cin, dummy);
                cout << "invalid input - please enter a non-negative integer\n";
                continue;
            }
            if (maxNeed[i] < 0) {
                cout << "maximum need must be non-negative\n";
                continue;
            }
            break;
        }
    }

    // read current holdings for each process (validate 0 <= holding <= maxNeed)
    while (true) {
        bool ok = true;
        int sumHold = 0;
        for (int i = 0; i < numProcesses; ++i) {
            while (true) {
                cout << "Enter the currently holding of " << processIds[i] << ": ";
                if (!(cin >> currentHoldings[i])) {
                    cin.clear();
                    string dummy;
                    getline(cin, dummy);
                    cout << "invalid input - please enter a non-negative integer\n";
                    continue;
                }
                if (currentHoldings[i] < 0) {
                    cout << "current holding must be non-negative\n";
                    continue;
                }
                if (currentHoldings[i] > maxNeed[i]) {
                    cout << "current holding cannot exceed maximum need (" << maxNeed[i] << ")\n";
                    continue;
                }
                break;
            }
            sumHold += currentHoldings[i];
        }
        if (sumHold > totalResources) {
            cout << "sum of current holdings (" << sumHold << ") exceeds total resources (" << totalResources << ") - please re-enter holdings\n";
            ok = false;
        }
        if (ok) break;
        // otherwise loop to re-enter holdings
    }

    int sumCurrentHoldings = 0;
    for (int i = 0; i < numProcesses; ++i) {
        need[i] = maxNeed[i] - currentHoldings[i];
        sumCurrentHoldings += currentHoldings[i];
    }

    int initialAvailable = totalResources - sumCurrentHoldings;

    for (int i = 0; i < numProcesses; ++i) perm[i] = i;

    cout << endl << "Possible combinations of process" << endl << endl;

    do {
        int available = initialAvailable;
        bool safe = true;

        for (int idx = 0; idx < numProcesses; ++idx) {
            int p = perm[idx];
            if (need[p] <= available) {
                available += currentHoldings[p];
            } else {
                safe = false;
                break;
            }
        }

        // print permutation
        cout << " ";
        for (int i = 0; i < numProcesses; ++i) {
            cout << processIds[perm[i]];
            if (i + 1 < numProcesses) cout << " ";
        }
        cout << " - " << (safe ? "SAFE" : "UNSAFE") << '\n';

    } while (next_permutation(perm, perm + numProcesses));

    return 0;
}