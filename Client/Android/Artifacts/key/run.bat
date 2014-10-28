::keytool -exportcert -alias DebugKey -keystore Debug2.jks -list -v
keytool -exportcert -keystore Debug2.jks -list -v -storepass android -keypass android
pause