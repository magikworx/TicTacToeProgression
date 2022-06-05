cmake_minimum_required(VERSION 3.22)
project(cpp)

set(CMAKE_CXX_STANDARD 17)

add_executable(cpp main.cpp olcPixelGameEngine.h quick_udp.h)
target_link_libraries(cpp user32 gdi32 opengl32 gdiplus Shlwapi dwmapi stdc++fs Ws2_32)
