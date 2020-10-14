package no.nav.sbl.dialogarena.mininnboks.consumer

import java.net.URI
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import java.util.regex.Pattern


object TekstServiceImpl : TekstService {
    private val tekster: MutableMap<String?, String?> = HashMap()

    private fun lastTekster() {
        val uri: URI? = TekstServiceImpl::class.java.classLoader?.getResource("tekster/mininnboks")?.toURI()
        (if (uri?.scheme == "jar") FileSystems.newFileSystem(uri, Collections.emptyMap<String, Any>()) else null).use { fileSystem ->
            val myPath: Path = Paths.get(uri!!)
            Files.walkFileTree(myPath, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    tekster[finnKey(file)] = Files.readString(file).trim { it <= ' ' }
                    return FileVisitResult.CONTINUE
                }
            })
        }
    }

    private fun finnKey(tekstFil: Path): String {
        val fileName = tekstFil.fileName
        val matcher = Pattern.compile("(.*)_nb.(txt|html)").matcher(fileName.toString())
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
