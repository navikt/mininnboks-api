package no.nav.sbl.dialogarena.mininnboks.consumer

import lombok.SneakyThrows
import org.apache.cxf.helpers.FileUtils
import java.io.File
import java.net.URL
import java.util.*
import java.util.regex.Pattern

class TekstServiceImpl : TekstService {
    private val tekster: MutableMap<String?, String?> = HashMap()

    @SneakyThrows
    private fun lastTekster() {
        val resources = TekstServiceImpl::class.java.classLoader.getResources("tekster/mininnboks")
        Collections.list(resources).stream().map { obj: URL -> obj.file }.map { pathname: String? -> File(pathname) }.forEach { file: File -> this.lastTekster(file) }
    }

    @SneakyThrows
    private fun lastTekster(file: File) {
        if (file.isDirectory) {
            Arrays.stream(file.listFiles()).forEach { file: File -> this.lastTekster(file) }
        } else {
            tekster[finnKey(file)] = FileUtils.getStringFromFile(file).trim { it <= ' ' }
        }
    }

    private fun finnKey(tekstFil: File): String {
        val fileName = tekstFil.name
        val matcher = Pattern.compile("(.*)_nb.(txt|html)").matcher(fileName)
        check(matcher.find()) { fileName }
        return matcher.group(1)
    }

    override fun hentTekst(key: String?): String? {
        return Optional.ofNullable(tekster[key]).orElseThrow { MissingResourceException("mangler tekst for key=$key", key, key) }
    }

    override fun hentTekster(): Map<String?, String?>? {
        return tekster
    }

    init {
        lastTekster()
    }
}
