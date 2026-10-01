# SONUS Edit 0.1.0 / code01 — estrutura funcional

## Objetivo desta entrega

Primeira implementação do fluxo de criação automática vertical do SONUS Edit.
O foco desta code01 é estabilizar seleção, regras, preferências e preparação do projeto antes do compilador multi-imagem.

## Fluxo

1. **Música**
   - seleção real por Android Storage Access Framework;
   - leitura de título, artista e duração;
   - predefinições: 15s, 30s, 40s, 50s, 60s, 65s, 1m30, 2m, 3m, 5m e 10m;
   - início do trecho por slider;
   - ajuste fino -10s, -5s, -1s, +1s, +5s e +10s;
   - preview do trecho com Media3/ExoPlayer;
   - presets maiores que a música ficam indisponíveis.

2. **Assinatura**
   - texto/nome de usuário;
   - imagem de perfil/logo opcional;
   - ao menos texto ou imagem é obrigatório para avançar;
   - persistência via DataStore quando `Salvar como padrão` estiver ativo;
   - URI persistível quando o provider Android permitir.

3. **Imagens**
   - seleção múltipla real pela galeria;
   - mínimo de 5 segundos por imagem;
   - limite calculado por `duração / 5`;
   - exemplos: 15s=3, 30s=6, 65s=13, 10min=120;
   - a interface apresenta até 20 miniaturas simultaneamente; seleções maiores continuam pertencendo ao projeto;
   - ordem de seleção = ordem inicial do vídeo;
   - projeto marcado como 9:16;
   - especificação de preenchimento vertical sem barras pretas armazenada no draft;
   - fade in/out automático armazenado no draft.

4. **Estilo**
   - 10 presets visuais: Padrão, Moderna, Elegante, Clássica, Minimal, Impacto, Manuscrita, Retrô, Neon e Editorial;
   - aparência correspondente à direção visual aprovada, usando famílias seguras do sistema nesta code01;
   - persistência da escolha via DataStore;
   - preset de efeito atual preservado no draft.

5. **Revisão**
   - música, trecho, duração, assinatura, imagens, fonte, formato e automações exibidos antes da criação;
   - gera `CreateProjectDraft` único e abre o shell do editor manual.

## Regras fixas

- formato inicial: 9:16;
- mínimo por imagem: 5s;
- preenchimento: zoom/crop automático para não gerar barras pretas;
- transição padrão: fade in 350ms + fade out 350ms;
- assinatura e fonte podem ser memorizadas;
- projeto automático e editor manual usam o mesmo draft, sem motor paralelo.

## O que ainda não deve ser declarado pronto

O starter original possui somente exportação Media3 de asset único. Portanto esta code01 **não declara** como concluídos:

- compilador Media3 multi-imagem;
- aplicação renderizada do crop/zoom em cada imagem;
- aplicação renderizada de fade entre itens;
- exportação final do projeto automático;
- timeline multi-track real;
- bridge backend do SONUS Stream.

Essas funções devem consumir o `CreateProjectDraft` desta code01 na próxima etapa, sem redesenhar o fluxo.
