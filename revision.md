### Visão geral do projeto
O repositório contém uma aplicação de análise de comportamento em vídeo chamada `MouseGlob`, atualizada para Java 21 e empacotada com Gradle (multi-módulo: `Injection` e `MouseGlob`). A aplicação oferece uma interface Swing que incorpora um `PApplet` (Processing) para visualização e interação, permitindo:

- Abrir e reproduzir vídeos (`dcc.mouseglob.movie.MovieManager`) e, opcionalmente, capturar da câmera (`dcc.mouseglob.camera.CameraManager`).
- Calibrar a cena/escala (`dcc.mouseglob.calibration.*`).
- Definir limites do labirinto (boundaries) e zonas de interesse (`dcc.mouseglob.maze.*`).
- Realizar rastreamento do objeto (p.ex., um roedor) ao longo dos frames (`dcc.mouseglob.tracking.*`).
- Detectar eventos de visita a zonas e produzir relatórios/análises (`dcc.mouseglob.visit.*`, `dcc.mouseglob.report.*`, `dcc.mouseglob.analysis.*`).
- Importar/exportar experimentos, labirintos e trajetórias (`dcc.mouseglob.experiment.*`, `dcc.mouseglob.maze.io.*`, `dcc.mouseglob.trajectory.*`).

A orquestração fica em `dcc.mouseglob.MouseGlob`, que injeta e conecta os módulos via um framework de injeção próprio (módulo `Injection`). A UI principal está em `dcc.mouseglob.MouseGlobUI`, que monta menus, toolbars e painéis (vídeo, calibragem, maze, tracking, análises, árvore de componentes, etc.).

Principais integrações externas:
- Processing Core e Video (`processing.core`, `processing.video`) para renderização e leitura de vídeo/câmera.
- JARs locais (gstreamer-java, jna, jlfgr) ainda presentes em `MouseGlob/lib` e adicionados ao classpath.

Fluxo geral em tempo de execução:
1) `MouseGlob.main` carrega propriedades, injeta as classes a partir de `classes.txt` e cria a GUI.
2) `MovieManager` e/ou `CameraManager` publicam frames (com timestamps) a diversos ouvintes (`NewFrameListener`), como limite/maze, `TrackingManager` e gerenciador de eventos/teclas.
3) `TrackingManager` emite eventos de tracking para `VisitEventManager` e IO de trajetórias.
4) A árvore `dcc.tree.Tree` reflete a estrutura do experimento e conecta inspeção, seleção e relatórios.

Observação relevante: no `CameraManager`, a captura está parcialmente desativada (comentada com `// TODO Reactivate camera`).

### Estrutura (arquivos e responsabilidades principais)
- `dcc.mouseglob.MouseGlob`: módulo principal que conecta paintables e listeners (novo frame, mouse, cursor, árvore, zonas, experimento) e mantém `CameraManager`/`MovieManager` para fechamento ordenado.
- `dcc.mouseglob.MouseGlobUI`: janela Swing, cria menus (arquivo/visualização/eventos), toolbars e painéis, integra sub-views: `MovieUI`, `MazeUI`, `CalibrationView`, `TrackingUI`, `AnalysesView`, `ExperimentIOView`, `TrajectoriesIOView`, `InspectorUI` e o `MouseGlobApplet`.
- `dcc.mouseglob.movie.MovieManager`: carrega filmes, controla reprodução (play/pause/rewind/fast-forward/jump/speed), gerencia fila/latência e dispara frames como `Image` (adaptados de `PImage`). Notifica `MovieListener` para mudanças de estado.
- `dcc.mouseglob.camera.CameraManager`: gerencia captura de câmera via `processing.video.Capture` (instancia em thread, ajusta o tamanho do applet; hoje com inicialização comentada). Dispara frames e timestamps.
- `dcc.mouseglob.tracking.*`: núcleo do rastreamento e seus eventos, controle (`TrackingController`), UI e codecs XML para persistência (`TrackingXML*`).
- `dcc.mouseglob.maze.*` e `dcc.mouseglob.maze.io.*`: gerenciamento de limites, zonas e IO de labirintos; controladores reagem a frames e eventos.
- `dcc.mouseglob.calibration.*`: modelo e controlador/visão para calibragem (também ouvinte de eventos de filme/mouse/cursor).
- `dcc.mouseglob.trajectory.*`: IO de trajetórias (exportação/importação), também escuta eventos de tracking para gravação.
- `dcc.mouseglob.analysis.*`: seleção/gerenciamento de análises com dependências. `AnalysesController` mostra um diálogo permitindo ativar/desativar análises, respeitando dependências e obrigatoriedades (via `@AnalysisInfo`).
- `dcc.mouseglob.report.*`: geração de relatórios, integrados a seleção na árvore e ao tracking.
- `dcc.mouseglob.keyevent.*`: mapeamento e gerenciamento de eventos de tecla ao player/tracking.
- `dcc.tree.*` e `dcc.ui.*`: infraestrutura de árvore e componentes UI utilitários.
- Módulo `Injection`: framework de injeção/factory próprio (`dcc.inject.*`, `dcc.module.*`), com `Context`, `Indexer`, `@Inject`, `AbstractModule`, `Controller`, `View` etc.

### Como executar
- Compilar: `gradle build`
- Rodar app: `gradle :MouseGlob:run`
- Gerar distribuição: `gradle :MouseGlob:installDist` (scripts em `MouseGlob/build/install/MouseGlob/bin/`)

