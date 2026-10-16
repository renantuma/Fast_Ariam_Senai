package com.fastariam.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utilitários para cálculo com valores monetários e percentuais.
 *
 * <p>Valores em R$ usam {@link BigDecimal} (e não {@code double}) para evitar
 * erros de ponto flutuante — por exemplo, {@code 0.1 + 0.2 != 0.3} em double.
 * No banco, esses valores ficam em colunas {@code DECIMAL}.</p>
 */
public final class Dinheiro {

    /** Casas decimais de um valor em reais (centavos). */
    public static final int ESCALA_REAIS = 2;

    /** Casas decimais usadas nas contas intermediárias (arredonda-se só no final). */
    public static final int ESCALA_CALCULO = 10;

    public static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_UP;

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private Dinheiro() {}

    /** Converte um {@code double} em reais com 2 casas (ex.: 4.8 → 4.80). */
    public static BigDecimal reais(double valor) {
        return BigDecimal.valueOf(valor).setScale(ESCALA_REAIS, ARREDONDAMENTO);
    }

    /** Arredonda para centavos (2 casas, HALF_UP). */
    public static BigDecimal arredondar(BigDecimal valor) {
        return valor.setScale(ESCALA_REAIS, ARREDONDAMENTO);
    }

    /** Converte um percentual em fator multiplicável (ex.: 9.25 → 0.0925). */
    public static BigDecimal fator(BigDecimal percentual) {
        return percentual.divide(CEM, ESCALA_CALCULO, ARREDONDAMENTO);
    }
}
