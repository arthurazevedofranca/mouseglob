# MouseGlob (Atualizado para Java 21)

Este repositório foi atualizado para compilar e executar com Java 21, sem dependências do Eclipse. Foi adicionado um build Gradle multi-módulo para os projetos:

- Injection (biblioteca interna)
- MouseGlob (aplicação principal)

Os artefatos antigos do Eclipse/Ant (build.xml, .iml, etc.) foram mantidos apenas para referência, mas não são mais necessários no build.

## Pré-requisitos
- JDK 21 instalado (JAVA_HOME apontando para o JDK 21)
- Gradle 8.x instalado (ou use o wrapper do projeto: `./gradlew` no Linux/Mac ou `gradlew.bat` no Windows)

## Compilar

### Linux / macOS
```bash
# Na raiz do repositório
./gradlew build
```

### Windows
```cmd
REM Na raiz do repositório
gradlew.bat build
```

## Executar

### Linux / macOS
```bash
# Executa a aplicação principal
./gradlew :MouseGlob:run
```

### Windows
```cmd
REM Executa a aplicação principal
gradlew.bat :MouseGlob:run
```

Também é possível gerar uma distribuição com scripts de inicialização:

### Linux / macOS
```bash
./gradlew :MouseGlob:installDist
# Scripts ficarão em MouseGlob/build/install/MouseGlob/bin/MouseGlob
```

### Windows
```cmd
gradlew.bat :MouseGlob:installDist
REM Scripts ficarão em MouseGlob\build\install\MouseGlob\bin\MouseGlob.bat
```

## Estrutura
- settings.gradle: define os módulos Injection e MouseGlob
- build.gradle (raiz): configura Java 21 via toolchain
- Injection/build.gradle: configura a biblioteca Injection usando o diretório de código legado `Injection/src`
- MouseGlob/build.gradle: configura a aplicação principal usando `MouseGlob/src` e `MouseGlob/src/resource` e dependências do Maven Central

## Dependências (Maven Central)
- processing core: org.processing:core:3.3.7
- JavaCV (FFmpeg/OpenCV): org.bytedeco:javacv-platform:1.5.10

As dependências locais (Processing video, gstreamer-java, jna, jlfgr) foram removidas do classpath ativo e substituídas por dependências publicadas, reduzindo atrito e melhorando a reprodutibilidade.

## Stack de vídeo
- Reprodução de vídeos via FFmpeg (JavaCV/FFmpegFrameGrabber).
- Câmera reativada usando OpenCV (JavaCV/OpenCVFrameGrabber), com fila limitada e descarte de frames para evitar stutter. Por padrão, o dispositivo 0 é aberto em 640x480. É possível sondar dispositivos com `CameraManager.probeDevices()` e iniciar com índice e resolução específicos via `CameraManager.start(applet, deviceIndex, width, height)`.

## Observações
- Alguns arquivos de exemplo/teste antigos (CompareApplet.java, HoughTest.java, OtsuTest.java, ForkJoinTest.java) foram excluídos da compilação por padrão para evitar conflitos com o JDK 21. Isso não afeta a aplicação principal `dcc.mouseglob.MouseGlob`.
- Se necessário, você pode reabilitar esses arquivos removendo as entradas `exclude` em `MouseGlob/build.gradle`.

# MouseGlob (Atualizado para Java 21)

Este repositório foi atualizado para compilar e executar com Java 21, sem dependências do Eclipse. Foi adicionado um build Gradle multi-módulo para os projetos:

- Injection (biblioteca interna)
- MouseGlob (aplicação principal)

Os artefatos antigos do Eclipse/Ant (build.xml, .iml, etc.) foram mantidos apenas para referência, mas não são mais necessários no build.

## Pré-requisitos
- JDK 21 instalado (JAVA_HOME apontando para o JDK 21)
- Gradle 8.x instalado (ou use o wrapper do projeto: `./gradlew` no Linux/Mac ou `gradlew.bat` no Windows)

## Compilar

### Linux / macOS
```bash
# Na raiz do repositório
./gradlew build
```

### Windows
```cmd
REM Na raiz do repositório
gradlew.bat build
```

## Executar

### Linux / macOS
```bash
# Executa a aplicação principal
./gradlew :MouseGlob:run
```

### Windows
```cmd
REM Executa a aplicação principal
gradlew.bat :MouseGlob:run
```

Também é possível gerar uma distribuição com scripts de inicialização:

### Linux / macOS
```bash
./gradlew :MouseGlob:installDist
# Scripts ficarão em MouseGlob/build/install/MouseGlob/bin/MouseGlob
```

### Windows
```cmd
gradlew.bat :MouseGlob:installDist
REM Scripts ficarão em MouseGlob\build\install\MouseGlob\bin\MouseGlob.bat
```

## Estrutura
- settings.gradle: define os módulos Injection e MouseGlob
- build.gradle (raiz): configura Java 21 via toolchain
- Injection/build.gradle: configura a biblioteca Injection usando o diretório de código legado `Injection/src`
- MouseGlob/build.gradle: configura a aplicação principal usando `MouseGlob/src` e `MouseGlob/src/resource` e dependências do Maven Central

