# MouseGlob (Atualizado para Java 21)

Este repositório foi atualizado para compilar e executar com Java 21, sem dependências do Eclipse. Foi adicionado um build Gradle multi-módulo para os projetos:

- Injection (biblioteca interna)
- MouseGlob (aplicação principal)

Os artefatos antigos do Eclipse/Ant (build.xml, .iml, etc.) foram mantidos apenas para referência, mas não são mais necessários no build.

## Pré-requisitos
- JDK 21 instalado (JAVA_HOME apontando para o JDK 21)
- Gradle 8.x instalado (ou use o wrapper do projeto: ./gradlew)

## Compilar

```bash
# Na raiz do repositório
./gradlew build
```

## Executar

```bash
# Executa a aplicação principal
./gradlew :MouseGlob:run
```

Também é possível gerar uma distribuição com scripts de inicialização:

```bash
./gradlew :MouseGlob:installDist
# Scripts ficarão em MouseGlob/build/install/MouseGlob/bin/
# macOS/Linux: MouseGlob/build/install/MouseGlob/bin/MouseGlob
# Windows: MouseGlob/build/install/MouseGlob/bin/MouseGlob.bat
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
- Gradle 8.x instalado (ou use o wrapper do projeto: ./gradlew)

## Compilar

```bash
# Na raiz do repositório
./gradlew build
```

## Executar

```bash
# Executa a aplicação principal
./gradlew :MouseGlob:run
```

Também é possível gerar uma distribuição com scripts de inicialização:

```bash
./gradlew :MouseGlob:installDist
# Scripts ficarão em MouseGlob/build/install/MouseGlob/bin/
# macOS/Linux: MouseGlob/build/install/MouseGlob/bin/MouseGlob
# Windows: MouseGlob/build/install/MouseGlob/bin/MouseGlob.bat
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
