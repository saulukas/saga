//ivedame sveikuju skaiciu masys. Suformuojame antra masyva, kuriame bus pirmojo masyvo elemetai 
//atvirkscia tvarka (nepainioti su rusiavimu mazėjancia tvarka). 
// 
//
#include <iostream>
#include <iomanip>
#include <vector>
using namespace std;

int main ()
{
    vector<int>  m, n;

    while (true)
    {
        int i;
        cout << "Iveskite skaiciu (0-baigti): ";
        cin >> i;
        if (i == 0)
            break;
        m.push_back(i);
    }

    for (int i = m.size(); i >= 0 ; i--)
        n.push_back(m[i]);

    cout<<("Atvirkscias masyvas: ")<< endl;
    for (int i = 0; i < n.size(); i++)
        cout<< setw(3) << i <<".    "<< n[i] << endl;    
}

