# Refatoração do banco de dados

Objetivo: nomes de colunas que se explicam sozinhos, valores em R$ exatos
(`DECIMAL`/`BigDecimal`) e uma proposta que não precisa de uma coluna nova a
cada veículo.

## ⚠️ Como aplicar (obrigatório)

O schema mudou (colunas renomeadas, tabela nova). Com `ddl-auto=update` o
Hibernate **não renomeia** colunas: deixaria as antigas (NOT NULL) e as inserções
falhariam. Recrie o banco:

1. No Workbench (root), rode `scripts/recriar-banco.sql`.
2. `mvn test` e depois suba a aplicação. O `DataInitializer` recria usuários
   (`admin`/`admin123`, `operador`/`op123`), configuração, produtos, clientes e cidades.

## 1. Nomes de colunas

| Tabela | Antes | Depois | Motivo |
|---|---|---|---|
| pedidos | `metros_carroceria` | `comprimento_ocupado_m` | unidade e significado |
| pedidos | `metros_com_margem_nvia` | `comprimento_margem_nvia_m` | idem |
| pedidos | `metros_com_margem_venda` | `comprimento_margem_venda_m` | idem |
| pedidos | `volume_totalm3` | `volume_total_m3` | nome colado |
| pedidos | `cidade_id` | `cidade_destino_id` | a FK era ambígua |
| pedidos / propostas / configuracao_frete | `criado_por`, `gerada_por`, `atualizado_por` | `*_por_id` | padrão único de FK (`_id`) |
| itens_pedido | `volumem3` | `volume_ajustado_m3` | é o volume já com o fator da categoria |
| produtos | `fator_qtdm3` | `pecas_por_m3` | é "peças por m³" (a tela já mostrava "pç/m³") |
| produtos | `comprimento`, `largura`, `altura` | `comprimento_m`, `largura_m`, `altura_m` | unidade |
| tarifas_frete | `frete_base_km_truck/carreta` | `valor_por_km_truck/carreta` | é R$/km |
| tarifas_frete | `origem_api` | `gerada_via_api` | "origem API" parecia "origin API" |
| configuracao_frete | `descargar_manual_*`, `descargar_empilhadeira` | `custo_descarga_manual_*`, `custo_descarga_empilhadeira` | substantivo, é R$ |
| configuracao_frete | `ad_valorem_default` | `ad_valorem_percent` | era % e não dizia |
| configuracao_frete | `capacidade_carreta_m3` | `volume_util_carreta_m3` | não confundir com a capacidade do enum `TipoVeiculo` (80 m³) |
| configuracao_frete | `fator_metros_carroceria` | `comprimento_carroceria_carreta_m` | é o comprimento (12 m) da carreta de referência |
| configuracao_frete | `capacidade_container_m3` | *(removida)* | não era usada em lugar nenhum |

Os nomes das colunas com `M3`/`M` agora são fixados com `@Column(name = ...)`:
o Spring gerava `volume_totalm3` porque não separa o `M` quando depois dele vem
número ou fim de palavra.

## 2. Dinheiro em DECIMAL

Valores em R$ e percentuais são `BigDecimal` no Java e `DECIMAL` no MySQL
(`DECIMAL(12,2)` para reais, `DECIMAL(5,2)` para percentuais). O cálculo em
`FreteService` mantém a **mesma fórmula** de antes, com precisão alta nas contas
intermediárias e arredondamento para centavos só no resultado, então os valores
das cotações não mudam. Utilitário: `util/Dinheiro`.

## 3. Proposta normalizada

Antes, `propostas` tinha uma coluna por veículo (`frete_base_truck`,
`frete_base_carreta`, `pedagio_*`, `descargar_manual_*`, `total_frete_*`).
Agora:

```
propostas       (id, pedido_id UNIQUE, valor_mercadoria, icms_percent, pis_cofins_percent,
                 margem_comercial_percent, ad_valorem_percent, veiculo_escolhido, frete_total,
                 ajuste_manual, justificativa_ajuste, caminho_pdf, gerada_em, gerada_por_id)
proposta_opcoes (id, proposta_id, tipo_veiculo, frete_base, pedagio, custo_descarga,
                 ad_valorem, subtotal, impostos, margem_comercial_valor, total)
                 UNIQUE (proposta_id, tipo_veiculo)
```

