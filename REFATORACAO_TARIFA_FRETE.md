# Refatoração — Tarifa de Frete extraída de Cidade

## O problema

A entidade `Cidade` acumulava responsabilidades que não são dela:

```java
private double icmsPercent;
private double distanciaKm;
private double pedagioTruck;
private double pedagioCarreta;
private double freteBaseKmTruck;
private double freteBaseKmCarreta;
private boolean viaApi;
```

Uma cidade é um lugar — ela tem nome e UF, e nada mais. Distância, pedágio e
frete-base por km descrevem uma **rota/tarifa** a partir da origem (Londrina/PR),
não a cidade. E `icmsPercent` era pior: o ICMS depende do **estado**, então o
valor ficava duplicado em cada cidade (violando DRY), quando já existe a regra
`FreteService.getIcmsEstado(estado)`.

## A solução

**`Cidade`** — agora só identidade geográfica (`id`, `nome`, `estado`), com
constraint único em `(nome, estado)`.

**`TarifaFrete`** (nova entidade) — relação `@OneToOne` com `Cidade`, concentra
`distanciaKm`, `freteBaseKmTruck/Carreta`, `pedagioTruck/Carreta` e `origemApi`
(antigo `viaApi`, renomeado para clareza). É um **modelo rico**: sabe calcular o
frete-base total (`freteBaseTruck()`, `freteBaseCarreta()`) e dizer se já foi
cadastrada (`isCadastrada()`).

**ICMS** — removido das entidades; permanece como regra fiscal derivada do estado
em `FreteService`.

### Arquivos afetados

| Arquivo | Mudança |
|---|---|
| `model/Cidade.java` | Reduzida à identidade (nome, estado) |
| `model/TarifaFrete.java` | **Nova** entidade com os dados de tarifa + comportamento |
| `repository/TarifaFreteRepository.java` | **Novo** repositório |
| `dto/CidadeTarifaView.java` | **Nova** projeção p/ a tela de cidades |
| `service/FreteService.java` | Usa `TarifaFrete`; ICMS derivado do estado |
| `service/RoteirizacaoService.java` | `gerarTarifaViaApi()` cria cidade + tarifa |
| `controller/AdminController.java` | Salva/exclui cidade + tarifa; lista via DTO |
| `config/DataInitializer.java` | Semeia cidade + tarifa (e remove "Maringá" duplicado) |
| `templates/admin/cidades.html` | Campo ICMS removido do form (agora é automático) |

## ⚠️ Migração do banco (IMPORTANTE)

O schema mudou: a tabela `cidades` perdeu colunas, ganhou o índice único
`(nome, estado)` e há uma nova tabela `tarifas_frete`. Com `ddl-auto=update`,
um banco **antigo** entraria em conflito.

**Antes de subir a aplicação, apague o banco H2 para recriá-lo do zero:**

```bash
rm -f data/fastariam.mv.db data/fastariam.trace.db
```

Na primeira execução o `DataInitializer` recria tudo (cidades + tarifas) com o
schema novo. Isso é seguro — os dados são apenas o seed de exemplo.
