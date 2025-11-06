@echo off
echo Probando el endpoint de login...

curl -X POST http://localhost:9090/api/usuarios/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"juanperez@example.com\",\"password\":\"miPasswordSeguro123\"}" ^
  -v

echo.
echo Prueba completada.
