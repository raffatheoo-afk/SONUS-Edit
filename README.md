# SONUS Edit — 0.1.0 / code01

Projeto Android independente: `com.sonusstudios.sonusedit`.

## Implementado nesta code01

- logo SONUS Edit integrado à Home e ao fluxo;
- fluxo em 5 abas: Música, Assinatura, Imagens, Estilo e Revisão;
- seleção de áudio local + leitura de metadados;
- preview de trecho com Media3/ExoPlayer;
- durações de 15s até 10min;
- ajuste fino ±1s, ±5s e ±10s;
- assinatura com texto/foto e preferência salva;
- seleção múltipla de imagens;
- regra de mínimo 5s por imagem e limite automático por duração;
- formato inicial fixo 9:16;
- flags de auto-fill vertical e fade in/out no draft;
- 10 presets de fonte aprovados e preferência salva;
- revisão final antes do editor;
- testes unitários da distribuição de imagens;
- deep link e Android Share preservados;
- LRCLIB/parser/Media3 boundary preservados.

## Limite conhecido desta code01

A exportação multi-imagem ainda não está ligada ao `CreateProjectDraft`. O botão de exportação do shell manual permanece desabilitado para não apresentar uma função incompleta como pronta.

## Codemagic

Workflow: `sonus-edit-0-1-0-code01-debug`.

O build valida versão/package, executa testes unitários, gera o APK debug e publica o SHA-256 do APK como artifact.