## Dependências (Maven Central)
- processing core: org.processing:core:3.3.7
- JavaCV (FFmpeg/OpenCV): org.bytedeco:javacv-platform:1.5.10

As dependências locais (Processing video, gstreamer-java, jna, jlfgr) foram removidas do classpath ativo e substituídas por dependências publicadas, reduzindo atrito e melhorando a reprodutibilidade.

## Stack de vídeo
- Reprodução de vídeos via FFmpeg (JavaCV/FFmpegFrameGrabber).
- Câmera reativada usando OpenCV (JavaCV/OpenCVFrameGrabber), com fila limitada e descarte de frames para evitar stutter. Por padrão, o dispositivo 0 é aberto em 640x480. É possível sondar dispositivos com `CameraManager.probeDevices()` e iniciar com índice e resolução específicos via `CameraManager.start(applet, deviceIndex, width, height)`.

## Pipeline de rastreamento (novo)
- Foi introduzida uma camada modular de processamento de frames em `dcc.mouseglob.tracking.pipeline`, com estágios encadeáveis:
  - GrayscaleStage: conversão para escala de cinza.
  - BackgroundSubtractStage: subtração de fundo com média móvel exponencial (alpha configurável) ou modo estático.
  - AdaptiveThresholdStage: limiarização global ou adaptativa (média local + constante C), com suporte a objetos claros/escuros.
  - MorphologyStage: operações morfológicas (open/close/erode/dilate) com kernel 3x3/5x5/7x7.
- A configuração é feita por JSON (ex.: `MouseGlob/src/resource/pipelines/default.json`). Exemplo:

```json
{
  "stages": [
    { "type": "grayscale" },
    { "type": "background", "mode": "running", "alpha": 0.02 },
    { "type": "adaptiveThreshold", "mode": "adaptiveMean", "dark": false, "blockSize": 15, "c": 5 },
    { "type": "morphology", "operation": "open", "kernel": "3x3" }
  ]
}
```

- Propriedades (PropertiesManager):
  - `tracking.pipeline.enabled` (default: `true`)
  - `tracking.pipeline.file` (default: `/resource/pipelines/default.json`)

Se a pipeline estiver ativa, o `TrackingManager` usa o resultado (`mask`) da pipeline; caso contrário, mantém o caminho legado (cinza → diferença opcional → threshold fixo claro/escuro).

## Persistência e formatos (novo)
- JSON + JSON Schema:
  - Adicionados esquemas JSON em `MouseGlob/src/resource/schemas/`:
    - `trajectory.schema.json`: NDJSON para exportar trajetórias (1º objeto é metadados, depois 1 linha por frame/medida).
    - `experiment.schema.json`: metadados básicos de experimento (arquivo de vídeo, trajetórias, calibração, etc.).
  - Utilitário de exportação de trajetórias para NDJSON: `dcc.mouseglob.trajectory.TrajectoriesJSON.exportNdjson(...)`.
- Relatórios em CSV/SVG:
  - NOVO menu no relatório (botão direito): "Export CSV..." para salvar as séries do gráfico em CSV; "Save As SVG..." para exportar em SVG (usa PNG embutido para compatibilidade).
  - Implementação em `dcc.mouseglob.report.ReportExportUtil` e `dcc.mouseglob.report.AppletReport`.
- Parquet: planejado. A exportação foi prototipada, mas removida do build por exigir dependências Hadoop pesadas. Pode ser habilitada futuramente.

## Testes de processamento/tracking
- Foi adicionado um smoke test simples em `MouseGlob/src/test/java/dcc/mouseglob/tracking/pipeline/PipelineTests.java` (sem dependências externas), que gera frames sintéticos com ruído, variação de iluminação e oclusão parcial para validar a robustez da pipeline. 
- Para executar manualmente:

```bash
# Executa a classe de teste (ex.: via Gradle Application ou sua IDE)
# Classe: dcc.mouseglob.tracking.pipeline.PipelineTests (método main)
```

## Observações
- Alguns arquivos de exemplo/teste antigos (CompareApplet.java, HoughTest.java, OtsuTest.java, ForkJoinTest.java) foram excluídos da compilação por padrão para evitar conflitos com o JDK 21. Isso não afeta a aplicação principal `dcc.mouseglob.MouseGlob`.
- Se necessário, você pode reabilitar esses arquivos removendo as entradas `exclude` em `MouseGlob/build.gradle`.


## Plugins de Análises (ServiceLoader)
- As análises (implementações de `dcc.mouseglob.analysis.Analysis`) agora podem ser descobertas por `ServiceLoader` via o SPI `dcc.mouseglob.analysis.spi.AnalysisProvider`.
- Como usar em um plugin externo:
  1) Crie uma classe que implemente `AnalysisProvider` e retorne as classes de `Analysis` fornecidas pelo seu plugin.
  2) Registre o provider adicionando o arquivo de recursos `META-INF/services/dcc.mouseglob.analysis.spi.AnalysisProvider` contendo o FQN da sua classe provider.
  3) Opcional: anote suas classes de `Analysis` com `@AnalysisInfo` para nome/descrição na UI.
