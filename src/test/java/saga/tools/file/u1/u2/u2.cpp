//
//  masyvas - dezutes su indeksais
//
#include <iostream>
using namespace std;

int main()
{
    int n;
    cout << "Iveskite kiek elementu sudarys masyva :";
    cin >> n;
    int  m [n];

    for (int i = 0; i < n; i++)
    {
        cout << "Iveskite " << i + 1 << " elementa: ";
        cin >> m[i];
    }
    int indeksas = 0;
    for (int i = 1; i < n; i++)
    {
        if (m[indeksas] < m[i])
        {
            indeksas = i;
        }
    }
    cout << "Didziausias: " << m[indeksas] << endl;
    cout << "Jo indeksas masyve: " << indeksas << endl;
    
}
