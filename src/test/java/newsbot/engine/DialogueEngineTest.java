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
    void isLikeCategoryInputHeuristics() {
        DialogueEngine e = new DialogueEngine();
        assertTrue(e.isLikeCategoryInput("спорт, экономика"));
        assertTrue(e.isLikeCategoryInput("технологии"));
        assertFalse(e.isLikeCategoryInput("\\news add спорт"));
        assertFalse(e.isLikeCategoryInput(""));
        assertFalse(e.isLikeCategoryInput("   "));
    }
}