- O projeto inclui um provider padrão (`dcc.mouseglob.analysis.spi.DefaultAnalysesProvider`) para as análises internas. Se nenhum provider for encontrado no classpath, há fallback para a descoberta antiga por varredura de classes.

## Compatibilidade com Windows

O projeto foi atualizado para suportar totalmente o Windows. As seguintes alterações foram implementadas:

### Correções de Compatibilidade
- ✅ Caminhos de arquivos agora usam APIs multiplataforma (`File(parent, child)` ou `Path.resolve()`)
- ✅ Mensagens de erro usam caminhos dinâmicos baseados em `System.getProperty("user.home")`
- ✅ Gradle wrapper (`gradlew.bat`) incluído para builds no Windows
- ✅ Scripts de distribuição `.bat` gerados automaticamente pelo Gradle

### Testando no Windows

#### Opção 1: Windows Nativo
1. Instale o [JDK 21 para Windows](https://adoptium.net/)
2. Configure a variável de ambiente `JAVA_HOME` apontando para o JDK 21
3. Clone o repositório
4. Execute: `gradlew.bat build`
5. Execute: `gradlew.bat :MouseGlob:run`

#### Opção 2: WSL (Windows Subsystem for Linux)
WSL permite executar um ambiente Linux dentro do Windows:

```powershell
# No PowerShell (como administrador)
wsl --install
```

Após reiniciar, no terminal WSL:
```bash
# Instalar JDK 21
sudo apt update
sudo apt install openjdk-21-jdk

# Clonar e executar o projeto
git clone <url-do-repositorio>
cd mouseglob
./gradlew build
./gradlew :MouseGlob:run
```

#### Opção 3: Docker no Windows
Use Docker Desktop para Windows:

```dockerfile
# Criar arquivo Dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN ./gradlew build

CMD ["./gradlew", ":MouseGlob:run"]
```

```powershell
# Build e execução
docker build -t mouseglob .
docker run -it mouseglob
```

### Observações para Windows
- Os caminhos de configuração ficam em `%USERPROFILE%\.mouseglob\`
- Logs ficam em `%USERPROFILE%\.mouseglob\logs\`
- Use `\` (barra invertida) ou `/` (barra) nos caminhos - Java aceita ambos no Windows
- Para GUI, certifique-se de ter suporte a X11 (necessário no WSL/Docker)

### Gerando Executável Windows (.exe)

O projeto suporta geração de instalador nativo Windows que **não requer Java instalado** no sistema do usuário.

**⚠️ IMPORTANTE:** O jpackage só gera instaladores nativos para o SO em que está rodando. Para gerar `.exe`/`.msi` para Windows, você precisa rodar em uma máquina Windows.

#### Opção A: Gerar automaticamente via GitHub Actions (Recomendado - funciona de qualquer OS)

1. Faça push do código para o GitHub
2. Vá em: **Actions** → **Build Windows Executable** → **Run workflow**
3. Aguarde ~5-10 minutos
4. Baixe os arquivos gerados em **Artifacts**:
   - `MouseGlob-Windows-Installer.zip` (contém o `.msi`)
   - `MouseGlob-Windows-Standalone.zip` (contém o `.exe`)

**Você pode fazer isso do Mac, Linux ou qualquer lugar!**

#### Opção B: Gerar localmente em uma máquina Windows

**Passo 1:** Instalar WiX Toolset (apenas na primeira vez)
1. Baixe o [WiX Toolset v3.x](https://github.com/wixtoolset/wix3/releases)
2. Instale e adicione ao PATH do sistema

**Passo 2:** Gerar o instalador
```cmd
REM Opção 1: Usar script automatizado
build-windows-exe.bat

REM Opção 2: Comando manual
gradlew.bat jpackage
```

#### O que é gerado:
- **Instalador MSI**: `MouseGlob\build\jpackage\MouseGlob-2.0.1.msi`
  - Instala a aplicação no sistema
  - Cria atalho no menu iniciar
  - Inclui JRE embutido (não precisa de Java instalado)
  - ~200-300 MB (inclui todas as dependências)

- **Imagem standalone**: `MouseGlob\build\jpackage\MouseGlob\bin\MouseGlob.exe`
  - Executável direto sem instalação
  - Pode ser copiado para outro computador Windows
  - Também inclui JRE embutido

#### Distribuindo para outros usuários:
1. Compartilhe o arquivo `.msi`
2. Usuário executa o `.msi` e instala normalmente
3. Aplicação aparece no Menu Iniciar
4. **Não é necessário ter Java instalado!**

#### Tamanho do instalador:
- Com JRE embutido: ~250-350 MB
- Sem JRE (requer Java no sistema): adicione `--no-runtime` nas `installerOptions`
