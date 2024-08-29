import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Question Class
class Question {
    private String questionText;
    private List<String> options;
    private int correctAnswerIndex;

    public Question(String questionText, List<String> options, int correctAnswerIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
}

// QuizTimer Class
class QuizTimer {
    private ScheduledExecutorService executor;

    public QuizTimer(int seconds, Runnable onTimeout) {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(onTimeout, seconds, TimeUnit.SECONDS);
    }

    public void cancel() {
        executor.shutdownNow();
    }
}

// QuizUI Class
class QuizUI {
    public int displayQuestion(Question question) {
        String[] options = question.getOptions().toArray(new String[0]);
        return JOptionPane.showOptionDialog(null, 
            question.getQuestionText(), 
            "Quiz", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.INFORMATION_MESSAGE, 
            null, 
            options, 
            options[0]);
    }
}

// QuizGame Class
class QuizGame {
    private int score = 0;
    private int totalQuestions = 0;

    public void handleAnswerSubmission(int selectedOption, Question question) {
        if (selectedOption == question.getCorrectAnswerIndex()) {
            score++;
        }
        totalQuestions++;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }
}

// QuizResult Class
class QuizResult {
    public void displayResult(int score, int totalQuestions) {
        JOptionPane.showMessageDialog(null, 
            "Quiz Complete!\nScore: " + score + "/" + totalQuestions, 
            "Result", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}

// Main Class
public class Main {
    public static void main(String[] args) {
        // Initialize quiz questions
        Question q1 = new Question("What is the capital of France?", 
                                   Arrays.asList("Berlin", "Madrid", "Paris", "Rome"), 
                                   2);
        Question q2 = new Question("What is 2 + 2?", 
                                   Arrays.asList("3", "4", "5", "6"), 
                                   1);
        List<Question> questions = Arrays.asList(q1, q2);

        QuizGame quizGame = new QuizGame();
        QuizUI quizUI = new QuizUI();

        for (Question question : questions) {
            final boolean[] answered = {false};

            // Timer to handle timeout
            QuizTimer timer = new QuizTimer(10, () -> {
                if (!answered[0]) {
                    JOptionPane.showMessageDialog(null, "Time's up!");
                    quizGame.handleAnswerSubmission(-1, question); // Mark as unanswered
                }
            });

            // Display question and get answer
            int selectedOption = quizUI.displayQuestion(question);
            answered[0] = true;
            timer.cancel();

            // Handle answer
            quizGame.handleAnswerSubmission(selectedOption, question);
        }

        // Display final result
        QuizResult quizResult = new QuizResult();
        quizResult.displayResult(quizGame.getScore(), quizGame.getTotalQuestions());
    }
}