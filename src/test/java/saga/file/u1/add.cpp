
// add.cpp
#include <iostream>
using namespace std;
int main ()
{
    int a;
    int b;
    cout << "Iveskite a: ";
    cin  >> a;
    cout << "Iveskite b: ";
    cin  >> b;
    int r1 = (a - b - 12) * 2 ;
    cout << " ( " << a << " - " << b << " - 12 ) * 2"  << " = " << r1 <<"\n";
    int c;
    cout << "Iveskite c: ";
    cin >> c;
    int r2 = (r1 - c - 13) * 2 ;
    cout << " ( " << r1 << " - "<< c << " - 13 ) * 2 "<< " = " << r2 <<"\n";
}
