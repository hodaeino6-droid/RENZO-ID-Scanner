#!/bin/sh
# Creates your own release keystore. Keep it private and backed up.
keytool -genkeypair -v -keystore renzo-release.jks -alias renzo \
  -keyalg RSA -keysize 2048 -validity 10000
echo "Now copy keystore.properties.example to keystore.properties and fill in the passwords."
