package newsbot.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DialogueEngineTest {

    @Test
    void onboardingTextIsNotEmpty() {
        DialogueEngine e = new DialogueEngine();
        String text = e.onboardText();
        assertNotNull(text);
        assertFalse(text.isBlank());
        assertTrue(text.toLowerCase().contains("новостной"));
    }

    @Test
    void helpDetectionWorks() {
        DialogueEngine e = new DialogueEngine();
        assertTrue(e.isHelp("\\help"));
        assertTrue(e.isHelp("\\HELP"));
        assertFalse(e.isHelp("\\help me"));
        assertFalse(e.isHelp("help"));
    }

    @Test
    void looksLikeCategoryInputHeuristics() {
        DialogueEngine e = new DialogueEngine();
        assertTrue(e.looksLikeCategoryInput("спорт, экономика"));
        assertTrue(e.looksLikeCategoryInput("технологии"));
        assertFalse(e.looksLikeCategoryInput("\\news add спорт"));
        assertFalse(e.looksLikeCategoryInput(""));
        assertFalse(e.looksLikeCategoryInput("   "));
    }
}
