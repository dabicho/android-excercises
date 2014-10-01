package mx.org.dabicho.dabichoquiz.model;

/**
 * Clase para indicar las preguntas y respuestas correctas
 */
public class TrueFalse
{
    /**
     * id de la pregunta
    */
    private int mQuestion;
    /**
     * Si la pregunta es cierta o falsa
     */
    private boolean mTrueQuestion;

    public TrueFalse(int question, boolean trueQuestion) {
        mQuestion=question;
        mTrueQuestion=trueQuestion;
    }

    public int getQuestion() {
        return mQuestion;
    }

    public void setQuestion(int question) {
        mQuestion = question;
    }

    public boolean isTrueQuestion() {
        return mTrueQuestion;
    }

    public void setTrueQuestion(boolean trueQuestion) {
        mTrueQuestion = trueQuestion;
    }
}
