@echo off
"C:\\Program Files\\Android\\Android Studio\\jbr\\bin\\java" ^
  --class-path ^
  "C:\\Users\\USUARIO\\.gradle\\caches\\modules-2\\files-2.1\\com.google.prefab\\cli\\2.1.0\\aa32fec809c44fa531f01dcfb739b5b3304d3050\\cli-2.1.0-all.jar" ^
  com.google.prefab.cli.AppKt ^
  --build-system ^
  cmake ^
  --platform ^
  android ^
  --abi ^
  x86 ^
  --os-version ^
  30 ^
  --stl ^
  c++_static ^
  --ndk-version ^
  27 ^
  --output ^
  "C:\\Users\\USUARIO\\AppData\\Local\\Temp\\agp-prefab-staging14251967965512100499\\staged-cli-output" ^
  "C:\\Users\\USUARIO\\.gradle\\caches\\8.13\\transforms\\c1b5e08c953bde6c4fb0bc6c8ac5b586\\transformed\\games-activity-4.0.0\\prefab"
