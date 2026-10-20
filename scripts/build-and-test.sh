#!/usr/bin/env bash
# Build only source files, then exercise gameplay rules and resources from the packaged JAR.
set -euo pipefail
cd "$(dirname "$0")/.."
rm -rf build
mkdir -p build/classes build/test-classes
find src -name '*.java' -print0 | xargs -0 javac --release 11 -cp 'resources/*' -d build/classes
find resources -maxdepth 1 -type f ! -name '*.jar' -exec cp {} build/classes/ \;
for dependency in resources/*.jar; do
  (cd build/classes && jar xf "../../$dependency")
done
find build/classes/META-INF -type f \( -name '*.SF' -o -name '*.RSA' -o -name '*.DSA' \) -delete 2>/dev/null || true
jar --create --file build/Quarantine.jar --manifest src/META-INF/MANIFEST.MF -C build/classes .
# Keep test classes outside the distributable and run with no source-output directory on the classpath.
find tests -name '*.java' -print0 | xargs -0 javac --release 11 -cp build/Quarantine.jar -d build/test-classes
classpath='build/Quarantine.jar:build/test-classes'
case "${OSTYPE:-}" in msys*|cygwin*|win32*) classpath='build/Quarantine.jar;build/test-classes' ;; esac
java -Djava.awt.headless=true -cp "$classpath" QuarantineBehaviorTest
