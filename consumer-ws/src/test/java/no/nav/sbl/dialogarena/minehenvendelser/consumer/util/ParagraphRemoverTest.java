package no.nav.sbl.dialogarena.minehenvendelser.consumer.util;

import org.junit.Assert;
import org.junit.Test;

public class ParagraphRemoverTest {

    @Test
    public void shouldRemoveParagraphTags() {
        String test = "<p>TEST</p>";
        String result = ParagraphRemover.remove(test);
        Assert.assertEquals("TEST", result);
    }

    @Test
    public void shouldNotRemoveOtherTags() {
        String test = "<a>TEST</a>";
        String result = ParagraphRemover.remove(test);
        Assert.assertEquals("<a>TEST</a>", result);
    }

    @Test
    public void shouldNotAlterPlainText() {
        String test = "TEST";
        String result = ParagraphRemover.remove(test);
        Assert.assertEquals("TEST", result);
    }

    @Test
    public void shouldRemoveStartingParagraphTags() {
        String test = "<p>TEST";
        String result = ParagraphRemover.remove(test);
        Assert.assertEquals("TEST", result);
    }

    @Test
    public void shouldRemoveEndingParagraphTags() {
        String test = "TEST</p>";
        String result = ParagraphRemover.remove(test);
        Assert.assertEquals("TEST", result);
    }
    
    @Test
    public void shouldWorkWithNullString(){
        String test = null;
        String result = ParagraphRemover.remove(test);
        Assert.assertEquals(null, result);
    }
    
    @Test
    public void shouldWorkWithEmptyString(){
        String test = "";
        String result = ParagraphRemover.remove(test);
        Assert.assertEquals("", result);
    }
}