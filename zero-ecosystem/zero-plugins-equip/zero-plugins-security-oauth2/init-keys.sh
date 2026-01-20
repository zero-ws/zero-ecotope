# 1. 确保目录存在
mkdir -p src/main/resources/keys

# 2. 生成密钥对 (已补充 -dname 参数)
keytool -genkeypair \
  -alias zero-jwt-key \
  -keyalg RSA \
  -keysize 2048 \
  -keystore src/main/resources/keys/oauth2.jks \
  -validity 3650 \
  -storepass changeit \
  -dname "CN=Zero Auth Server, OU=ZeroWS, O=ZeroWS, L=ChongQing, ST=ChongQing, C=CN"