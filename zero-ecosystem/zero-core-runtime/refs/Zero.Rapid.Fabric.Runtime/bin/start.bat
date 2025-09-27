chcp 65001
set ZERO_HOME=%cd%
java -cp .;"%ZERO_HOME%/conf" -Dfelix.config.properties="file:///%ZERO_HOME%/conf/config.properties" -jar "%ZERO_HOME%\libs\felix.jar"