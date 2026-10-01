# SONUS <-> SONUS Edit — contrato inicial

## Objetivo

Permitir que uma faixa do SONUS Stream abra diretamente um projeto no SONUS Edit sem fundir os dois APKs.

## Pacotes

- SONUS: `com.sonusstudios.streampulse`
- SONUS Edit: `com.sonusstudios.sonusedit`

## Deep link v1

```text
sonusedit://create?source=sonus&trackId=<TRACK_ID>&startMs=<OPTIONAL>&durationMs=<OPTIONAL>
```

Obrigatório:
- `trackId`

Opcionais:
- `startMs`
- `durationMs`

## Não transportar no link

- token Firebase
- token pCloud
- URL privada/permanente do áudio
- senha
- e-mail
- UID como prova de autorização
- assinatura secreta

## Fluxo

1. SONUS chama explicitamente o package do SONUS Edit.
2. SONUS Edit valida URI e `trackId`.
3. Cria um projeto provisório com `source = SONUS_STREAM`.
4. `SonusBridgeRepository` busca metadados no backend.
5. Backend retorna somente o necessário.
6. Se o áudio exigir autorização, backend emite URL temporária/assinada.
7. Editor carrega faixa e procura letra sincronizada.
8. Usuário edita/exporta.

## App não instalado

No primeiro release conjunto, o SONUS deve capturar `ActivityNotFoundException` e oferecer instalar/abrir a página oficial do SONUS Edit. A URL de instalação será definida quando existir distribuição real.

## App Links HTTPS

Não ativar até existir domínio próprio controlado.

Quando existir:
- adicionar `android:autoVerify=true`;
- publicar `/.well-known/assetlinks.json`;
- usar certificado de assinatura de release;
- manter o custom scheme como compatibilidade interna se desejado.

## Compatibilidade de contrato

Reservar parâmetro `v=1` quando o contrato começar a evoluir. O app deve ignorar query params desconhecidos para manter compatibilidade futura.
