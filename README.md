# MouseGlob

**Plataforma multiplataforma para análise comportamental de roedores em vídeo**

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Gradle](https://img.shields.io/badge/Gradle-8.5+-blue.svg)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-GPLv3-blue.svg)](license.txt)

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Propósito e Responsabilidades](#-propósito-e-responsabilidades)
- [Funcionalidades Principais](#-funcionalidades-principais)
- [Público-Alvo](#-público-alvo)
- [Casos de Uso](#-casos-de-uso)
- [Instalação e Execução](#-instalação-e-execução)
- [Guia Rápido](#-guia-rápido)
- [Arquitetura](#-arquitetura)
- [Contribuição](#-contribuição)
- [Documentação Técnica](#-documentação-técnica)

## 🎯 Visão Geral

**MouseGlob** é uma aplicação científica especializada em **análise automatizada de comportamento animal em vídeos**, desenvolvida para pesquisadores em neurociência, farmacologia e etologia. A ferramenta permite rastrear, quantificar e analisar o comportamento de roedores em ambientes controlados (labirintos, arenas abertas, etc.) com precisão e reprodutibilidade.

### Por que MouseGlob?

- **Automação completa**: Elimina a necessidade de análise manual frame a frame
- **Reprodutibilidade**: Análises consistentes e auditáveis via configurações exportáveis
- **Extensibilidade**: Sistema de plugins permite criar análises customizadas
- **Open Source**: Código aberto, transparente e auditável pela comunidade científica
- **Multiplataforma**: Funciona em Windows, Linux e macOS

## 🎓 Propósito e Responsabilidades

### Propósito

MouseGlob foi criado para **democratizar o acesso a ferramentas de análise comportamental computadorizada**, oferecendo uma alternativa open-source a softwares comerciais caros. O projeto visa:

1. **Facilitar a pesquisa científica** em comportamento animal com ferramentas robustas e acessíveis
2. **Garantir reprodutibilidade** através de pipelines de processamento configuráveis e auditáveis
3. **Reduzir viés humano** na análise comportamental através de automação
4. **Promover colaboração** científica com formatos de dados abertos e interoperáveis

### Responsabilidades

A aplicação é responsável por:

#### 1. Captura e Reprodução de Vídeo
- Abrir arquivos de vídeo em múltiplos formatos (via FFmpeg)
- Capturar vídeo de câmeras em tempo real (via OpenCV)
- Controlar reprodução (play, pause, velocidade, navegação frame a frame)
- Gerenciar sincronização temporal para análises precisas

#### 2. Calibração Espacial
- Converter pixels em unidades reais (cm, mm)
- Permitir calibração visual interativa
- Armazenar e recuperar calibrações para reuso

#### 3. Definição de Ambiente Experimental
- Desenhar limites da arena/labirinto (boundaries)
- Definir zonas de interesse nomeadas (ex: braço esquerdo, centro, periferia)
- Detectar eventos de entrada/saída de zonas

#### 4. Rastreamento de Objetos
- Rastrear múltiplos pontos do animal (cabeça, corpo, cauda)
- Pipeline modular de processamento de imagem:
  - Conversão para escala de cinza
  - Subtração de fundo (background subtraction)
  - Limiarização adaptativa
  - Operações morfológicas (erosão, dilatação)
- Gerar trajetórias temporais com coordenadas XY

#### 5. Análises Comportamentais
Sistema extensível com 23+ análises pré-implementadas:
- **Espaciais**: posição, distância percorrida, mapa de calor
- **Cinéticas**: velocidade, aceleração, paradas
- **Angulares**: orientação, mudanças de direção, rotações
- **Cognitivas**: alternação espontânea, tempo em zonas

#### 6. Geração de Relatórios
- Gráficos interativos em tempo real
- Exportação de dados em múltiplos formatos:
  - **CSV**: para análise estatística (R, Python, SPSS)
  - **NDJSON**: formato estruturado para processamento automatizado
  - **SVG/PNG**: gráficos vetoriais e rasterizados
- Mapas de calor e visualizações espaciais

#### 7. Persistência e Reprodutibilidade
- Salvar experimentos completos (vídeo + configurações + análises)
- Exportar/importar configurações de pipeline
- Garantir auditabilidade através de metadados detalhados

## ✨ Funcionalidades Principais

### Interface Gráfica Intuitiva
- Visualização em tempo real do vídeo com sobreposições (zonas, trajetórias)
- Controles de reprodução completos
- Painéis modulares para cada funcionalidade
- Árvore hierárquica de componentes do experimento

### Pipeline de Processamento Configurável
Configure o processamento de imagem via JSON sem recompilar:

```json
{
  "stages": [
    { "type": "grayscale" },
    { "type": "background", "mode": "running", "alpha": 0.02 },
    { "type": "adaptiveThreshold", "mode": "adaptiveMean", "blockSize": 15, "c": 5 },
    { "type": "morphology", "operation": "open", "kernel": "3x3" }
  ]
}
```

### Sistema de Plugins
Crie análises customizadas implementando a interface `Analysis` e registrando via `ServiceLoader`:

```java
@AnalysisInfo(
    name = "Minha Análise",
    description = "Descrição da análise customizada"
)
public class MinhaAnalise extends AbstractAnalysis {
    // Implementação
}
```

### Modo Batch (CLI)
Processe múltiplos vídeos sem interface gráfica:

```bash
./gradlew :MouseGlob:runCli -- --batch --input videos/ --output results/
```

## 👥 Público-Alvo

### Pesquisadores em Neurociência e Farmacologia
- Avaliar efeitos de drogas em comportamento motor
- Quantificar ansiedade em testes de labirinto elevado
- Estudar memória espacial em labirintos aquáticos

### Pesquisadores em Etologia
- Analisar padrões de exploração espacial
- Quantificar comportamentos sociais
- Estudar preferências de habitat

### Desenvolvedores e Bioinformatas
- Integrar análises comportamentais em pipelines maiores
- Desenvolver novas análises customizadas
- Automatizar processamento em larga escala

### Estudantes e Educadores
- Aprender conceitos de visão computacional aplicada
- Ensinar metodologia científica quantitativa
- Demonstrar análise comportamental automatizada

## 🔬 Casos de Uso

### 1. Teste de Labirinto em Y (Alternação Espontânea)

**Objetivo**: Avaliar memória de trabalho espacial

**Fluxo**:
1. Abrir vídeo do experimento
2. Calibrar escala espacial
3. Definir zonas: braço esquerdo, direito, centro
4. Configurar rastreamento do animal
5. Executar análise de "Alternação Espontânea"
6. Exportar dados: sequência de visitas, % de alternação

**Saída**: CSV com eventos de entrada/saída + relatório estatístico

### 2. Teste de Campo Aberto (Ansiedade)

**Objetivo**: Quantificar comportamento exploratório e ansiedade

**Fluxo**:
1. Abrir vídeo da arena
2. Definir zonas: centro (aversivo) e periferia (preferencial)
3. Rastrear posição e movimento do animal
4. Gerar análises: tempo em cada zona, distância percorrida, velocidade média
5. Criar mapa de calor da ocupação espacial
6. Exportar gráficos e dados estatísticos

**Saída**: Mapa de calor (PNG/SVG) + CSV com métricas temporais

### 3. Processamento em Lote de Múltiplos Experimentos

**Objetivo**: Analisar dezenas de vídeos com configurações padronizadas

**Fluxo**:
1. Criar arquivo de configuração de pipeline (JSON)
2. Definir template de zonas e calibração
3. Executar modo batch via CLI:
   ```bash
   ./gradlew :MouseGlob:runCli -- \
     --batch \
     --config experiment-template.json \
     --input-dir /data/videos/ \
     --output-dir /data/results/
   ```
4. Consolidar resultados CSV para análise estatística (R/Python)

**Saída**: Diretório com resultados individuais + consolidado

### 4. Desenvolvimento de Análise Customizada

**Objetivo**: Implementar métrica comportamental específica

**Fluxo**:
1. Criar classe implementando `Analysis`
2. Anotar com `@AnalysisInfo`
3. Implementar cálculo baseado em trajetórias
4. Registrar via `META-INF/services/dcc.mouseglob.analysis.spi.AnalysisProvider`
5. Compilar e testar na UI
6. Compartilhar como plugin externo

**Benefício**: Extensibilidade sem modificar código-fonte

## 🚀 Instalação e Execução

### Pré-requisitos

- **JDK 21** ou superior ([Download Adoptium](https://adoptium.net/))
- **Gradle 8.5+** (incluído via wrapper)
- **FFmpeg** (para reprodução de vídeos)
- **OpenCV** (para captura de câmera - opcional)

### Compilar e Executar

#### Linux / macOS

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/mouseglob.git
cd mouseglob

# Compile o projeto
./gradlew build

# Execute a aplicação
./gradlew :MouseGlob:run
```

#### Windows

```cmd
REM Clone o repositório
git clone https://github.com/seu-usuario/mouseglob.git
cd mouseglob

REM Compile o projeto
gradlew.bat build

REM Execute a aplicação
gradlew.bat :MouseGlob:run
```

### Instalador Windows (sem JDK)

Usuários Windows podem baixar o instalador `.msi` que **não requer Java instalado**:

1. Acesse a [página de Releases](https://github.com/seu-usuario/mouseglob/releases)
2. Baixe `MouseGlob-Windows-Installer.zip`
3. Extraia e execute o `.msi`
4. A aplicação será instalada com JRE embutido

Ou gere localmente:

```cmd
REM Requer WiX Toolset instalado
gradlew.bat jpackage
```

Instalador gerado em: `MouseGlob\build\jpackage\MouseGlob-2.0.1.msi`

### Docker

```bash
# Build da imagem
docker build -t mouseglob .

# Execute (GUI requer X11 forwarding)
docker run -it --rm \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  mouseglob
```

## 📖 Guia Rápido

### 1. Primeiro Uso: Analisar um Vídeo

1. **Abrir vídeo**: Menu `Arquivo > Abrir Vídeo` (ou arraste o arquivo para a janela)

2. **Calibrar escala**:
   - Vá para o painel "Calibração"
   - Desenhe uma linha de tamanho conhecido no vídeo
   - Insira o tamanho real (ex: 50 cm)
   - Clique em "Salvar Calibração"

3. **Definir arena/zonas**:
   - Painel "Maze/Zonas"
   - Desenhe o limite da arena (boundary)
   - Adicione zonas de interesse (clique direito > "Adicionar Zona")
   - Nomeie as zonas (ex: "Centro", "Periferia")

4. **Configurar rastreamento**:
   - Painel "Rastreamento"
   - Ajuste parâmetros (threshold, tamanho mínimo/máximo do objeto)
   - Clique em "Iniciar Rastreamento"

5. **Executar análises**:
   - Painel "Análises"
   - Selecione análises desejadas (ex: "Velocidade", "Distância Percorrida")
   - Clique em "Executar"

6. **Exportar resultados**:
   - Painel "Relatórios"
   - Clique direito no gráfico > "Export CSV"
   - Salve trajetórias: Menu `Arquivo > Exportar Trajetórias` (NDJSON)

### 2. Reutilizar Configuração

Para aplicar as mesmas configurações em novos vídeos:

1. Salve o experimento: `Arquivo > Salvar Experimento`
2. Para novo vídeo: `Arquivo > Abrir Experimento` (carrega zonas, calibração, análises)
3. Apenas troque o vídeo: `Arquivo > Abrir Vídeo`

### 3. Personalizar Pipeline de Processamento

Edite `MouseGlob/src/resource/pipelines/default.json`:

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

Parâmetros ajustáveis:
- `alpha`: taxa de atualização do fundo (0.01-0.1)
- `blockSize`: tamanho da janela para threshold adaptativo (ímpar, ex: 11, 15, 21)
- `c`: constante de ajuste do threshold (-20 a 20)
- `kernel`: tamanho do kernel morfológico (3x3, 5x5, 7x7)

## 🏗️ Arquitetura

MouseGlob utiliza uma arquitetura modular em camadas:

```
┌────────────────────────────────────────┐
│     UI Layer (Swing + Processing)      │  Interface gráfica e visualização
├────────────────────────────────────────┤
│   Business Logic / Controllers         │  Orquestração de funcionalidades
├────────────────────────────────────────┤
│   Domain Models / Events               │  Modelos de domínio e eventos
├────────────────────────────────────────┤
│   I/O & Persistence                    │  Leitura/escrita de dados
├────────────────────────────────────────┤
│   Infrastructure (DI, Logging)         │  Serviços transversais
└────────────────────────────────────────┘
```

### Módulos Principais

- **MouseGlob**: Aplicação principal (UI, análises, rastreamento)
- **Injection**: Framework de injeção de dependências customizado

### Padrões de Design

- **Event-Driven Architecture**: Comunicação via eventos e listeners
- **Pipeline Pattern**: Processamento modular e configurável de frames
- **Service Provider Interface (SPI)**: Sistema de plugins extensível
- **Module-View-Controller**: Separação clara de responsabilidades

Para detalhes técnicos completos, consulte [CLAUDE.md](CLAUDE.md).

## 🤝 Contribuição

Contribuições são muito bem-vindas! Veja como contribuir:

### Reportar Bugs

Abra uma issue no GitHub com:
- Descrição clara do problema
- Passos para reproduzir
- Logs relevantes (em `~/.mouseglob/logs/`)
- Versão do Java e sistema operacional

### Sugerir Funcionalidades

Crie uma issue descrevendo:
- Caso de uso científico
- Funcionalidade desejada
- Benefícios esperados

### Contribuir com Código

1. Fork o repositório
2. Crie uma branch: `git checkout -b minha-feature`
3. Implemente com testes
4. Commit: `git commit -m "feat: adiciona análise XYZ"`
5. Push: `git push origin minha-feature`
6. Abra um Pull Request

### Desenvolver Plugins

Crie análises customizadas sem modificar o código-fonte:

1. Implemente `dcc.mouseglob.analysis.Analysis`
2. Anote com `@AnalysisInfo`
3. Registre via `META-INF/services/dcc.mouseglob.analysis.spi.AnalysisProvider`
4. Publique como biblioteca separada

Veja [CLAUDE.md](CLAUDE.md) para guia detalhado de desenvolvimento.

## 📚 Documentação Técnica

- **[CLAUDE.md](CLAUDE.md)**: Documentação completa para desenvolvedores (arquitetura, APIs, guias técnicos)
- **[revision.md](revision.md)**: Histórico de decisões de design e melhorias planejadas
- **[TODO.txt](TODO.txt)**: Lista de tarefas e roadmap

### Documentação de APIs

- [JSON Schemas](MouseGlob/src/resource/schemas/): Especificação de formatos de dados
  - `trajectory.schema.json`: Formato de trajetórias NDJSON
  - `experiment.schema.json`: Metadados de experimento

### Guias Específicos

- [Pipeline de Processamento](CLAUDE.md#pipeline-de-processamento)
- [Sistema de Plugins](CLAUDE.md#sistema-de-plugins)
- [Formatos de Exportação](CLAUDE.md#formatos-de-exportação)
- [CI/CD e Builds](CLAUDE.md#cicd)

## 📄 Licença

Este projeto é distribuído sob a licença GNU General Public License v3.0. Veja [license.txt](license.txt) para o texto completo da licença.

**Resumo da GPLv3:**
- ✅ Uso comercial permitido
- ✅ Modificação permitida
- ✅ Distribuição permitida
- ✅ Uso de patentes permitido
- ⚠️ Modificações devem ser disponibilizadas sob a mesma licença (copyleft)
- ⚠️ Código-fonte deve ser disponibilizado
- ⚠️ Mudanças devem ser documentadas

## 🙏 Agradecimentos

MouseGlob utiliza as seguintes bibliotecas open-source:

- [Processing](https://processing.org/) - Visualização e renderização
- [JavaCV](https://github.com/bytedeco/javacv) - Processamento de vídeo (FFmpeg/OpenCV)
- [FlatLaf](https://www.formdev.com/flatlaf/) - Look and Feel moderno
- [Jackson](https://github.com/FasterXML/jackson) - Serialização JSON
- [SLF4J](https://www.slf4j.org/) / [Logback](https://logback.qos.ch/) - Logging

## 📞 Contato e Suporte

- **Issues**: [GitHub Issues](https://github.com/seu-usuario/mouseglob/issues)
- **Discussões**: [GitHub Discussions](https://github.com/seu-usuario/mouseglob/discussions)
- **Email**: [seu-email@example.com](mailto:seu-email@example.com)

---

**Desenvolvido com ❤️ para a comunidade científica open-source**
