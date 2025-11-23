@echo off
REM Script para gerar executavel Windows (.exe) do MouseGlob
REM Requer: JDK 21 com jpackage (incluido por padrao)

echo ========================================
echo MouseGlob - Build de Executavel Windows
echo ========================================
echo.

REM Verificar se JAVA_HOME esta configurado
if not defined JAVA_HOME (
    echo ERRO: JAVA_HOME nao esta configurado!
    echo Por favor, configure JAVA_HOME apontando para o JDK 21
    exit /b 1
)

echo JAVA_HOME: %JAVA_HOME%
echo.

REM Verificar se jpackage existe
if not exist "%JAVA_HOME%\bin\jpackage.exe" (
    echo ERRO: jpackage nao encontrado no JDK!
    echo Certifique-se de usar JDK 21 ou superior.
    exit /b 1
)

echo [1/3] Limpando builds anteriores...
call gradlew.bat clean
if errorlevel 1 (
    echo ERRO: Falha ao limpar builds anteriores
    exit /b 1
)

echo.
echo [2/3] Compilando projeto...
call gradlew.bat build
if errorlevel 1 (
    echo ERRO: Build falhou!
    exit /b 1
)

echo.
echo [3/3] Gerando instalador Windows com jpackage...
call gradlew.bat jpackage
if errorlevel 1 (
    echo ERRO: Falha ao gerar instalador!
    exit /b 1
)

echo.
echo ========================================
echo Instalador gerado com sucesso!
echo ========================================
echo.
echo Localização: MouseGlob\build\jpackage\
echo.
echo Tipos gerados:
echo   - Instalador MSI: MouseGlob\build\jpackage\MouseGlob-*.msi
echo   - Imagem da aplicacao: MouseGlob\build\jpackage\MouseGlob\
echo.
echo Para instalar, execute o arquivo .msi
echo.

pause
