cmake_minimum_required(VERSION 3.22.1)
project("rhythmtrainermvp")

find_package(oboe REQUIRED CONFIG)

add_library(
        rhythmtrainermvp
        SHARED
        audio_engine.cpp)

target_link_libraries(
        rhythmtrainermvp
        oboe::oboe
        log)
