# SONUS Edit — Arquitetura inicial

## 1. Identidade

- Produto: SONUS Edit
- Tipo: editor de vídeos musicais Android independente
- Package: `com.sonusstudios.sonusedit`
- Base inicial: 0.1.0 / code1
- SONUS principal permanece independente.
- O Edit existente dentro do SONUS continua simples; este projeto é a experiência completa.

## 2. Princípio do produto

O diferencial não é ser "mais um CapCut". A primeira experiência deve ser automática:

**música + mídias + estilo -> vídeo pronto -> refinamento manual opcional.**

O editor manual existe para dar controle, mas a automação deve resolver o primeiro resultado rapidamente.

## 3. Stack

### UI
- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Tema escuro próprio SONUS Edit

### Motor de mídia
- Media3 Transformer como fundação oficial de exportação.
- `Composition` para sequências de vídeo, imagem e áudio.
- Efeitos Media3/OpenGL quando possível.
- FFmpeg apenas como fallback/ferramenta especializada; nunca executar trabalho pesado no Main thread.
- Engine escondida atrás de interfaces para permitir troca sem reescrever a UI.

### Preview
- Camada `PreviewEngine` separada.
- Avaliar `CompositionPlayer` por feature flag enquanto a API permanecer experimental.
- Fallback seguro: ExoPlayer + preview simplificado dos efeitos quando necessário.

### Concorrência
- Nenhuma cópia, decode, waveform, busca de metadados, análise musical ou exportação no Main thread.
- Coroutines + Dispatchers.IO/Default conforme o tipo de tarefa.
- Exportações longas devem ser canceláveis e sobreviver à UI usando serviço/worker apropriado.

## 4. Camadas

### UI / Presentation
- Home
- Projetos
- Templates
- Editor
- Lyrics Search
- Export
- Settings

### Domain
- EditorProject
- Timeline
- Track/Clip
- TextOverlay
- LyricsTrack
- Effect/Filter
- Transition
- ExportPreset
- AutoEditRecipe

### Data
- ProjectRepository
- MediaMetadataRepository
- LyricsRepository
- TemplateRepository
- SonusBridgeRepository
- CacheRepository

### Engine
- Media3ProjectCompiler
- PreviewEngine
- ExportEngine
- BeatAnalysisEngine
- WaveformEngine
- LyricsTimingEngine
- AutoEditEngine

## 5. Timeline alvo

A timeline final deve aceitar múltiplas trilhas:

1. Video principal
2. Imagens/B-roll
3. Overlays/stickers
4. Texto
5. Letra sincronizada
6. Música
7. Voz/efeitos de áudio

Cada item deve possuir início, fim, trim, posição e parâmetros próprios.

## 6. Auto Edit

`AutoEditEngine` recebe:

- faixa de áudio
- trecho escolhido
- fotos/vídeos escolhidos
- formato da tela
- modo/estilo
- intensidade
- letra, quando disponível

E produz uma `EditorProject` comum. O projeto automático NÃO deve usar um formato paralelo; depois de gerado, tudo deve ser editável na mesma timeline manual.

### Modos iniciais

- Lyric Video
- Beat Edit
- Cinematic
- Visualizer
- Short/Reel
- SONUS Artist

## 7. Letras sincronizadas

Ordem de resolução:

1. Letra já vinculada ao projeto/faixa.
2. LRC local.
3. Cache local da busca anterior.
4. Busca automática pelo provider principal.
5. Busca manual por lupa.
6. Fallback externo (ex.: Lyricsify aberto pelo navegador, sem scraping frágil).
7. Importar LRC.
8. Editor/sincronizador manual.

### Provider inicial

`LyricsProvider` -> `LrclibLyricsProvider`.

Busca automática usa:
- título
- artista
- álbum quando disponível
- duração quando disponível

Armazenar o match selecionado em cache local pela identidade da faixa.

### Recorte inteligente

Se o usuário editar apenas 00:45–01:15, `LyricsTimingEngine`:
- seleciona as linhas dentro do trecho;
- desloca timestamps em -45s;
- mantém sync no vídeo exportado.

### Evolução

O modelo deve aceitar:
- line-sync (LRC)
- word-sync quando o provider fornecer granularidade por palavra
- overlapping vocals no futuro

## 8. Integração SONUS

### Regra
O SONUS não passa URLs privadas nem credenciais para o Edit.

Contrato inicial:

`sonusedit://create?source=sonus&trackId=<id>&startMs=<ms>&durationMs=<ms>`

O SONUS Edit recebe `trackId` e consulta os dados necessários por um backend autorizado.

### Futuro
Criar endpoint específico de integração, por exemplo `getEditTrack`, retornando apenas dados necessários e uma URL temporária/assinada quando aplicável.

### Quando domínio existir
Migrar/duplicar a entrada para Android App Links HTTPS verificados usando `assetlinks.json`.

## 9. Aparência

### Direção
- Fundo preto profundo, não cinza lavado.
- Superfícies grafite com hierarquia clara.
- Violeta elétrico como ação principal.
- Ciano como informação/áudio/sync.
- Vermelho apenas erro/gravação/perigo.
- Ícones vetoriais; não usar emoji como ícone.
- Cantos arredondados, mas sem transformar tudo em cartões gigantes.
- Animações rápidas e discretas.
- Haptic feedback nos pontos importantes da timeline.

### Editor
- Preview domina a parte superior.
- Timeline sempre tem área de gesto grande.
- Ferramentas em rail horizontal inferior.
- Painéis abrem sobre a área inferior sem esconder completamente o preview.
- Playhead de alta visibilidade.
- Waveform legível.
- Zoom de timeline por pinch.

### Tipografia/fontes
- Biblioteca de fontes com busca e favoritos.
- Conjunto inicial de fontes abertas/licenciadas.
- Importação de fonte local em fase posterior.
- Controles: família, peso, tamanho, tracking, linha, alinhamento, caixa, contorno, sombra e fundo.
- Presets específicos de lyric/karaoke.

## 10. Segurança e robustez

- Nunca confiar em URI recebida sem validação.
- Persistable URI permission quando aplicável.
- Projetos devem sobreviver a reinício do app.
- Temporários limpos após export/cancelamento.
- Export nunca deve congelar UI.
- Tratamento de low-memory.
- Preview pode reduzir resolução; export usa resolução final.
- Nenhum segredo em deep links, logs ou BuildConfig público.

## 11. Organização futura recomendada

Começar no módulo `app` para acelerar o bootstrap, mantendo packages por domínio. Quando as interfaces estabilizarem, extrair:

- `:core:model`
- `:core:designsystem`
- `:core:media`
- `:core:lyrics`
- `:core:storage`
- `:core:sonusbridge`
- `:feature:home`
- `:feature:editor`
- `:feature:lyrics`
- `:feature:templates`
- `:feature:export`

Não modularizar por esporte; extrair quando reduzir acoplamento real.
