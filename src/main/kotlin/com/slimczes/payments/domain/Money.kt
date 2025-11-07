package com.slimczes.payments.domain

import java.math.BigDecimal

data class Money(val amount: BigDecimal, val currency: String) {
}