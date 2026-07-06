@echo off
setlocal

set OUTPUT=estructura.txt

echo Generando estructura de directorios...
echo.

(
echo ==========================================
echo ESTRUCTURA DEL DIRECTORIO
echo Fecha: %date% %time%
echo Ruta: %CD%
echo ==========================================
echo.

tree /F /A
) > "%OUTPUT%"

echo.
echo Listo.
echo Archivo generado: %OUTPUT%
pause