`ResultadoFreteDTO` também deixou de ter 24 campos `...Truck/...Carreta`:
agora tem `truck` e `carreta` (`OpcaoFreteDTO`). O PDF monta uma coluna por
opção gravada e passa a mostrar os valores reais de **Impostos** e **Margem
Comercial** (antes eram "—", marcados como "simplificado" no código).

## 4. Outras mudanças

- Índices em `pedidos` (`status`, `criado_em`, `numero_pedido`).
- `application.properties`: removidas as chaves duplicadas (url/usuário/senha
  apareciam duas vezes); usuário e senha aceitam `DB_USERNAME`/`DB_PASSWORD`.
- `SecurityConfig`: removidas as referências ao `/h2-console`.

## 5. Bugs que encontrei e corrigi

1. **Ad valorem sumia na proposta final.** `proposta.html` enviava
   `valorMercadoria` fixo em `0`; o PDF era recalculado sem ad valorem mesmo que a
   prévia tivesse. Agora envia o valor informado.
2. **`propostas.valor_mercadoria` ficava sempre 0**: ninguém o preenchia. Agora é gravado.

## 6. Pontos que NÃO alterei (decisão sua)

- **Ajuste manual não entra no total.** `frete_total` é o total do veículo
  escolhido; o ajuste é só gravado e mostrado numa nota no PDF. Se a intenção é
  somá-lo, é uma linha em `PedidoService.gerarProposta`.
- **Veículos sem preço usam o de Truck.** O seletor oferece VUC, Toco, Truck,
  Carreta e Bi-trem, mas só existem preços de Truck e Carreta: Carreta usa o de
  carreta e **todo o resto (inclusive Bi-trem) usa o de Truck**. Regra
  preservada em `ResultadoFreteDTO.opcaoPara`.
- **Duas "capacidades" de carreta:** 80 m³ no enum (sugere o veículo) e 60 m³ na
  configuração (converte m³ em metros). São usos diferentes e agora têm nomes
  diferentes, mas continuam duas fontes.
- **O que é "NVIA"?** Mantive o termo (`comprimento_margem_nvia_m`, +10%) porque
  não sei o significado; se for sigla de algo, vale um nome melhor.
- `pedidos.pdf_original` nunca é preenchido (campo sem uso).

## 7. Adiado de propósito

- **Flyway.** Exigiria um SQL inicial idêntico ao que o Hibernate espera
  (tipos ENUM/BIT/DECIMAL) e eu não consigo testá-lo contra o seu MySQL. Fica
  como próximo passo, já com o schema estabilizado.
- **Tabelas `veiculos` e `categorias_produto`** (tirar capacidades e fatores
  do código). Só valem a pena junto de uma tela de administração; sem ela, editar
  esses dados no banco seria pior que no enum.
- **Tarifa por veículo** (`tarifa_veiculo`). A tela de cidades tem campos fixos
  de Truck/Carreta; só compensa quando houver um terceiro veículo precificado.

## Arquivos alterados

models (`ConfiguracaoFrete`, `TarifaFrete`, `Produto`, `ItemPedido`, `Pedido`,
`Proposta`, **novo** `PropostaOpcao`), DTOs (`ResultadoFreteDTO`,
`ResultadoVolumetriaDTO`, `CidadeTarifaView`, **novo** `OpcaoFreteDTO`),
**novo** `util/Dinheiro`, services (`FreteService`, `RoteirizacaoService`,
`VolumetriaService`, `PedidoService`, `PropostaGeracaoService`), controllers
(`AdminController`, `PedidoController`), `DataInitializer`, `SecurityConfig`,
templates (`admin/cidades|configuracao|produtos`, `pedidos/detalhe|frete|proposta`),
testes (`FreteServiceTest`, `VolumetriaServiceTest`) e `application.properties`.
