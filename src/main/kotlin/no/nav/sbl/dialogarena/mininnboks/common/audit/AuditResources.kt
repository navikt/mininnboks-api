package no.nav.sbl.dialogarena.mininnboks.common.audit

class AuditResources {
    companion object {
        val Journalpost = Audit.AuditResource("dokument.journalpost")
        val Dokument = Audit.AuditResource("dokument.dokument")

        val Henvendelse = Audit.AuditResource("henvendelse")
        val Les = Audit.AuditResource("henvendelse.les")
    }
}
