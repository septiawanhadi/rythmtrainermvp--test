cmake_minimum_required(VERSION 3.22.1)
project("rhythmengine")

# Oboe via Prefab (dependency declared in app/build.gradle.kts)
find_package(oboe REQUIRED CONFIG)

add_library(rhythmengine SHARED
    RhythmEngine.cpp
    MetronomePlayer.cpp
    jni_bridge.cpp
)

target_link_libraries(rhythmengine
    oboe::oboe
    android
    log
)
