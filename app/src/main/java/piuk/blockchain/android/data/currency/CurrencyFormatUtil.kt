package piuk.blockchain.android.data.currency

import piuk.blockchain.android.util.annotations.Mockable
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * This class allows us to format decimal values for clean UI display.
 */
@Mockable
class CurrencyFormatUtil {

    private lateinit var btcFormat: DecimalFormat
    private lateinit var ethFormat: DecimalFormat
    private lateinit var fiatFormat: DecimalFormat
    private lateinit var ethShortFormat: DecimalFormat

    private val btcUnit = CryptoCurrencies.BTC.symbol
    private val bchUnit = CryptoCurrencies.BCH.symbol
    private val ethUnit = CryptoCurrencies.ETHER.symbol

    private val maxEthShortDecimalLength = 8
    private val maxBtcDecimalLength = 8
    private val maxEthDecimalLength = 18

    init {
        val defaultLocale = Locale.getDefault()

        fiatFormat = (NumberFormat.getInstance(defaultLocale) as DecimalFormat).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }

        btcFormat = (NumberFormat.getInstance(defaultLocale) as DecimalFormat).apply {
            minimumFractionDigits = 1
            maximumFractionDigits = maxBtcDecimalLength
        }

        ethFormat = (NumberFormat.getInstance(defaultLocale) as DecimalFormat).apply {
            minimumFractionDigits = 1
            maximumFractionDigits = maxEthDecimalLength
        }

        ethShortFormat = (NumberFormat.getInstance(defaultLocale) as DecimalFormat).apply {
            minimumFractionDigits = 1
            maximumFractionDigits = maxEthShortDecimalLength
        }
    }

    fun getBtcUnit() = btcUnit

    fun getBchUnit() = bchUnit

    fun getEthUnit() = ethUnit

    fun getBtcMaxFractionDigits() = maxBtcDecimalLength

    fun getBchMaxFractionDigits() = maxBtcDecimalLength

    fun getEthMaxFractionDigits() = maxEthDecimalLength

    fun formatFiat(fiatBalance: BigDecimal, fiatUnit: String): String {
        return getFiatFormat(fiatUnit).format(fiatBalance)
    }

    fun formatFiatWithSymbol(fiatValue: Double, currencyCode: String, locale: Locale): String {
        val numberFormat = NumberFormat.getCurrencyInstance(locale)
        val decimalFormatSymbols = (numberFormat as DecimalFormat).decimalFormatSymbols
        numberFormat.decimalFormatSymbols = decimalFormatSymbols.apply {
            this.currencySymbol = Currency.getInstance(currencyCode).getSymbol(locale)
        }
        return numberFormat.format(fiatValue)
    }

    fun getFiatSymbol(currencyCode: String, locale: Locale): String =
            Currency.getInstance(currencyCode).getSymbol(locale)

    fun formatBtc(btc: BigDecimal): String {
        val amountFormatted = btcFormat.format(btc.toNaturalNumber()).toWebZero()
        return "$amountFormatted"
    }

    fun formatSatoshi(satoshi: Long): String {
        val amountFormatted = btcFormat.format(satoshi.div(BTC_DEC).toNaturalNumber()).toWebZero()
        return "$amountFormatted"
    }

    fun formatBch(bch: BigDecimal): String {
        return formatBtc(bch)
    }

    fun formatEth(eth: BigDecimal): String {
        val amountFormatted = ethFormat.format(eth.toNaturalNumber()).toWebZero()
        return "$amountFormatted"
    }

    fun formatEthShort(eth: BigDecimal): String {
        val amountFormatted = ethShortFormat.format(eth.toNaturalNumber()).toWebZero()
        return "$amountFormatted"
    }

    fun formatWei(wei: Long): String {
        val amountFormatted = ethFormat.format(wei.div(ETH_DEC).toNaturalNumber()).toWebZero()
        return "$amountFormatted"
    }

    fun formatBtcWithUnit(btc: BigDecimal): String {
        val amountFormatted = btcFormat.format(btc.toNaturalNumber()).toWebZero()
        return "$amountFormatted ${btcUnit}"
    }

    fun formatBchWithUnit(bch: BigDecimal): String {
        val amountFormatted = btcFormat.format(bch.toNaturalNumber()).toWebZero()
        return "$amountFormatted ${bchUnit}"
    }

    fun formatEthWithUnit(eth: BigDecimal): String {
        val amountFormatted = ethFormat.format(eth.toNaturalNumber()).toWebZero()
        return "$amountFormatted ${ethUnit}"
    }

    fun formatEthShortWithUnit(eth: BigDecimal): String {
        val amountFormatted = ethShortFormat.format(eth.toNaturalNumber()).toWebZero()
        return "$amountFormatted ${ethUnit}"
    }

    fun formatWeiWithUnit(wei: Long): String {
        val amountFormatted = ethFormat.format(wei.div(ETH_DEC).toNaturalNumber()).toWebZero()
        return "$amountFormatted ${ethUnit}"
    }

    /**
     * Returns the Fiat format as a [NumberFormat] object for a given currency code.
     *
     * @param fiat The currency code (ie USD) for the format you wish to return
     * @return A [NumberFormat] object with the correct decimal fractions for the chosen Fiat format
     * @see ExchangeRateFactory.getCurrencyLabels
     */
    //TODO This should be private but is exposed for CurrencyFormatManager for now until usage removed
    fun getFiatFormat(currencyCode: String) = fiatFormat.apply { currency = Currency.getInstance(currencyCode) }

    companion object {
        private const val BTC_DEC = 1e8
        private const val ETH_DEC = 1e18
    }
}

private fun BigDecimal.toNaturalNumber() = Math.max(this.toDouble(), 0.0)

private fun Double.toNaturalNumber() = Math.max(this, 0.0)

// Replace 0.0 with 0 to match web
private fun String.toWebZero() =
        if (this.equals("0.0") || this.equals("0.00"))
            "0"
        else
            this