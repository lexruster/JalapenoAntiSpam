::keytool -exportcert -alias DebugKey -keystore Debug2.jks -list -v
::"c:\Program Files\Java\jdk1.7.0_45\jre\bin\keytool.exe" -exportcert -keystore Debug2.jks -list -v -storepass android -keypass android
::pause


keytool -exportcert -alias androiddebugkey -keystore C:\Users\Alexander\.android\debug.keystore -list -v
pause