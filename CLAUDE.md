# CLAUDE.md - Documentação Técnica para Sustentação e Evolução

> **Audiência**: Desenvolvedores, mantenedores, contribuidores e IA assistentes (Claude)
>
> **Propósito**: Fornecer conhecimento técnico completo para manutenção, debugging, extensão e evolução do projeto MouseGlob

## 📑 Índice

1. [Arquitetura Técnica](#1-arquitetura-técnica)
2. [Estrutura de Código](#2-estrutura-de-código)
3. [Pipeline de Processamento](#3-pipeline-de-processamento)
4. [Sistema de Injeção de Dependências](#4-sistema-de-injeção-de-dependências)
5. [Sistema de Eventos](#5-sistema-de-eventos)
6. [Formatos de Dados](#6-formatos-de-dados)
7. [Guias de Desenvolvimento](#7-guias-de-desenvolvimento)
8. [Debugging e Troubleshooting](#8-debugging-e-troubleshooting)
9. [Performance e Otimização](#9-performance-e-otimização)
10. [Roadmap Técnico](#10-roadmap-técnico)
11. [Decisões Arquiteturais](#11-decisões-arquiteturais)

---

## 1. Arquitetura Técnica

### 1.1 Visão Geral da Arquitetura

MouseGlob segue uma arquitetura em **camadas modulares** com comunicação baseada em **eventos**:

```
┌─────────────────────────────────────────────────────────────┐
│                    UI Layer (Swing)                         │
│  MouseGlobUI, MenuBar, Toolbars, Panels                     │
│  - MouseGlobUI: janela principal e composição de painéis    │
│  - MazeUI: edição de zonas e limites                        │
│  - TrackingUI: configuração de rastreamento                 │
│  - AnalysesView: seleção e gerenciamento de análises        │
├─────────────────────────────────────────────────────────────┤
│              Rendering Layer (Processing)                   │
│  MouseGlobApplet (extends PApplet)                          │
│  - Renderização 2D de vídeo, zonas, trajetórias             │
│  - Interface Paintable para componentes visuais             │
│  - Captura de eventos de mouse/teclado                      │
├─────────────────────────────────────────────────────────────┤
│           Business Logic / Controllers                      │
│  - MovieManager: reprodução de vídeo e distribuição frames  │
│  - CameraManager: captura de câmera                         │
│  - TrackingManager: orquestração de rastreamento            │
│  - AnalysesManager: gerenciamento de análises               │
│  - VisitEventManager: detecção de eventos em zonas          │
├─────────────────────────────────────────────────────────────┤
│              Domain Models / Events                         │
│  - Tracker, Experiment, Maze, Zone, Calibration             │
│  - TrackingEvent, VisitEvent, MovieEvent                    │
│  - Listeners: NewFrameListener, TrackingListener, etc.      │
├─────────────────────────────────────────────────────────────┤
│                I/O & Persistence                            │
│  - XML Codecs: ExperimentXML, MazeXML, TrackingXML          │
│  - JSON: TrajectoriesJSON (NDJSON), JsonPipelineLoader      │
│  - Export: CSV, SVG, PNG (ReportExportUtil)                 │
├─────────────────────────────────────────────────────────────┤
│              Infrastructure & Utilities                     │
│  - Injection (DI): Context, Injector, @Inject               │
│  - Graphics: Image processing, plotting, pools              │
│  - Module: AbstractModule<M,V,C>, Controller, View          │
│  - Logging: SLF4J + Logback                                 │
│  - Properties: PropertiesManager                            │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Módulos Gradle

O projeto é dividido em 2 módulos Gradle (definidos em `settings.gradle`):

#### **Injection** (biblioteca)
- **Localização**: `/Injection/`
- **Responsabilidade**: Framework de injeção de dependências customizado
- **Pacotes principais**:
  - `dcc.inject.*`: Core do DI (Context, Injector, DependencyGraph)
  - `dcc.module.*`: Padrão Module-View-Controller
  - `dcc.event.*`: Sistema de eventos observáveis
  - `dcc.tree.*`: Estrutura de árvore para hierarquia de componentes
  - `dcc.ui.*`: Componentes UI genéricos
  - `dcc.graphics.*`: Renderização, processamento de imagem, pools
  - `dcc.xml.*`: Codificação/decodificação XML

#### **MouseGlob** (aplicação)
- **Localização**: `/MouseGlob/`
- **Responsabilidade**: Aplicação principal de análise comportamental
- **Pacotes principais**: Ver seção 2.2

### 1.3 Fluxo de Execução Principal

```
main() [MouseGlob.java]
  │
  ├─> PropertiesManager.loadProperties()
  │   └─> Carrega ~/.mouseglob/mouseglob.properties
  │
  ├─> Context.getInstance().indexClasses(classes.txt)
  │   └─> Descobre e indexa todas as classes para DI
  │
  ├─> Context.getInstance().inject(MouseGlob.class)
  │   └─> Resolve dependências e cria instâncias
  │
  ├─> new MouseGlobUI()
  │   ├─> Cria MenuBar, Toolbars
  │   ├─> Cria MouseGlobApplet (PApplet)
  │   ├─> Cria painéis: MovieUI, MazeUI, TrackingUI, etc.
  │   └─> Conecta listeners
  │
  └─> Aguarda interação do usuário
      │
      ├─> Abrir vídeo
      │   └─> MovieManager.open(file)
      │       └─> Inicia thread de reprodução
      │           └─> Loop: dispara NewFrameEvent
      │
      ├─> Iniciar rastreamento
      │   └─> TrackingManager.start()
      │       └─> Escuta NewFrameEvent
      │           ├─> Processa frame via Pipeline
      │           ├─> Detecta objetos
      │           └─> Dispara TrackingEvent
      │
      └─> Exportar dados
          └─> TrajectoriesJSON.exportNdjson()
```

### 1.4 Padrões de Design Utilizados

| Padrão | Uso | Localização |
|--------|-----|-------------|
| **Dependency Injection** | Wiring de componentes, inversão de controle | `dcc.inject.*` |
| **Observer/Listener** | Comunicação via eventos (frames, tracking, seleção) | `*Listener.java`, `*Event.java` |
| **Pipeline** | Processamento modular de frames (estágios encadeáveis) | `dcc.mouseglob.tracking.pipeline.*` |
| **Service Provider Interface (SPI)** | Descoberta de plugins de análises | `dcc.mouseglob.analysis.spi.*` |
| **Module-View-Controller** | Separação UI/lógica/modelo | `dcc.module.*` |
| **Object Pool** | Reutilização de buffers (matrizes, arrays) | `dcc.graphics.pool.*` |
| **Template Method** | Análises com estrutura comum | `AbstractAnalysis.java` |
| **Factory** | Criação de instâncias via DI | `dcc.inject.Context` |
| **Codec** | Serialização/desserialização XML | `*XMLEncoder.java`, `*XMLDecoder.java` |

---

## 2. Estrutura de Código

### 2.1 Árvore de Diretórios

```
mouseglob/
├── Injection/                      # Módulo de infraestrutura
│   ├── src/
│   │   ├── dcc/inject/             # DI framework
│   │   ├── dcc/module/             # Module-View-Controller
│   │   ├── dcc/event/              # Sistema de eventos
│   │   ├── dcc/tree/               # Estrutura de árvore
│   │   ├── dcc/ui/                 # Componentes UI genéricos
│   │   ├── dcc/graphics/           # Graphics, pools, plotting
│   │   ├── dcc/xml/                # XML codecs
│   │   └── dcc/identifiable/       # Interface Identifiable
│   └── build.gradle
│
├── MouseGlob/                      # Módulo principal
│   ├── src/
│   │   ├── dcc/mouseglob/
│   │   │   ├── MouseGlob.java           # Main class
│   │   │   ├── MouseGlobUI.java         # UI principal
│   │   │   ├── MouseGlobApplet.java     # Processing applet
│   │   │   │
│   │   │   ├── analysis/                # Análises comportamentais
│   │   │   │   ├── Analysis.java
│   │   │   │   ├── AbstractAnalysis.java
│   │   │   │   ├── AnalysisInfo.java     # Anotação
│   │   │   │   ├── AnalysesManager.java
│   │   │   │   ├── spi/                  # Plugin SPI
│   │   │   │   │   ├── AnalysisProvider.java
│   │   │   │   │   └── DefaultAnalysesProvider.java
│   │   │   │   └── [23+ análises concretas]
│   │   │   │
│   │   │   ├── applet/                  # Rendering Processing
│   │   │   │   ├── Paintable.java        # Interface de renderização
│   │   │   │   └── [painters]
│   │   │   │
│   │   │   ├── calibration/             # Calibração espacial
│   │   │   │   ├── Calibration.java      # Modelo
│   │   │   │   ├── CalibrationController.java
│   │   │   │   └── CalibrationView.java
│   │   │   │
│   │   │   ├── camera/                  # Captura de câmera
│   │   │   │   └── CameraManager.java
│   │   │   │
│   │   │   ├── cli/                     # CLI headless
│   │   │   │   ├── MouseGlobCLI.java
│   │   │   │   └── BatchProcessor.java
│   │   │   │
│   │   │   ├── experiment/              # Experimentos
│   │   │   │   ├── Experiment.java
│   │   │   │   ├── ExperimentXML*.java   # Codecs XML
│   │   │   │   └── ExperimentIOView.java
│   │   │   │
│   │   │   ├── inspector/               # Inspeção de objetos
│   │   │   │   └── InspectorUI.java
│   │   │   │
│   │   │   ├── keyevent/                # Mapeamento de teclas
│   │   │   │   └── KeyEventManager.java
│   │   │   │
│   │   │   ├── labelable/               # Objetos rotulados
│   │   │   │   └── Labelable.java
│   │   │   │
│   │   │   ├── maze/                    # Labirinto e zonas
│   │   │   │   ├── Maze.java
│   │   │   │   ├── Zone.java
│   │   │   │   ├── MazeController.java
│   │   │   │   ├── MazeUI.java
│   │   │   │   └── io/                   # Import/Export
│   │   │   │
│   │   │   ├── movie/                   # Reprodução de vídeo
│   │   │   │   ├── MovieManager.java     # Core: reprodução
│   │   │   │   ├── MovieListener.java    # Eventos de player
│   │   │   │   ├── MovieController.java
│   │   │   │   └── MovieUI.java
│   │   │   │
│   │   │   ├── report/                  # Relatórios e gráficos
│   │   │   │   ├── AppletReport.java
│   │   │   │   ├── ReportExportUtil.java # CSV/SVG export
│   │   │   │   └── [tipos de relatório]
│   │   │   │
│   │   │   ├── shape/                   # Desenho de formas
│   │   │   │   ├── Circle.java
│   │   │   │   └── Polygon.java
│   │   │   │
│   │   │   ├── tracking/                # Rastreamento
│   │   │   │   ├── Tracker.java
│   │   │   │   ├── TrackingManager.java  # Core: rastreamento
│   │   │   │   ├── TrackingEvent.java
│   │   │   │   ├── TrackingListener.java
│   │   │   │   ├── TrackingController.java
│   │   │   │   ├── TrackingUI.java
│   │   │   │   └── pipeline/             # Pipeline modular
│   │   │   │       ├── FramePipeline.java
│   │   │   │       ├── Stage.java
│   │   │   │       ├── JsonPipelineLoader.java
│   │   │   │       └── [estágios concretos]
│   │   │   │
│   │   │   ├── trajectory/              # I/O de trajetórias
│   │   │   │   ├── TrajectoriesJSON.java # NDJSON export
│   │   │   │   └── TrajectoriesIOView.java
│   │   │   │
│   │   │   ├── ui/                      # UI específica
│   │   │   │   └── [componentes]
│   │   │   │
│   │   │   ├── visit/                   # Eventos de visita
│   │   │   │   ├── VisitEvent.java
│   │   │   │   └── VisitEventManager.java
│   │   │   │
│   │   │   └── util/                    # Utilitários
│   │   │       └── PropertiesManager.java
│   │   │
│   │   └── resource/
│   │       ├── pipelines/
│   │       │   └── default.json          # Pipeline padrão
│   │       ├── schemas/
│   │       │   ├── trajectory.schema.json
│   │       │   └── experiment.schema.json
│   │       ├── logback.xml               # Logging config
│   │       ├── META-INF/services/
│   │       │   └── dcc.mouseglob.analysis.spi.AnalysisProvider
│   │       └── [fonts, icons, data]
│   │
│   └── build.gradle
│
├── .github/workflows/
│   └── build-windows.yml               # GitHub Actions CI
│
├── build.gradle                        # Root build config
├── settings.gradle                     # Módulos
├── README.md                           # Docs para usuários
├── CLAUDE.md                           # Este arquivo
├── revision.md                         # Design decisions
└── TODO.txt                            # Roadmap
```

### 2.2 Pacotes Principais e Responsabilidades

| Pacote | LOC | Responsabilidade | Classes-chave |
|--------|-----|------------------|---------------|
| `dcc.mouseglob` | ~500 | Ponto de entrada, orquestração geral | `MouseGlob`, `MouseGlobUI`, `MouseGlobApplet` |
| `dcc.mouseglob.movie` | ~800 | Reprodução de vídeo, distribuição de frames | `MovieManager`, `MovieController` |
| `dcc.mouseglob.camera` | ~300 | Captura de câmera em tempo real | `CameraManager` |
| `dcc.mouseglob.tracking` | ~1200 | Rastreamento e detecção de objetos | `TrackingManager`, `Tracker`, `TrackingEvent` |
| `dcc.mouseglob.tracking.pipeline` | ~600 | Pipeline modular de processamento | `FramePipeline`, `Stage`, `JsonPipelineLoader` |
| `dcc.mouseglob.maze` | ~900 | Definição de zonas e limites | `Maze`, `Zone`, `MazeController` |
| `dcc.mouseglob.analysis` | ~3500 | Análises comportamentais (23+ análises) | `Analysis`, `AnalysesManager`, SPI |
| `dcc.mouseglob.report` | ~1800 | Geração de relatórios e gráficos | `AppletReport`, `ReportExportUtil` |
| `dcc.mouseglob.trajectory` | ~400 | I/O de trajetórias (NDJSON) | `TrajectoriesJSON` |
| `dcc.mouseglob.experiment` | ~600 | Persistência de experimentos (XML) | `Experiment`, `ExperimentXML*` |
| `dcc.mouseglob.calibration` | ~500 | Calibração espacial | `Calibration`, `CalibrationController` |
| `dcc.mouseglob.visit` | ~300 | Detecção de eventos em zonas | `VisitEvent`, `VisitEventManager` |
| `dcc.inject.*` | ~2000 | Framework de injeção de dependências | `Context`, `Injector`, `@Inject` |
| `dcc.graphics.*` | ~4000 | Processamento de imagem, gráficos, pools | `Image`, `BinaryImage`, `Plot` |
| `dcc.module.*` | ~600 | Padrão Module-View-Controller | `AbstractModule`, `Controller`, `View` |

**Total**: ~37.744 linhas de código Java em 221 arquivos

### 2.3 Pontos de Entrada

#### GUI (interface gráfica)
```bash
./gradlew :MouseGlob:run
# ou
java -jar MouseGlob/build/libs/MouseGlob.jar
```
**Classe**: `dcc.mouseglob.MouseGlob.main()`

#### CLI (headless/batch)
```bash
./gradlew :MouseGlob:runCli -- --batch --input videos/ --output results/
```
**Classe**: `dcc.mouseglob.cli.MouseGlobCLI.main()`

#### Testes
```bash
./gradlew test
```
**Classe de teste**: `dcc.mouseglob.tracking.pipeline.PipelineTests.main()`

---

## 3. Pipeline de Processamento

### 3.1 Visão Geral

O pipeline modular permite configurar o processamento de frames via JSON, sem recompilar:

```
Frame RGB → [Stage 1] → [Stage 2] → ... → [Stage N] → Máscara Binária
```

**Localização**: `dcc.mouseglob.tracking.pipeline.*`

### 3.2 Estágios Disponíveis

#### **GrayscaleStage**
Converte imagem RGB para escala de cinza (luminância).

**Config**:
```json
{ "type": "grayscale" }
```

**Implementação**: `Y = 0.299R + 0.587G + 0.114B`

---

#### **BackgroundSubtractStage**
Subtrai fundo para isolar objetos em movimento.

**Modos**:
1. **running**: Fundo atualizado continuamente via EMA (Exponential Moving Average)
   - `background_t = (1 - alpha) * background_{t-1} + alpha * frame_t`
2. **static**: Fundo fixo (primeiro frame ou especificado)

**Config**:
```json
{
  "type": "background",
  "mode": "running",     // ou "static"
  "alpha": 0.02          // taxa de atualização (0.01-0.1)
}
```

**Quando usar**:
- `running`: câmera fixa, iluminação variável
- `static`: câmera fixa, iluminação constante

---

#### **AdaptiveThresholdStage**
Binariza a imagem (preto/branco) baseado em threshold adaptativo ou global.

**Modos**:
1. **adaptiveMean**: Threshold = média local - C
2. **global**: Threshold fixo (valor especificado)

**Config**:
```json
{
  "type": "adaptiveThreshold",
  "mode": "adaptiveMean",  // ou "global"
  "dark": false,           // true: objeto escuro; false: objeto claro
  "blockSize": 15,         // tamanho da janela (ímpar: 11, 15, 21, ...)
  "c": 5,                  // constante de ajuste (-20 a 20)
  "threshold": 128         // apenas para mode="global"
}
```

**Quando usar**:
- `adaptiveMean`: iluminação não-uniforme
- `global`: iluminação uniforme, contraste alto

---

#### **MorphologyStage**
Operações morfológicas para remover ruído e preencher buracos.

**Operações**:
- **erode**: Erosão (reduz objetos claros)
- **dilate**: Dilatação (expande objetos claros)
- **open**: Erosão seguida de dilatação (remove ruído pequeno)
- **close**: Dilatação seguida de erosão (preenche buracos)

**Config**:
```json
{
  "type": "morphology",
  "operation": "open",    // erode, dilate, open, close
  "kernel": "3x3"         // 3x3, 5x5, 7x7
}
```

**Quando usar**:
- `open`: remover ruído/artefatos pequenos
- `close`: preencher buracos dentro de objetos

---

### 3.3 Exemplos de Configuração

#### Objeto claro em fundo escuro (LED, marcador reflexivo)
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

#### Objeto escuro em fundo claro (animal sem marcador)
```json
{
  "stages": [
    { "type": "grayscale" },
    { "type": "background", "mode": "running", "alpha": 0.03 },
    { "type": "adaptiveThreshold", "mode": "adaptiveMean", "dark": true, "blockSize": 21, "c": 10 },
    { "type": "morphology", "operation": "close", "kernel": "5x5" }
  ]
}
```

#### Iluminação constante (sem subtração de fundo)
```json
{
  "stages": [
    { "type": "grayscale" },
    { "type": "adaptiveThreshold", "mode": "global", "dark": false, "threshold": 200 },
    { "type": "morphology", "operation": "open", "kernel": "3x3" }
  ]
}
```

### 3.4 Como Criar um Novo Estágio

1. **Implemente a interface `Stage`**:

```java
package dcc.mouseglob.tracking.pipeline;

import dcc.graphics.image.Image;

public class MeuEstagioCustomizado implements Stage {

    private final int parametro;

    public MeuEstagioCustomizado(int parametro) {
        this.parametro = parametro;
    }

    @Override
    public Image process(Image input) {
        // Processar imagem
        // Retornar nova imagem ou modificar input in-place
        return input;
    }
}
```

2. **Adicione factory no `JsonPipelineLoader`**:

```java
// Em JsonPipelineLoader.java, método loadPipeline()
case "meuEstagioCustomizado":
    int param = node.path("parametro").asInt(10);
    return new MeuEstagioCustomizado(param);
```

3. **Use no JSON**:

```json
{
  "stages": [
    { "type": "grayscale" },
    { "type": "meuEstagioCustomizado", "parametro": 42 }
  ]
}
```

### 3.5 Debugging do Pipeline

**Ativar logs**:
```properties
# Em ~/.mouseglob/mouseglob.properties
logging.level.dcc.mouseglob.tracking.pipeline=DEBUG
```

**Visualizar estágios intermediários**:
```java
// Em PipelineTests.java ou seu código
FramePipeline pipeline = JsonPipelineLoader.load("/resource/pipelines/default.json");
Image frame = /* ... */;
Image result = pipeline.process(frame);

// Salvar intermediários
for (int i = 0; i < pipeline.getStages().size(); i++) {
    Image intermediate = pipeline.getStages().get(i).process(frame);
    intermediate.save("stage_" + i + ".png");
}
```

---

## 4. Sistema de Injeção de Dependências

### 4.1 Visão Geral

MouseGlob utiliza um framework de DI **customizado** (módulo `Injection`) ao invés de soluções padrão como Guice/Dagger.

**Motivação original**: Controle fino sobre wiring e inicialização.

**Desvantagens**: Maior complexidade de manutenção, menos ferramentas de debugging.

**Futuro**: Considerar migração para Guice (ver roadmap).

### 4.2 Componentes Principais

| Classe | Responsabilidade |
|--------|------------------|
| `Context` | Singleton global, registry de instâncias |
| `Injector` | Resolve dependências e cria instâncias |
| `DependencyGraph` | Grafo de dependências para validação |
| `Indexer` | Descobre classes no classpath via `classes.txt` |
| `@Inject` | Anotação para construtores e métodos injetáveis |

### 4.3 Como Funciona

#### 1. Indexação de Classes

No build, um arquivo `classes.txt` é gerado com todos os `.class`:

```
dcc/mouseglob/MouseGlob.class
dcc/mouseglob/MouseGlobUI.class
dcc/mouseglob/tracking/TrackingManager.class
...
```

Na inicialização:
```java
Context.getInstance().indexClasses(classesUrl);
```

Isso popula um mapa de `SimpleName → ClassName`:
```
"TrackingManager" → "dcc.mouseglob.tracking.TrackingManager"
```

#### 2. Injeção de Dependências

**Via construtor**:
```java
public class TrackingManager {

    @Inject
    public TrackingManager(MovieManager movieManager, MazeController mazeController) {
        // Dependências injetadas automaticamente
    }
}
```

**Via método**:
```java
public class MouseGlob {

    private TrackingManager trackingManager;

    @Inject
    public void setTrackingManager(TrackingManager tm) {
        this.trackingManager = tm;
    }
}
```

**Resolução**:
```java
// Em MouseGlob.main()
TrackingManager tm = Context.getInstance().inject(TrackingManager.class);
```

O `Injector`:
1. Verifica se já existe instância (singleton implícito)
2. Senão, analisa construtores anotados com `@Inject`
3. Resolve dependências recursivamente
4. Cria instância
5. Armazena no `Context`

#### 3. Validação do Grafo

```java
DependencyGraph graph = new DependencyGraph();
graph.add(TrackingManager.class);
graph.validate();  // Detecta ciclos
```

### 4.4 Exemplo Completo

```java
// 1. Definir classe com dependências
public class MinhaAnalise extends AbstractAnalysis {

    private final TrackingManager trackingManager;
    private final PropertiesManager properties;

    @Inject
    public MinhaAnalise(TrackingManager tm, PropertiesManager props) {
        this.trackingManager = tm;
        this.properties = props;
    }

    @Override
    public void calculate() {
        // Usar dependências
    }
}

// 2. Obter instância (dependências resolvidas automaticamente)
MinhaAnalise analise = Context.getInstance().inject(MinhaAnalise.class);
```

### 4.5 Limitações Atuais

- **Sem escopos**: Todas as instâncias são singletons implícitos
- **Sem qualificadores**: Não há como ter múltiplas instâncias de um tipo
- **Sem módulos**: Configuração hardcoded, não modular
- **Debugging difícil**: Pouca visibilidade sobre o grafo de dependências

---

## 5. Sistema de Eventos

### 5.1 Visão Geral

MouseGlob usa o padrão **Observer** extensivamente para comunicação entre componentes.

### 5.2 Principais Eventos e Listeners

| Evento | Listener | Disparador | Consumidor |
|--------|----------|------------|------------|
| `NewFrameEvent` | `NewFrameListener` | `MovieManager`, `CameraManager` | `TrackingManager`, `MazeController`, UI |
| `TrackingEvent` | `TrackingListener` | `TrackingManager` | `VisitEventManager`, `TrajectoriesIO`, Relatórios |
| `VisitEvent` | `VisitEventListener` | `VisitEventManager` | Relatórios de alternação |
| `MovieEvent` | `MovieListener` | `MovieManager` | UI (atualizar controles) |
| `TreeSelectionEvent` | `TreeSelectionListener` | `Tree` | `Inspector`, Relatórios |
| `ZoneEvent` | `ZoneListener` | `Maze` | UI, rastreamento |

### 5.3 Fluxo de Eventos Típico

```
1. Usuário clica "Play"
   └─> MovieManager.play()
       └─> Thread de reprodução inicia loop

2. Loop de reprodução
   ├─> Lê próximo frame do vídeo (FFmpeg)
   ├─> Converte PImage → Image
   ├─> Dispara NewFrameEvent
   │   └─> Notifica todos os NewFrameListeners
   │
   ├─> TrackingManager.onNewFrame(frame, timestamp)
   │   ├─> Processa frame via Pipeline
   │   ├─> Detecta objetos (threshold, contornos, centroide)
   │   ├─> Cria TrackingEvent
   │   └─> Notifica TrackingListeners
   │
   ├─> VisitEventManager.onTracking(event)
   │   ├─> Verifica se objeto entrou em zona
   │   ├─> Cria VisitEvent
   │   └─> Notifica VisitEventListeners
   │
   └─> UI atualiza visualização
       └─> MouseGlobApplet.draw()
           └─> Renderiza frame + sobreposições
```

### 5.4 Implementar um Novo Listener

```java
// 1. Definir interface do listener
public interface MeuEventListener {
    void onMeuEvento(MeuEvento evento);
}

// 2. Definir classe de evento
public class MeuEvento {
    private final Object dados;
    private final long timestamp;

    public MeuEvento(Object dados, long timestamp) {
        this.dados = dados;
        this.timestamp = timestamp;
    }

    // Getters
}

// 3. Adicionar suporte em classe que dispara eventos
public class MeuPublisher {
    private final List<MeuEventListener> listeners = new ArrayList<>();

    public void addListener(MeuEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MeuEventListener listener) {
        listeners.remove(listener);
    }

    private void dispararEvento(MeuEvento evento) {
        for (MeuEventListener listener : listeners) {
            listener.onMeuEvento(evento);
        }
    }
}

// 4. Registrar listener
MeuPublisher publisher = /* ... */;
publisher.addListener(evento -> {
    System.out.println("Evento recebido: " + evento);
});
```

---

## 6. Formatos de Dados

### 6.1 Trajetórias (NDJSON)

**Formato**: Newline-Delimited JSON (1 objeto JSON por linha)

**Schema**: `MouseGlob/src/resource/schemas/trajectory.schema.json`

**Estrutura**:
```json
{"version": "1.0", "experiment": "exp_001", "tracker": "head", "frameCount": 1000, "fps": 30, "calibration": {"pixelsPerCm": 10.5}}
{"frame": 0, "timestamp": 0, "x": 320.5, "y": 240.2, "angle": 1.57, "inZone": "center"}
{"frame": 1, "timestamp": 33, "x": 321.0, "y": 240.8, "angle": 1.58, "inZone": "center"}
...
```

**Linha 1**: Metadados (versão, experimento, calibração)
**Linhas 2+**: 1 medida por frame (coordenadas, ângulo, zona atual)

**Vantagens**:
- Streaming: pode processar linha a linha (baixo uso de memória)
- Append-friendly: pode adicionar frames em tempo real
- Compatível com ferramentas de processamento de texto (`jq`, `grep`, `sed`)

**Export**:
```java
TrajectoriesJSON.exportNdjson(
    experiment,
    trackers,
    new File("trajectory.ndjson")
);
```

**Import** (streaming):
```java
try (BufferedReader reader = new BufferedReader(new FileReader("trajectory.ndjson"))) {
    String metadataLine = reader.readLine();
    JsonNode metadata = objectMapper.readTree(metadataLine);

    String line;
    while ((line = reader.readLine()) != null) {
        JsonNode frame = objectMapper.readTree(line);
        // Processar frame
    }
}
```

### 6.2 Experimentos (XML)

**Codec**: `dcc.mouseglob.experiment.ExperimentXMLEncoder/Decoder`

**Estrutura**:
```xml
<?xml version="1.0"?>
<experiment id="exp_001">
  <metadata>
    <created>2025-11-23T10:30:00Z</created>
    <version>2.0</version>
  </metadata>

  <video>
    <file>/path/to/video.mp4</file>
    <fps>30</fps>
    <frameCount>1000</frameCount>
  </video>

  <calibration>
    <pixelsPerCm>10.5</pixelsPerCm>
    <referenceLength>50.0</referenceLength>
  </calibration>

  <maze>
    <boundaries>
      <polygon>
        <point x="100" y="100"/>
        <point x="500" y="100"/>
        <point x="500" y="400"/>
        <point x="100" y="400"/>
      </polygon>
    </boundaries>
    <zones>
      <zone id="center" name="Centro">
        <circle x="300" y="250" r="50"/>
      </zone>
    </zones>
  </maze>

  <tracking>
    <tracker id="head" name="Cabeça">
      <threshold>180</threshold>
      <minSize>50</minSize>
      <maxSize>500</maxSize>
    </tracker>
  </tracking>

  <analyses>
    <analysis type="Position" enabled="true"/>
    <analysis type="Velocity" enabled="true"/>
  </analyses>
</experiment>
```

**Load**:
```java
Experiment exp = ExperimentXMLDecoder.decode(new File("experiment.xml"));
```

**Save**:
```java
ExperimentXMLEncoder.encode(experiment, new File("experiment.xml"));
```

### 6.3 Relatórios (CSV)

**Formato**: Comma-Separated Values

**Estrutura** (exemplo: série temporal de velocidade):
```csv
Frame,Timestamp (ms),Velocity (cm/s)
0,0,0.0
1,33,2.5
2,66,5.3
3,99,4.8
...
```

**Export**:
```java
// Via UI: clique direito no gráfico → "Export CSV"

// Via código:
ReportExportUtil.exportCSV(
    report,
    new File("velocity.csv")
);
```

**Análise em R**:
```r
data <- read.csv("velocity.csv")
plot(data$Timestamp, data$Velocity, type="l")
```

**Análise em Python**:
```python
import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("velocity.csv")
df.plot(x="Timestamp (ms)", y="Velocity (cm/s)")
plt.show()
```

### 6.4 Gráficos (SVG)

**Formato**: Scalable Vector Graphics (XML)

**Estrutura**:
```xml
<svg xmlns="http://www.w3.org/2000/svg" width="800" height="600">
  <!-- Imagem PNG embutida (compatibilidade) -->
  <image href="data:image/png;base64,..." width="800" height="600"/>

  <!-- Elementos vetoriais (linhas, textos) -->
  <line x1="50" y1="550" x2="750" y2="550" stroke="black"/>
  <text x="400" y="30" text-anchor="middle">Velocidade ao longo do tempo</text>
  <polyline points="50,500 100,450 150,480 ..." stroke="blue" fill="none"/>
</svg>
```

**Vantagens**:
- Vetorial: escalável sem perda de qualidade
- Editável: pode abrir no Inkscape/Illustrator e editar
- PNG embutido: visualização funciona mesmo em viewers que não suportam SVG totalmente

**Export**:
```java
// Via UI: clique direito no gráfico → "Save As SVG"

// Via código:
ReportExportUtil.exportSVG(
    report,
    new File("velocity.svg")
);
```

---

## 7. Guias de Desenvolvimento

### 7.1 Criar uma Nova Análise

#### Passo 1: Implementar a interface `Analysis`

```java
package com.meulab.mouseglob.analises;

import dcc.mouseglob.analysis.AbstractAnalysis;
import dcc.mouseglob.analysis.AnalysisInfo;
import dcc.mouseglob.tracking.Tracker;
import dcc.mouseglob.trajectory.Trajectory;

@AnalysisInfo(
    name = "Aceleração",
    description = "Calcula a aceleração do objeto ao longo do tempo",
    dependencies = {"Velocity"}  // Requer análise de velocidade
)
public class AceleracaoAnalise extends AbstractAnalysis {

    private double[] aceleracao;

    @Override
    public void calculate() {
        Tracker tracker = getTracker();
        Trajectory trajectory = tracker.getTrajectory();

        // Obter velocidades (de análise dependente)
        Analysis velocityAnalysis = getAnalysisManager().getAnalysis("Velocity");
        double[] velocities = (double[]) velocityAnalysis.getResult();

        // Calcular aceleração (derivada da velocidade)
        aceleracao = new double[velocities.length - 1];
        for (int i = 0; i < aceleracao.length; i++) {
            double dt = (trajectory.getTimestamp(i+1) - trajectory.getTimestamp(i)) / 1000.0;  // ms → s
            aceleracao[i] = (velocities[i+1] - velocities[i]) / dt;
        }
    }

    @Override
    public Object getResult() {
        return aceleracao;
    }

    @Override
    public String getResultAsString() {
        if (aceleracao == null) return "N/A";

        double media = Arrays.stream(aceleracao).average().orElse(0);
        double max = Arrays.stream(aceleracao).max().orElse(0);

        return String.format("Média: %.2f cm/s², Máxima: %.2f cm/s²", media, max);
    }
}
```

#### Passo 2: Registrar via SPI

Crie o arquivo `META-INF/services/dcc.mouseglob.analysis.spi.AnalysisProvider`:

```
com.meulab.mouseglob.analises.MeuAnalysisProvider
```

Implemente o provider:

```java
package com.meulab.mouseglob.analises;

import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.spi.AnalysisProvider;
import java.util.List;

public class MeuAnalysisProvider implements AnalysisProvider {

    @Override
    public List<Class<? extends Analysis>> getAnalyses() {
        return List.of(
            AceleracaoAnalise.class
            // Adicionar outras análises customizadas
        );
    }
}
```

#### Passo 3: Compilar e testar

```bash
./gradlew build
./gradlew :MouseGlob:run
```

A análise "Aceleração" aparecerá automaticamente no painel de análises!

### 7.2 Adicionar um Novo Formato de Exportação

#### Exemplo: Exportar para Parquet

```java
package dcc.mouseglob.export;

import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class ParquetExporter {

    public static void exportTrajectory(Trajectory trajectory, File outputFile) throws IOException {

        // Definir schema
        MessageType schema = MessageTypeParser.parseMessageType(
            "message Trajectory {" +
            "  required int32 frame;" +
            "  required int64 timestamp;" +
            "  required double x;" +
            "  required double y;" +
            "  optional double angle;" +
            "}"
        );

        // Criar writer
        try (ParquetWriter<Group> writer = ExampleParquetWriter.builder(new Path(outputFile.toURI()))
                .withType(schema)
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .build()) {

            GroupFactory factory = new SimpleGroupFactory(schema);

            // Escrever cada frame
            for (int i = 0; i < trajectory.size(); i++) {
                Group group = factory.newGroup()
                    .append("frame", i)
                    .append("timestamp", trajectory.getTimestamp(i))
                    .append("x", trajectory.getX(i))
                    .append("y", trajectory.getY(i));

                if (trajectory.hasAngle()) {
                    group.append("angle", trajectory.getAngle(i));
                }

                writer.write(group);
            }
        }
    }
}
```

**Adicionar ao menu**:

```java
// Em TrajectoriesIOView.java
JMenuItem exportParquet = new JMenuItem("Export Parquet...");
exportParquet.addActionListener(e -> {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new FileNameExtensionFilter("Parquet files", "parquet"));

    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        try {
            ParquetExporter.exportTrajectory(
                experiment.getTrajectory(),
                chooser.getSelectedFile()
            );
            JOptionPane.showMessageDialog(this, "Exported successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Export Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
});
menu.add(exportParquet);
```

### 7.3 Adicionar Suporte a Novo Codec de Vídeo

MouseGlob usa **JavaCV (FFmpeg)** para reprodução de vídeo, que já suporta centenas de formatos.

Se precisar adicionar um codec não suportado:

#### Opção 1: Atualizar FFmpeg no JavaCV

```gradle
// Em MouseGlob/build.gradle
dependencies {
    implementation "org.bytedeco:javacv-platform:1.5.11"  // versão mais nova
}
```

#### Opção 2: Usar FFmpeg customizado

```java
// Em MovieManager.java
FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);

// Especificar codec customizado
grabber.setVideoCodec(avcodec.AV_CODEC_ID_H265);
grabber.setOption("hwaccel", "cuda");  // aceleração CUDA

grabber.start();
```

#### Opção 3: Adicionar plugin de codec

```java
// Implementar VideoCodecPlugin
public interface VideoCodecPlugin {
    boolean supports(String extension);
    FrameGrabber createGrabber(File file);
}

// Registrar via SPI
META-INF/services/dcc.mouseglob.video.VideoCodecPlugin
```

### 7.4 Estender a UI com Novo Painel

```java
package com.meulab.mouseglob.ui;

import dcc.module.AbstractModule;
import dcc.module.View;
import javax.swing.*;

// 1. Criar a view
public class MeuPainelView extends JPanel implements View {

    public MeuPainelView() {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Meu Painel Customizado");
        add(label, BorderLayout.NORTH);

        // Adicionar componentes
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
}

// 2. Criar o módulo
public class MeuPainelModule extends AbstractModule<MeuModel, MeuPainelView, MeuController> {

    public MeuPainelModule() {
        super(new MeuModel(), new MeuPainelView(), new MeuController());
    }
}

// 3. Adicionar ao MouseGlobUI
// Em MouseGlobUI.java, método createPanels()
MeuPainelModule meuPainel = new MeuPainelModule();
tabbedPane.addTab("Meu Painel", meuPainel.getView().getComponent());
```

---

## 8. Debugging e Troubleshooting

### 8.1 Configurar Logging

**Nível global** (`~/.mouseglob/mouseglob.properties`):
```properties
logging.level.root=INFO
```

**Nível por pacote**:
```properties
logging.level.dcc.mouseglob.tracking=DEBUG
logging.level.dcc.mouseglob.movie=TRACE
```

**Logs vão para**:
- **Console**: stdout
- **Arquivo**: `~/.mouseglob/logs/mouseglob-YYYY-MM-DD.log`
  - Rotação diária
  - Compressão automática
  - Retenção: 7 dias

**Visualizar logs em tempo real**:
```bash
tail -f ~/.mouseglob/logs/mouseglob-2025-11-23.log
```

### 8.2 Problemas Comuns

#### **Vídeo não abre / erro ao carregar**

**Sintomas**: Exception ao abrir vídeo, tela preta

**Possíveis causas**:
1. Codec não suportado
2. Arquivo corrompido
3. FFmpeg ausente

**Diagnóstico**:
```bash
# Verificar codec do vídeo
ffprobe video.mp4

# Testar com FFmpeg diretamente
ffplay video.mp4
```

**Solução**:
- Recodificar vídeo para H.264:
  ```bash
  ffmpeg -i input.mp4 -c:v libx264 -preset slow -crf 22 output.mp4
  ```
- Atualizar JavaCV para versão mais nova

---

#### **Rastreamento não detecta objeto**

**Sintomas**: Nenhum objeto detectado, trajetória vazia

**Diagnóstico**:
1. Visualizar pipeline intermediário:
   ```java
   // Salvar máscara binária
   TrackingManager tm = /* ... */;
   Image mask = tm.getLastMask();
   mask.save("debug_mask.png");
   ```

2. Verificar threshold:
   - Objeto muito escuro → diminuir threshold
   - Objeto muito claro → aumentar threshold

3. Verificar tamanho do objeto:
   - Objeto pequeno → diminuir `minSize`
   - Muito ruído → aumentar `minSize`

**Soluções**:
- Ajustar parâmetros do pipeline (`default.json`)
- Melhorar iluminação do vídeo
- Usar marcador de alto contraste (LED, fita reflexiva)

---

#### **Análise retorna valores incorretos**

**Sintomas**: Velocidade negativa, distância muito alta, etc.

**Diagnóstico**:
1. Verificar calibração:
   ```java
   Calibration cal = experiment.getCalibration();
   System.out.println("Pixels/cm: " + cal.getPixelsPerCm());
   ```

2. Verificar dependências:
   ```java
   Analysis analysis = /* ... */;
   System.out.println("Dependências: " + Arrays.toString(analysis.getDependencies()));
   ```

3. Verificar trajetória:
   ```java
   Trajectory traj = tracker.getTrajectory();
   for (int i = 0; i < Math.min(10, traj.size()); i++) {
       System.out.printf("Frame %d: x=%.2f, y=%.2f%n", i, traj.getX(i), traj.getY(i));
   }
   ```

**Soluções**:
- Recalibrar escala espacial
- Verificar se análises dependentes foram executadas
- Validar timestamps (ordem crescente, sem duplicatas)

---

#### **Crash / OutOfMemoryError**

**Sintomas**: `java.lang.OutOfMemoryError: Java heap space`

**Causa**: Vídeo muito grande, muitos frames retidos na memória

**Solução**:
```bash
# Aumentar heap size
export JAVA_OPTS="-Xmx4G -Xms1G"
./gradlew :MouseGlob:run
```

Ou editar `MouseGlob/build.gradle`:
```gradle
application {
    applicationDefaultJvmArgs = ["-Xmx4G", "-Xms1G"]
}
```

**Otimizações**:
- Usar object pools (já implementado para matrizes)
- Processar em batch (chunks de frames)
- Reduzir resolução do vídeo

---

#### **UI congelada / travada**

**Sintomas**: Interface não responde, botões não funcionam

**Causa**: Operação bloqueante na Event Dispatch Thread (EDT)

**Diagnóstico**:
```bash
# Thread dump
jstack <pid> | grep "AWT-EventQueue"
```

**Solução**: Mover operação longa para background thread

```java
// ERRADO (bloqueia EDT)
button.addActionListener(e -> {
    processarVideoCompleto();  // operação lenta
});

// CORRETO (background thread)
button.addActionListener(e -> {
    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() {
            processarVideoCompleto();
            return null;
        }

        @Override
        protected void done() {
            // Atualizar UI no EDT
            JOptionPane.showMessageDialog(null, "Concluído!");
        }
    };
    worker.execute();
});
```

---

### 8.3 Ferramentas de Debug

#### **Ativar modo verbose**

```bash
./gradlew :MouseGlob:run --debug
```

#### **Conectar debugger remoto**

```bash
# Executar com debug port
./gradlew :MouseGlob:run --debug-jvm

# No IntelliJ IDEA:
# Run → Edit Configurations → + Remote JVM Debug
# Host: localhost, Port: 5005
```

#### **Profiling de performance**

```bash
# JProfiler, YourKit, VisualVM
jvisualvm
# Attach to MouseGlob process
```

#### **Inspecionar injeção de dependências**

```java
// Imprimir grafo de dependências
Context context = Context.getInstance();
DependencyGraph graph = context.getDependencyGraph();
graph.print();
```

---

## 9. Performance e Otimização

### 9.1 Métricas Atuais

| Operação | Tempo Médio | Target |
|----------|-------------|--------|
| Carregar frame (FFmpeg) | ~2-5 ms | < 10 ms |
| Pipeline de processamento | ~5-15 ms | < 33 ms (30 FPS) |
| Detecção de objeto | ~2-8 ms | < 10 ms |
| Renderização (Processing) | ~5-10 ms | < 16 ms (60 FPS) |

**Bottleneck principal**: Pipeline de processamento em vídeos de alta resolução (> 1080p)

### 9.2 Otimizações Implementadas

#### **Object Pools** (reduz GC)
```java
// DoubleMatrixPool: reutiliza double[][]
DoubleMatrixPool pool = DoubleMatrixPool.getInstance();
double[][] matrix = pool.acquire(width, height);
// Usar matrix
pool.release(matrix);
```

#### **Frame Queue com Backpressure**
```java
// Em MovieManager.java
BlockingQueue<Frame> frameQueue = new ArrayBlockingQueue<>(5);

// Producer
if (!frameQueue.offer(frame, 100, TimeUnit.MILLISECONDS)) {
    // Queue cheia, descartar frame antigo
    frameQueue.poll();
    frameQueue.offer(frame);
}

// Consumer
Frame frame = frameQueue.poll(100, TimeUnit.MILLISECONDS);
```

#### **Lazy Loading de Análises**
```java
// Análises só são calculadas quando requisitadas
@Override
public Object getResult() {
    if (result == null) {
        calculate();
    }
    return result;
}
```

### 9.3 Otimizações Futuras (Roadmap)

#### **GPU Acceleration via OpenCV**
```java
// Usar cv::cuda para operações de imagem
import org.opencv.core.cuda.GpuMat;

GpuMat gpuInput = new GpuMat();
gpuInput.upload(inputMat);

// Operações na GPU
cv::cuda::threshold(gpuInput, gpuOutput, 128, 255, THRESH_BINARY);

gpuOutput.download(outputMat);
```

#### **Parallel Processing de Frames**
```java
// Processar múltiplos frames em paralelo
ExecutorService executor = Executors.newFixedThreadPool(4);
List<Future<TrackingResult>> futures = new ArrayList<>();

for (Frame frame : frames) {
    Future<TrackingResult> future = executor.submit(() -> processFrame(frame));
    futures.add(future);
}

// Coletar resultados
for (Future<TrackingResult> future : futures) {
    TrackingResult result = future.get();
    // ...
}
```

#### **Cache de Pipeline**
```java
// Cache de resultados intermediários do pipeline
Map<Integer, Image> pipelineCache = new LRUCache<>(100);

Image processFrame(Frame frame) {
    int hash = frame.hashCode();
    if (pipelineCache.containsKey(hash)) {
        return pipelineCache.get(hash);
    }

    Image result = pipeline.process(frame);
    pipelineCache.put(hash, result);
    return result;
}
```

---

## 10. Roadmap Técnico

### 10.1 Curto Prazo (3-6 meses)

#### **P0 - Crítico**
- [ ] Migrar logging para SLF4J (remover `System.out`)
- [ ] Adicionar validação de JSON Schema em runtime
- [ ] Implementar retry logic em falhas de I/O de vídeo
- [ ] Adicionar testes de integração (JUnit 5)

#### **P1 - Alta Prioridade**
- [ ] Reativar captura de câmera (`CameraManager`)
- [ ] Adicionar seleção de dispositivo de câmera na UI
- [ ] Melhorar mensagens de erro (user-friendly)
- [ ] Adicionar métricas de performance na UI (FPS, latência)

#### **P2 - Média Prioridade**
- [ ] Exportação para Parquet (análise em larga escala)
- [ ] Suporte a multi-threading no pipeline
- [ ] Adicionar wizard de calibração
- [ ] Melhorar HiDPI support

### 10.2 Médio Prazo (6-12 meses)

#### **Arquitetura**
- [ ] Migrar DI customizado para Guice
  - Benefícios: módulos, escopos, melhor debugging
  - Esforço: ~2-3 semanas
  - Impacto: reduz complexidade, melhora testabilidade

- [ ] Separar lógica de domínio da UI (headless-first)
  - Criar `mouseglob-core` (sem Swing/Processing)
  - Criar `mouseglob-ui` (depende de core)
  - Permite CLI robusto e integração programática

- [ ] Adicionar suporte a plugins externos
  - Classloader isolado para plugins
  - API estável para extensões
  - Marketplace de plugins?

#### **Performance**
- [ ] GPU acceleration via OpenCV CUDA
- [ ] Parallel frame processing
- [ ] Streaming de vídeo (processar sem carregar completo)
- [ ] Benchmarks automatizados

#### **Qualidade**
- [ ] Cobertura de testes > 60%
- [ ] CI/CD matrix (Linux/macOS/Windows)
- [ ] Análise estática (SpotBugs, Checkstyle)
- [ ] Formatação automática (Spotless)

### 10.3 Longo Prazo (12+ meses)

#### **Funcionalidades Avançadas**
- [ ] Machine Learning para rastreamento (YOLO, DeepSORT)
- [ ] Rastreamento de múltiplos animais simultâneos
- [ ] Reconhecimento de comportamentos complexos (grooming, rearing)
- [ ] Integração com sistemas de aquisição em tempo real

#### **Infraestrutura**
- [ ] Cloud processing (AWS Lambda, Google Cloud Functions)
- [ ] Web UI (via WebAssembly ou backend REST)
- [ ] Banco de dados para armazenar experimentos (PostgreSQL)
- [ ] API REST para integração com outros sistemas

#### **Comunidade**
- [ ] Documentação completa (Sphinx/MkDocs)
- [ ] Tutoriais em vídeo
- [ ] Dataset público de exemplo
- [ ] Publicação científica descrevendo a ferramenta

---

## 11. Decisões Arquiteturais

### 11.1 Por que DI customizado ao invés de Guice?

**Decisão**: Framework de DI customizado (`dcc.inject.*`)

**Contexto**: Projeto iniciado antes da popularização de Guice/Dagger

**Vantagens**:
- Controle total sobre resolução de dependências
- Sem dependências externas pesadas
- Aprendizado educacional

**Desvantagens**:
- Maior complexidade de manutenção
- Falta de features avançadas (escopos, qualificadores)
- Debugging mais difícil

**Revisão**: Considerar migração para Guice no futuro (roadmap médio prazo)

---

### 11.2 Por que Processing ao invés de JavaFX/Swing puro?

**Decisão**: Usar Processing (`PApplet`) para renderização

**Vantagens**:
- Renderização 2D eficiente
- API simples e intuitiva
- Comunidade grande (arte generativa, visualização)

**Desvantagens**:
- Baseado em AWT (legado)
- Menos moderno que JavaFX
- Menos componentes UI prontos

**Alternativas consideradas**:
- JavaFX: mais moderno, mas curva de aprendizado maior
- Swing puro: menos eficiente para renderização 2D

**Decisão atual**: Manter Processing, avaliar JavaFX para UI (não rendering) no futuro

---

### 11.3 Por que XML ao invés de JSON para experimentos?

**Decisão**: XML para persistência de experimentos (legado), JSON para trajetórias (novo)

**Contexto**: XML foi escolhido inicialmente por ser padrão em Java

**Migração em andamento**:
- Trajetórias: JSON/NDJSON ✅
- Pipelines: JSON ✅
- Experimentos: XML (considerando migração)

**Plano futuro**: Migrar tudo para JSON + JSON Schema

---

### 11.4 Por que NDJSON ao invés de JSON para trajetórias?

**Decisão**: Newline-Delimited JSON (1 objeto por linha)

**Vantagens**:
- **Streaming**: processar linha a linha (baixo uso de memória)
- **Append-friendly**: adicionar frames em tempo real
- **Ferramentas**: compatível com `jq`, `grep`, `awk`

**Desvantagens**:
- Menos legível que JSON "pretty"
- Necessita parsing linha a linha

**Alternativas consideradas**:
- JSON array: simples, mas precisa carregar tudo na memória
- Parquet: eficiente, mas binário e requer bibliotecas pesadas
- CSV: simples, mas perde tipagem e estrutura

**Decisão**: NDJSON é ideal para trajetórias longas (milhares de frames)

---

### 11.5 Por que não usar OpenCV diretamente?

**Decisão**: Usar JavaCV (wrapper) + código customizado

**Motivo**: Projeto iniciado antes de JavaCV ser maduro

**Estado atual**: Migração parcial
- Vídeo: JavaCV (FFmpeg) ✅
- Câmera: JavaCV (OpenCV) ✅
- Processamento de imagem: código customizado (`dcc.graphics.*`)

**Plano futuro**: Migrar processamento de imagem para OpenCV
- **Vantagens**: GPU acceleration, algoritmos otimizados
- **Desvantagens**: Dependência externa maior
- **Esforço**: ~4-6 semanas

---

## 12. FAQ para Desenvolvedores

### Como adiciono um novo tipo de análise?
Ver seção [7.1 Criar uma Nova Análise](#71-criar-uma-nova-análise)

### Como faço para debugar o pipeline de processamento?
Ver seção [3.5 Debugging do Pipeline](#35-debugging-do-pipeline)

### Onde ficam os logs?
`~/.mouseglob/logs/mouseglob-YYYY-MM-DD.log`

### Como executo em modo headless (sem GUI)?
```bash
./gradlew :MouseGlob:runCli -- --batch --input videos/ --output results/
```

### Como adiciono uma nova dependência Maven?
Edite `MouseGlob/build.gradle`:
```gradle
dependencies {
    implementation "group:artifact:version"
}
```

### Como atualizo a versão do Java?
1. Edite `build.gradle` (root):
   ```gradle
   java {
       toolchain {
           languageVersion = JavaLanguageVersion.of(21)  // mudar para 22, 23, etc.
       }
   }
   ```
2. Recompile: `./gradlew clean build`

### Como gero um instalador Windows?
```bash
gradlew.bat jpackage
# Resultado: MouseGlob/build/jpackage/MouseGlob-2.0.1.msi
```

### Como contribuo com código?
1. Fork o repositório
2. Crie branch: `git checkout -b feature/minha-feature`
3. Implemente + testes
4. Commit: `git commit -m "feat: descrição"`
5. Push: `git push origin feature/minha-feature`
6. Abra Pull Request

---

## 13. Recursos Adicionais

### Documentação Oficial
- [README.md](README.md) - Documentação para usuários
- [revision.md](revision.md) - Histórico de design e decisões
- [TODO.txt](TODO.txt) - Lista de tarefas

### Schemas
- [trajectory.schema.json](MouseGlob/src/resource/schemas/trajectory.schema.json)
- [experiment.schema.json](MouseGlob/src/resource/schemas/experiment.schema.json)

### Exemplos
- [default.json](MouseGlob/src/resource/pipelines/default.json) - Pipeline padrão
- [PipelineTests.java](MouseGlob/src/test/java/dcc/mouseglob/tracking/pipeline/PipelineTests.java) - Testes de pipeline

### Bibliotecas Externas
- [Processing](https://processing.org/reference/)
- [JavaCV](https://github.com/bytedeco/javacv)
- [Jackson](https://github.com/FasterXML/jackson-docs)
- [FlatLaf](https://www.formdev.com/flatlaf/)

---

## 14. Changelog

### v2.0.1 (2025-11-23)
- ✅ Atualização para Java 21
- ✅ Pipeline modular de processamento (JSON configurável)
- ✅ Exportação NDJSON para trajetórias
- ✅ Exportação CSV/SVG para relatórios
- ✅ Sistema de plugins via SPI
- ✅ GitHub Actions CI/CD para Windows
- ✅ Suporte a instalador Windows (.msi/.exe)
- ✅ Documentação completa (README.md + CLAUDE.md)

### v1.x (legado)
- Versão original em Java 8
- Build via Ant + Eclipse
- DI customizado
- XML para persistência

---

## 15. Contato para Desenvolvedores

Para questões técnicas, bugs ou contribuições:

- **Issues**: [GitHub Issues](https://github.com/seu-usuario/mouseglob/issues)
- **Pull Requests**: [GitHub PRs](https://github.com/seu-usuario/mouseglob/pulls)
- **Discussões**: [GitHub Discussions](https://github.com/seu-usuario/mouseglob/discussions)

---

**Este documento é vivo e deve ser atualizado conforme o projeto evolui.**

**Última atualização**: 2025-11-24
**Mantenedores**: [Lista de mantenedores]
**Licença**: GNU General Public License v3.0 (GPLv3) - veja [license.txt](license.txt)
