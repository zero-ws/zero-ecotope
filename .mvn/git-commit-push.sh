#!/usr/bin/env bash
# 单独编译
# shellcheck disable=SC2164
cd zero-core-ams
echo "[ END ] Git Push Zero AMS"
git add .
git commit -m "Zero AMS: $1"
git push
cd ..
# shellcheck disable=SC2164
cd zero-energy
echo "[ END ] Git Push Zero Core"
git add .
git commit -m "Zero Core: $1"
git push
cd ..
# shellcheck disable=SC2164
cd zero-plugins-equip
echo "[ END ] Git Push Zero Plugins Equip"
git add .
git commit -m "Zero Plugins Equip: $1"
git push
cd ..
# shellcheck disable=SC2164
cd zero-plugins-extension
echo "[ END ] Git Push Zero Plugins Extension"
git add .
git commit -m "Zero Plugins Extension: $1"
git push
cd ..
# shellcheck disable=SC2164
cd zero-plugins-external
echo "[ END ] Git Push Zero Plugins External"
git add .
git commit -m "Zero Plugins External: $1"
git push
cd ..
echo "[ END ] Git Push Zero Ecotope"
git add .
git commit -m "Zero Ecotope: $1"
git push
echo "[ END ] Git Push Finished Compiled"