


PROJECT ( sketchsolve CXX )

#FIND_PACKAGE( Boost )
#INCLUDE_DIRECTORIES( ${Boost_INCLUDE_DIR} )
#find_package( Threads )

#INCLUDE_DIRECTORIES( ${xballistics_SOURCE_DIR} )
INCLUDE_DIRECTORIES( ${CMAKE_SOURCE_DIR} )

# damit libstdc++ statisch gelinkt wird, und auf dem Webserver läuft, über das flag "-m32 -L/usr/lib" einstellen, dass für 32bit compiliert werdens soll,
# braucht glibc-devel-static-32bit
SET(CMAKE_CXX_FLAGS ${CMAKE_CXX_FLAGS}  "-g -static  -L/usr/lib")

ADD_EXECUTABLE(sketchsolve
   ConstraintedValue.cpp
    LineTo.cpp
    Derivatives.cpp
   solve.cpp
   Polyline.cpp
   
   main.cpp
   
 )

## Link libraries
#set(BOOST_LIBS thread date_time system)
#find_package(Boost COMPONENTS ${BOOST_LIBS} REQUIRED)
#target_link_libraries(async ${Boost_LIBRARIES})
#find_package(Threads)
#target_link_libraries(serialportMain ${CMAKE_THREAD_LIBS_INIT})

cmake_minimum_required(VERSION 2.8)