### Pontos fortes
- Arquitetura modular clara: separa UI, controle, modelo e IO por domínio (vídeo, tracking, maze, calibração, etc.).
- Pipeline de eventos bem definido: listeners para frames, árvore, mouse, cursor, experimento, zonas e tracking.
- Suporte a reprodução de vídeo com controle completo e difusão de frames; timestamping consistente.
- Persistência e import/export de experimentos, maze e trajetórias; relatórios e análises com metadados e dependências.

### Pontos de melhoria (técnicos e de produto)
1) Dependências e empacotamento
    - Substituir JARs locais por dependências publicadas (Maven Central) e fixar versões no `build.gradle`. Isso reduz atrito e melhora reprodutibilidade.
    - Atualizar stack de vídeo: `processing.video` depende de GStreamer legacy. Avaliar migração para OpenCV (via `bytedeco/javacv`), GStreamer moderno (gstreamer 1.x bindings) ou `FFmpegFrameGrabber` para maior robustez cross-platform.
    - Remover artefatos legados (Ant `.xml`, `.iml`) do build ativo; mantê-los apenas como histórico, fora do caminho de build.

2) Câmera e I/O multimídia
    - Reativar e tratar a inicialização da câmera no `CameraManager` (o código está comentado). Incluir fallback/diagnóstico detalhado (logs) e seleção de dispositivo/resolução.
    - Unificar caminho de frames (câmera/arquivo) com um contrato comum e filas com backpressure para evitar stutter; hoje existe, mas pode ganhar métricas e autotuning.
    - Suportar formatos modernos e hardware acceleration quando disponível (e.g., via OpenCV/GStreamer 1.x).

3) Injeção de dependências
    - Avaliar substituir o framework de DI próprio (`Injection`) por uma solução padrão como Guice ou Dagger. Benefícios: escopos, módulos, integração com ferramentas, testes e manutenção.
    - Se manter DI próprio, adicionar validação estática do grafo, relatórios de wiring e testes cobrindo ciclos/dependências.

4) Concorrência e desempenho
    - Revisar threading no `MovieManager` (executor de comandos e filas) e `CameraManager`. Usar `ExecutorService`, `CompletableFuture` e filas `BlockingQueue` com limites para controle de latência.
    - Evitar cópias desnecessárias de frames entre `PImage` → `Image`; reutilizar buffers quando possível; considerar pool de buffers.
    - Adicionar métricas (FPS alvo, FPS efetivo, tempo médio de processamento por frame com `AveragingStopwatch`) e expor na UI.

5) Processamento e tracking
    - Documentar e modularizar os algoritmos de rastreamento; considerar migrar partes para OpenCV (background subtraction, threshold adaptativo, filtros morfológicos, Hough, etc.).
    - Criar uma camada de “pipelines” de processamento configuráveis (ex.: JSON/YAML) para facilitar experimentação sem recompilar.
    - Introduzir testes com vídeos de exemplo para validar robustez a ruído, iluminação, oclusões.

6) UI/UX
    - Modernizar aparência (LaF, ícones, escalabilidade HiDPI). Adicionar modo escuro e atalhos personalizáveis expostos na UI.
    - Mostrar diagnósticos de latência, uso de CPU/GPU, perdas de frame, status da câmera.
    - Assistentes (wizards) para calibração e definição de zonas; validação visual clara.

7) Persistência e formatos
    - Hoje há encoders/decoders XML para tracking e outros artefatos. Considerar JSON + schema (JSON Schema) para interoperabilidade.
    - Gerar relatórios também em `CSV`/`Parquet` e gráficos embutidos (PNG/SVG) para facilitar publicação.

8) Qualidade de código e testes
    - Adicionar testes unitários e de integração (JUnit 5), especialmente para: leitura de vídeo, rastreamento, IO de trajetórias e análises.
    - Configurar CI (GitHub Actions) com build matrix (Linux/macOS/Windows), cache de dependências e smoke tests com pequenos clipes.
    - Habilitar análise estática (SpotBugs, Checkstyle/PMD), cobertura (JaCoCo) e formatação (Spotless/Google Java Format).

9) Logging, erros e propriedades
    - Substituir `System.out` por `SLF4J` com implementação (Logback). Nivelar logs por módulo, com MDC para id do experimento.
    - Fortalecer `PropertiesManager`: caminhos por `java.nio.file.Path`, escopos de perfil (dev/prod), validação e defaults; preferencialmente oferecer uma UI de preferências.
    - Mensagens de erro amigáveis na UI com ações de correção (ex.: “Instale GStreamer 1.x”, “Selecione dispositivo de câmera”).

10) Arquitetura e extensibilidade
- Formalizar um contrato de plugin para novas análises (`Analysis`) com descoberta por `ServiceLoader` ao invés de lista estática.
- Desacoplar a lógica de domínio da UI para facilitar headless/CLI e processamento em lote.
- Exportar/importar “projeto de experimento” completo (vídeos + configurações + zonas + calibração) num bundle portátil.

11) Documentação
- Expandir o `README` com: visão funcional do app, screenshots, exemplos de uso, datasets de exemplo e guias passo a passo (calibração, zonas, tracking, export, relatórios).
- Adicionar Javadoc às classes principais (`MovieManager`, `TrackingManager`, `Maze`/`Zones`, `AnalysesManager`).

### Itens rápidos e práticos
- Reativar a câmera: descomentar `start(applet)` no `CameraManager` e tratar falhas, oferecendo escolha de dispositivo.
- Incluir logging SLF4J e remover `System.out.println(injection)` do `main`.
- Publicar as libs locais como dependências em `build.gradle` (ou apontar para forks atualizados).
- Incluir um pequeno vídeo de exemplo e um “projeto” de demonstração, com script para abrir automaticamente.

Se quiser, posso detalhar um plano de migração do pipeline de vídeo para OpenCV ou montar um checklist de CI/CD para este projeto.