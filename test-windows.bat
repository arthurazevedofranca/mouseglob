@echo off
REM Script de teste para Windows
REM Este script testa o build e execução do MouseGlob no Windows

echo ========================================
echo MouseGlob - Teste de Compatibilidade Windows
echo ========================================
echo.

REM Verificar se JAVA_HOME está configurado
if not defined JAVA_HOME (
    echo ERRO: JAVA_HOME nao esta configurado!
    echo Por favor, configure JAVA_HOME apontando para o JDK 21
    exit /b 1
)

echo JAVA_HOME: %JAVA_HOME%
echo.

REM Verificar versão do Java
echo Verificando versao do Java...
java -version
echo.

REM Build do projeto
echo ========================================
echo Executando build...
echo ========================================
call gradlew.bat clean build
if errorlevel 1 (
    echo ERRO: Build falhou!
    exit /b 1
)

echo.
echo ========================================
echo Build concluido com sucesso!
echo ========================================
echo.

REM Executar testes
echo ========================================
echo Executando testes...
echo ========================================
call gradlew.bat test
if errorlevel 1 (
    echo AVISO: Alguns testes falharam!
)

echo.
echo ========================================
echo Gerando distribuicao...
echo ========================================
call gradlew.bat :MouseGlob:installDist
if errorlevel 1 (
    echo ERRO: Falha ao gerar distribuicao!
    exit /b 1
)

echo.
echo ========================================
echo Teste concluido!
echo ========================================
echo.
echo Distribuicao gerada em: MouseGlob\build\install\MouseGlob\bin\MouseGlob.bat
echo.
echo Para executar a aplicacao:
echo   MouseGlob\build\install\MouseGlob\bin\MouseGlob.bat
echo.
echo Ou execute diretamente:
echo   gradlew.bat :MouseGlob:run
echo.

pause
