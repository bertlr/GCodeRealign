# contoursolve

This library contains sketchsolve from:
https://code.google.com/archive/p/sketchsolve/

The java native interface (jni) is used to call functions from sketchsolve.

## create the header file (already done of course):

    javah contoursolveinterface.Contour

## Compile the library:
### Windows:
MinGW von http://mingw-w64.org/doku.php/download/win-builds installieren, alles Standardmäßig lassen:
Die PATH Variable auf C:\MinGW64\bin setzen (MinGW Ordner anpassen)
 
    g++ -std=c++11 -Wall -m64 -I"C:\Program Files\Java\jdk1.8.0_121\include"  -I"C:\Program Files\Java\jdk1.8.0_121\include\win32" -fPIC -c ConstraintedValue.cpp Derivatives.cpp LineTo.cpp Polyline.cpp contoursolveinterface_Contour.cpp solve.cpp main.cpp
    g++ -shared -static -static-libgcc -static-libstdc++  -lstdc++ -o contoursolve.dll  ConstraintedValue.o Derivatives.o LineTo.o Polyline.o contoursolveinterface_Contour.o solve.o main.o
    copy /Y contoursolve.dll "..\..\GCodeRealign\release\modules\lib\amd64\windows 10\"
    copy /Y contoursolve.dll "..\..\GCodeRealign\release\modules\lib\amd64\windows 7\"

list the dependents of the .dll:

    "C:\Program Files (x86)\Microsoft Visual Studio 12.0\VC\bin\dumpbin" /dependents contoursolve.dll

### Linux:

    g++ -std=c++11 -m64 -Wall -fPIC  -c ConstraintedValue.cpp Derivatives.cpp LineTo.cpp Polyline.cpp contoursolveinterface_Contour.cpp solve.cpp main.cpp
    g++ -shared  -static-libgcc -static-libstdc++  -o libcontoursolve.so   ConstraintedValue.o Derivatives.o LineTo.o Polyline.o contoursolveinterface_Contour.o solve.o main.o
    cp libcontoursolve.so ../../GCodeRealign/release/modules/lib/amd64/linux


