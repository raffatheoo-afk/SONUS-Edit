# SONUS Edit — Roadmap de implementação

## Fase 0 — Bootstrap (este pacote)
- [x] Projeto Android independente
- [x] Identidade/package
- [x] Tema e Home inicial
- [x] Editor shell
- [x] Custom deep link SONUS -> Edit
- [x] Android Share -> Edit
- [x] Contrato de integração
- [x] LyricsProvider
- [x] Cliente LRCLIB inicial
- [x] Parser LRC
- [x] Recorte/deslocamento de timestamps
- [x] Engine boundary Media3

## Fase 1 — Projeto real
- [ ] Media picker
- [ ] Metadados de áudio/vídeo
- [ ] Criar/abrir/salvar projeto
- [ ] Autosave
- [ ] Biblioteca de projetos
- [ ] Undo/redo
- [ ] Cache e limpeza

## Fase 2 — Letras automáticas
- [ ] Auto lookup ao escolher música
- [ ] Score de correspondência título/artista/duração
- [ ] UI de lupa/resultados
- [ ] Preview da letra
- [ ] Cache do match
- [ ] Estilos lyric
- [ ] Importar LRC
- [ ] Editor manual
- [ ] Word-sync quando disponível

## Fase 3 — Timeline completa
- [ ] Multi-track
- [ ] Trim/split
- [ ] Reorder
- [ ] Pinch zoom
- [ ] Snap
- [ ] Waveform
- [ ] Texto/fontes
- [ ] Overlays
- [ ] Canvas 9:16 / 16:9 / 1:1 / 4:5

## Fase 4 — Visual
- [ ] Filtros
- [ ] Ajustes de imagem
- [ ] Transições
- [ ] Animações de entrada/saída
- [ ] Keyframes
- [ ] Visualizers
- [ ] Background/blur
- [ ] Presets

## Fase 5 — Auto Edit
- [ ] Beat/onset analysis
- [ ] Seleção de cortes por ritmo
- [ ] Auto crop/reframe
- [ ] Lyric Video recipe
- [ ] Beat Edit recipe
- [ ] Cinematic recipe
- [ ] Visualizer recipe
- [ ] Short/Reel recipe
- [ ] Intensidade do automático

## Fase 6 — Exportação
- [ ] Media3 Composition compiler
- [ ] Preview engine
- [ ] Export cancelável
- [ ] Progresso real
- [ ] Background/foreground policy
- [ ] 720p / 1080p / 2K / 4K
- [ ] 24 / 30 / 60 fps quando suportado
- [ ] Preservar HDR quando suportado / fallback SDR
- [ ] Compartilhar resultado

## Fase 7 — SONUS Bridge real
- [ ] Botão Criar no SONUS Edit no SONUS principal (somente quando autorizado)
- [ ] Backend `getEditTrack`
- [ ] Metadados por trackId
- [ ] Acesso temporário ao áudio
- [ ] Capa/artista/título automáticos
- [ ] Retorno Abrir no SONUS
- [ ] Android App Links quando houver domínio

## Regra de qualidade
Cada fase precisa de teste real em aparelho antes de ser chamada de concluída.

## Entrega 0.1.0 / code01 — novo fluxo automático
- [x] Wizard separado em Música / Assinatura / Imagens / Estilo / Revisão
- [x] Media picker de áudio e imagens
- [x] Leitura básica de metadados de áudio
- [x] Preview do trecho com ExoPlayer
- [x] Presets de 15s até 10min
- [x] Ajuste fino ±1s / ±5s / ±10s
- [x] Assinatura e fonte salvas via DataStore
- [x] Limite de imagens por mínimo de 5s
- [x] Projeto fixado inicialmente em 9:16
- [x] Draft registra auto-fill vertical e fades
- [x] 10 presets visuais de fonte
- [x] Revisão final do projeto
- [x] Testes unitários da distribuição
- [ ] Compilador Media3 multi-imagem consumindo CreateProjectDraft
- [ ] Render real do auto-fill/crop 9:16
- [ ] Render real de fade in/out
- [ ] Export final do projeto automático
